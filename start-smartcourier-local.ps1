param(
    [string]$JavaOpts = ""
)

$root = $PSScriptRoot

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

Write-Host "Starting SmartCourier services in order..." -ForegroundColor Green

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
