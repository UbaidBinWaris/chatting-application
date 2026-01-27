@echo off
echo Running database migration for file attachments...

psql -U chating_username -d chatting_app -f database/add_file_attachments.sql

if %ERRORLEVEL% EQU 0 (
    echo Migration completed successfully!
) else (
    echo Migration failed!
)

pause
