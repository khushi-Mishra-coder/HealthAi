@echo off
echo Starting Python ML Prediction Microservice on http://localhost:8000 ...
cd /d "%~dp0ml-service"
uvicorn app:app --host 127.0.0.1 --port 8000
pause
