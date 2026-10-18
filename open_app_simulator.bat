@echo off
echo =====================================================================
echo   Opening Campus Lost and Found Mobile and Data Simulator...
echo =====================================================================
start "" "http://127.0.0.1:8080/app_simulator.html"
if %ERRORLEVEL% NEQ 0 (
    start "" "app_simulator.html"
)
