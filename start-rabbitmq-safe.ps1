Write-Host "Starting SmartCourier RabbitMQ container with safer limits..." -ForegroundColor Green
Write-Host "This starts only RabbitMQ, not the full Docker stack." -ForegroundColor Yellow

docker compose -f compose.rabbitmq.yaml up -d

Write-Host ""
Write-Host "RabbitMQ AMQP: http://localhost:5672" -ForegroundColor Cyan
Write-Host "RabbitMQ UI:   http://localhost:15672" -ForegroundColor Cyan
Write-Host "Login: guest / guest" -ForegroundColor Cyan
