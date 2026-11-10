# CineBook - PowerShell Launch Script
$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "      Compiling CineBook (Java 17+)     " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

if (!(Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$javac = "javac"
$java = "java"

if (!(Get-Command javac -ErrorAction SilentlyContinue)) {
    $fallbackJavac = "C:\Users\avenash.v\.gradle\jdks\eclipse_adoptium-17-amd64-windows.2\bin\javac.exe"
    $fallbackJava = "C:\Users\avenash.v\.gradle\jdks\eclipse_adoptium-17-amd64-windows.2\bin\java.exe"
    if (Test-Path $fallbackJavac) {
        $javac = $fallbackJavac
        $java = $fallbackJava
    } else {
        Write-Error "JDK 17+ (javac) not found. Please ensure Java 17+ is installed and on your PATH."
        exit 1
    }
}

$sources = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
& $javac -d bin $sources

if ($LASTEXITCODE -ne 0) {
    Write-Error "Compilation failed."
    exit $LASTEXITCODE
}

Write-Host "[SUCCESS] Compilation successful! Launching CineBook...`n" -ForegroundColor Green
& $java -cp bin com.cinebook.Main

