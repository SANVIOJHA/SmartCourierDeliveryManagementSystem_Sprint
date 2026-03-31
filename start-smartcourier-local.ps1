param(
    [string]$JavaOpts = ""
)

$root = $PSScriptRoot
$zipkinJar = Join-Path $env:USERPROFILE "tools\\zipkin\\zipkin-server.jar"
$servicePorts = @{
    "eureka-server" = 8761
    "config-server" = 8888
    "auth-service" = 8081
    "delivery-service" = 8082
    "tracking-service" = 8083
    "admin-service" = 8084
    "api-gateway" = 8080
}

function Start-ServiceWindow {
    param(
        [string]$Title,
        [string]$ServiceDir,
        [hashtable]$EnvVars
    )

    $envLines = @()
    foreach ($key in $EnvVars.Keys) {
        $value = $EnvVars[$key]
        $envLines += "`$env:$key='$value'"
    }

    $envScript = $envLines -join "; "
    $javaOptScript = ""
    if ($JavaOpts -and $JavaOpts.Trim().Length -gt 0) {
        $javaOptScript = "; `$env:JAVA_TOOL_OPTIONS='$JavaOpts'"
    }

    $cmd = @"
cd '$ServiceDir'
$envScript$javaOptScript
Write-Host 'Starting $Title ...' -ForegroundColor Cyan
.\mvnw.cmd spring-boot:run
"@

    Start-Process powershell -ArgumentList @(
        "-NoExit",
        "-Command",
        $cmd
    ) | Out-Null
}

function Test-PortOpen {
    param([int]$Port)
    return (Test-NetConnection localhost -Port $Port -WarningAction SilentlyContinue).TcpTestSucceeded
}

function Get-PortOwnerProcess {
    param([int]$Port)

    $connection = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($null -eq $connection) {
        return $null
    }

    return Get-Process -Id $connection.OwningProcess -ErrorAction SilentlyContinue
}

function Stop-SmartCourierProcesses {
    $patterns = @(
        "eureka-server",
        "config-server",
        "auth-service",
        "delivery-service",
        "tracking-service",
        "admin-service",
        "api-gateway"
    )

    $javaProcesses = Get-CimInstance Win32_Process -ErrorAction SilentlyContinue | Where-Object { $_.Name -like "java*" }
    foreach ($proc in $javaProcesses) {
        $commandLine = $proc.CommandLine
        if ([string]::IsNullOrWhiteSpace($commandLine)) {
            continue
        }

        foreach ($pattern in $patterns) {
            if ($commandLine -match [regex]::Escape($pattern)) {
                Stop-Process -Id $proc.ProcessId -Force -ErrorAction SilentlyContinue
                break
            }
        }
    }
}

function Stop-JavaProcessesOnServicePorts {
    foreach ($serviceName in $servicePorts.Keys) {
        $port = $servicePorts[$serviceName]
        $process = Get-PortOwnerProcess -Port $port

        if ($null -ne $process -and $process.ProcessName -eq "java") {
            Write-Host "Stopping stale java process on port $port for $serviceName ..." -ForegroundColor Yellow
            Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue
        }
    }
}

function Confirm-PortFree {
    param(
        [string]$ServiceName,
        [int]$Port
    )

    if (Test-PortOpen -Port $Port) {
        Write-Host "$ServiceName still has port $Port in use. Free the port before starting the project." -ForegroundColor Red
        exit 1
    }
}

function Start-ZipkinWindow {
    if (-not (Test-Path $zipkinJar)) {
        Write-Host "Zipkin jar not found at $zipkinJar" -ForegroundColor Yellow
        return
    }

    if (Test-PortOpen -Port 9411) {
        Write-Host "Zipkin already running on port 9411" -ForegroundColor Yellow
        return
    }

    $cmd = @"
`$env:JAVA_TOOL_OPTIONS='$JavaOpts'
Write-Host 'Starting Zipkin ...' -ForegroundColor Cyan
java -jar '$zipkinJar'
"@

    Start-Process powershell -ArgumentList @(
        "-NoExit",
        "-Command",
        $cmd
    ) | Out-Null
}

Write-Host "Starting SmartCourier services in order..." -ForegroundColor Green
Write-Host "Stopping stale SmartCourier Java processes if any are already running..." -ForegroundColor Yellow
Stop-SmartCourierProcesses
Stop-JavaProcessesOnServicePorts
Start-Sleep -Seconds 2

foreach ($serviceName in $servicePorts.Keys) {
    Confirm-PortFree -ServiceName $serviceName -Port $servicePorts[$serviceName]
}

if (-not (Test-PortOpen -Port 3306)) {
    Write-Host "MySQL is not reachable on port 3306. Start MySQL before running the services." -ForegroundColor Yellow
}

if (-not (Test-PortOpen -Port 5672)) {
    Write-Host "RabbitMQ is not reachable on port 5672. Start RabbitMQ before running the services." -ForegroundColor Yellow
}

Start-ZipkinWindow
Start-Sleep -Seconds 8

Start-ServiceWindow -Title "1) Eureka Server" -ServiceDir "$root\eureka-server" -EnvVars @{}
Start-Sleep -Seconds 8

Start-ServiceWindow -Title "2) Config Server" -ServiceDir "$root\config-server" -EnvVars @{
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE = "http://localhost:8761/eureka"
}
Start-Sleep -Seconds 8

Start-ServiceWindow -Title "3) Auth Service" -ServiceDir "$root\auth-service" -EnvVars @{
    SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/smartcourier_auth_db"
}
Start-Sleep -Seconds 8

Start-ServiceWindow -Title "4) Delivery Service" -ServiceDir "$root\delivery-service" -EnvVars @{
    SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/smartcourier_delivery_db"
    SPRING_RABBITMQ_HOST = "localhost"
    SPRING_RABBITMQ_PORT = "5672"
}
Start-Sleep -Seconds 8

Start-ServiceWindow -Title "5) Tracking Service" -ServiceDir "$root\tracking-service" -EnvVars @{
    SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/smartcourier_tracking_db"
    SPRING_RABBITMQ_HOST = "localhost"
    SPRING_RABBITMQ_PORT = "5672"
}
Start-Sleep -Seconds 8

Start-ServiceWindow -Title "6) Admin Service" -ServiceDir "$root\admin-service" -EnvVars @{
    SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/smartcourier_admin_db"
}
Start-Sleep -Seconds 8

Start-ServiceWindow -Title "7) API Gateway" -ServiceDir "$root\api-gateway" -EnvVars @{
    EUREKA_CLIENT_SERVICEURL_DEFAULTZONE = "http://localhost:8761/eureka"
}

Write-Host ""
Write-Host "All service windows launched." -ForegroundColor Green
Write-Host "Check Eureka: http://localhost:8761" -ForegroundColor Yellow
Write-Host "Gateway base: http://localhost:8080/gateway" -ForegroundColor Yellow
