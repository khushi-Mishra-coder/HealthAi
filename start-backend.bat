@echo off
echo Starting Spring Boot Backend on http://localhost:8080 ...
set PATH=%PATH%;C:\Users\utkarsh mishra\.m2\apache-maven-3.9.9\bin
cd /d "%~dp0backend"
"C:\Users\utkarsh mishra\.m2\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
pause
