@echo off
setlocal
echo ========================================
echo       Compiling CineBook (Java 17+)
echo ========================================

if not exist "bin" mkdir "bin"

set "JAVAC_CMD=javac"
set "JAVA_CMD=java"

where javac >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if exist "C:\Users\avenash.v\.gradle\jdks\eclipse_adoptium-17-amd64-windows.2\bin\javac.exe" (
        set "JAVAC_CMD=C:\Users\avenash.v\.gradle\jdks\eclipse_adoptium-17-amd64-windows.2\bin\javac.exe"
        set "JAVA_CMD=C:\Users\avenash.v\.gradle\jdks\eclipse_adoptium-17-amd64-windows.2\bin\java.exe"
    )
)

"%JAVAC_CMD%" -d bin src\main\java\com\cinebook\model\*.java src\main\java\com\cinebook\interfaces\*.java src\main\java\com\cinebook\service\*.java src\main\java\com\cinebook\util\*.java src\main\java\com\cinebook\app\*.java src\main\java\com\cinebook\*.java

if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed.
    exit /b %ERRORLEVEL%
)

echo [SUCCESS] Compilation successful!
echo Launching CineBook...
echo.
"%JAVA_CMD%" -cp bin com.cinebook.Main
endlocal
