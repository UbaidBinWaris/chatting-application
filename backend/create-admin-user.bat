@echo off
REM Batch script to create admin user in the database
REM Admin credentials:
REM   Email: admin@chatme.com
REM   Password: admin123

echo ================================================================
echo Creating Admin User
echo ================================================================
echo.
echo Database: chatting_app
echo User: chating_username
echo.
echo Admin Credentials:
echo   Email: admin@chatme.com
echo   Password: admin123
echo   Role: ADMIN
echo   Privilege Level: 99
echo.
echo ================================================================

set PGPASSWORD=chatting_password
psql -U chating_username -d chatting_app -f "%~dp0database\create_admin_user.sql"

echo.
echo ================================================================
echo Admin user creation complete!
echo ================================================================
echo.
echo You can now login with:
echo   Email: admin@chatme.com
echo   Password: admin123
echo.

pause
