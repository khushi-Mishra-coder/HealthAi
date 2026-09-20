@echo off
echo Starting Spring Boot Backend...
set PATH=%PATH%;C:\Users\utkarsh mishra\.m2\apache-maven-3.9.9\bin
cd /d "%~dp0"
"C:\Users\utkarsh mishra\.m2\apache-maven-3.9.9\bin\mvn.cmd" spring-boot:run
pause
