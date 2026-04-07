#!/bin/bash
set -e

REPO="https://github.com/pratik-vii/task-manager.git"
DIR="task-manager"

# Clone if not already cloned
if [ ! -d "$DIR" ]; then
    git clone "$REPO"
fi

cd "$DIR"

# Build and run
docker compose up --build -d

echo ""
echo "Task Manager is running at http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui/index.html"
echo "Health: http://localhost:8080/actuator/health"
echo ""
echo "To seed test data: curl -X POST http://localhost:8080/seed"
echo "To stop: docker compose down"
