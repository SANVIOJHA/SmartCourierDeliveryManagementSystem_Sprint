Write-Host "Starting SmartCourier in Docker with safer defaults..." -ForegroundColor Green
Write-Host "This compose setup uses one MySQL container, one RabbitMQ container, and bounded JVM memory." -ForegroundColor Yellow

docker compose up -d --build

Write-Host ""
Write-Host "Open these once containers are healthy:" -ForegroundColor Cyan
Write-Host "Gateway:  http://localhost:8080/gateway"
Write-Host "Eureka:   http://localhost:8761"
Write-Host "RabbitMQ: http://localhost:15672"
Write-Host "Zipkin:   http://localhost:9411"
