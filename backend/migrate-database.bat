@echo off
echo ==========================================
echo Database Table Migration Tool
echo ==========================================
echo.
echo This script will:
echo 1. Drop the existing users table
echo 2. Recreate it with the correct schema
echo.
echo WARNING: This will delete all existing user data!
echo.
set /p confirm="Do you want to continue? (yes/no): "
if /i not "%confirm%"=="yes" (
    echo Migration cancelled.
    pause
    exit /b
)

echo.
echo Running migration script...
echo.

psql -U postgres -d chatting_app -f database\migrate_users_table.sql

if errorlevel 1 (
    echo.
    echo ERROR: Migration failed!
    echo Please check the error messages above.
    pause
    exit /b 1
)

echo.
echo ==========================================
echo Migration completed successfully!
echo ==========================================
echo.
echo The users table has been recreated.
echo You can now start the application.
echo.
pause

