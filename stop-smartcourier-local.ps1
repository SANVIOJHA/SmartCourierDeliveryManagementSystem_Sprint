Write-Host "Stopping SmartCourier Java processes..." -ForegroundColor Yellow

$patterns = @(
    "eureka-server",
    "config-server",
    "auth-service",
    "delivery-service",
    "tracking-service",
    "admin-service",
    "api-gateway"
)

$killed = 0
$procs = Get-CimInstance Win32_Process | Where-Object { $_.Name -like "java*" }

foreach ($proc in $procs) {
    $cmd = $proc.CommandLine
    if ([string]::IsNullOrWhiteSpace($cmd)) {
        continue
    }

    foreach ($p in $patterns) {
        if ($cmd -match [regex]::Escape($p)) {
            Stop-Process -Id $proc.ProcessId -Force -ErrorAction SilentlyContinue
            $killed++
            break
        }
    }
}

Write-Host "Stopped processes: $killed" -ForegroundColor Green
