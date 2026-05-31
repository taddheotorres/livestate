#!/bin/bash
set -e

echo "=== Livestate Deploy ==="
echo "Pulling latest code..."
git pull origin main

echo "Building and restarting containers..."
docker compose down
docker compose up -d --build

echo "Cleaning up old images..."
docker system prune -f

echo "=== Deploy complete ==="
echo "Backend: http://localhost:8081/actuator/health"
echo "Frontend: http://localhost:80"
