@echo off
echo Dang bien dich code Java...
javac -cp ".;mysql-connector-j-8.3.0.jar" *.java
if %errorlevel% neq 0 (
    echo Loi khi bien dich! Kiem tra lai code.
    pause
    exit /b %errorlevel%
)
echo Dang khoi dong ung dung...
java -cp ".;mysql-connector-j-8.3.0.jar" BookingAppGUI
