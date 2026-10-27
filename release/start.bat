@echo off
setlocal EnableExtensions
cd /d "%~dp0"

if not defined OCB_USERNAME set "OCB_USERNAME=Player"

set "JAVA=java"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA=%JAVA_HOME%\bin\java.exe"

set "ASSETS=%CD%\assets"
if exist "%ASSETS%\indexes\1.8.json" goto assets_ready
if exist "%APPDATA%\.minecraft\assets\indexes\1.8.json" set "ASSETS=%APPDATA%\.minecraft\assets" & goto assets_ready

echo Downloading the game assets (about 115 MB, only needed once)...
powershell -NoProfile -ExecutionPolicy Bypass -File "tools\download-assets.ps1" -Target "%ASSETS%"
if errorlevel 1 goto assets_failed

:assets_ready
if not exist "game" mkdir "game"
"%JAVA%" %OCB_JAVA_OPTS% @runtime\java.args Start --username "%OCB_USERNAME%" --gameDir game --assetsDir "%ASSETS%" --uuid 00000000000000000000000000000001 --accessToken 0 --userType legacy --width 854 --height 480 --installationID 00000000-0000-0000-0000-000000000000 --websocketToken 0 --launcherVersion 2026.10.0 %*
if errorlevel 1 pause
exit /b %ERRORLEVEL%

:assets_failed
echo Could not download the game assets. Check your network connection and run start.bat again.
pause
exit /b 1
