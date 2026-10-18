@echo off
setlocal enabledelayedexpansion

echo ========================================================
echo       Starting NestCheck - All-in-One Launcher
echo ========================================================

cd /d "C:\Users\Devraj\.gemini\antigravity\scratch\NestCheck"

set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
set "EMU=%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe"

echo [1/5] Pulling latest updates from GitHub...
git pull origin main

echo [2/5] Building NestCheck APK...
call gradlew.bat assembleDebug
if errorlevel 1 (
    echo [ERROR] Gradle build failed!
    pause
    exit /b 1
)

echo [3/5] Checking Android Emulator...
"%ADB%" devices | findstr /R "emulator.*device$" >nul
if errorlevel 1 (
    echo Starting Pixel 8 emulator...
    start "" "%EMU%" -avd Pixel_8 -no-snapshot-load
    echo Waiting for emulator to connect...
    "%ADB%" wait-for-device
    echo Waiting for Android to finish booting...
    :wait_boot
    timeout /t 2 /nobreak >nul
    for /f %%i in ('"%ADB%" shell getprop sys.boot_completed 2^>nul') do set BOOT=%%i
    if not "!BOOT!"=="1" goto wait_boot
)

echo [4/5] Installing NestCheck onto emulator...
"%ADB%" install -r "app\build\outputs\apk\debug\app-debug.apk"

echo [5/5] Launching NestCheck...
"%ADB%" shell monkey -p com.nestcheck.app -c android.intent.category.LAUNCHER 1

echo.
echo ========================================================
echo   SUCCESS! NestCheck is running with all features!
echo ========================================================
pause
