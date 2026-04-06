Write-Host "Stopping SmartCourier RabbitMQ container..." -ForegroundColor Yellow
docker compose -f compose.rabbitmq.yaml down
