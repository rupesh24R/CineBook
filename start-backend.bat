@echo off
setlocal
echo ========================================
echo   Starting CineBook Spring Boot Backend
echo ========================================

:: Check Java
set "JAVA_CMD=java"
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if exist "C:\Users\avenash.v\.gradle\jdks\eclipse_adoptium-17-amd64-windows.2\bin\java.exe" (
        set "JAVA_CMD=C:\Users\avenash.v\.gradle\jdks\eclipse_adoptium-17-amd64-windows.2\bin\java.exe"
    )
)

cd backend
if exist "mvnw.cmd" (
    call mvnw.cmd spring-boot:run
) else (
    echo Launching with Maven...
    mvn spring-boot:run
)
endlocal

