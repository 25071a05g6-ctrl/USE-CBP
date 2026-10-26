@echo off
echo ========================================================
echo Smart College Circular Information Extraction System
echo ========================================================
echo.
echo Compiling Main.java...
javac -cp ".;mysql-connector-j-8.3.0.jar;lib\mysql-connector-j-8.3.0.jar" Main.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Compilation failed. Please ensure JDK is installed and in PATH.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [SUCCESS] Compiled successfully! Launching application...
echo.
java -cp ".;mysql-connector-j-8.3.0.jar;lib\mysql-connector-j-8.3.0.jar" Main
pause
