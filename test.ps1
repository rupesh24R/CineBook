# CineBook - PowerShell Test Runner Script
$ErrorActionPreference = "Stop"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "    Running CineBook Test Suite (Java 17) " -ForegroundColor Cyan
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
        Write-Error "JDK 17+ (javac) not found."
        exit 1
    }
}

$mainSources = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
$testSources = Get-ChildItem -Path "src\test\java\com\cinebook\test" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
& $javac -d bin ($mainSources + $testSources)

if ($LASTEXITCODE -ne 0) {
    Write-Error "Test compilation failed."
    exit $LASTEXITCODE
}

& $java -cp bin com.cinebook.test.CineBookTestRunner

