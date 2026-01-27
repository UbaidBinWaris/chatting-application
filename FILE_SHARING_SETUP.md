# File Sharing Feature

## Overview
The chatting application now supports sending and receiving files including:
- **Images** (jpg, png, gif, etc.)
- **Videos** (mp4, avi, mov, etc.)
- **Audio** (mp3, wav, etc.)
- **Documents** (pdf, doc, docx, xls, xlsx, ppt, pptx, txt)
- **Archives** (zip, rar, 7z)
- **Other files**

## Setup Instructions

### 1. Database Migration
Run the database migration to add file attachment columns:

```bash
cd backend
migrate-file-attachments.bat
```

Or manually execute:
```bash
psql -U chating_username -d chatting_app -f database/add_file_attachments.sql
```

### 2. Create Upload Directory
The backend will automatically create an `uploads` folder in the backend directory when you first upload a file. You can change this location in `application.properties`:

```properties
file.upload-dir=uploads
```

### 3. File Size Limits
Default maximum file size is **50MB**. You can adjust this in `application.properties`:

```properties
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

## Features

### File Upload
- Click the **📎** button in the chat input area
- Select a file from your computer
- Optionally add a caption/message
- Click **Send** to upload

### File Types & Display

#### Images
- Displayed inline with preview
- Click to open in full size
- Formats: jpg, jpeg, png, gif, bmp, webp

#### Videos
- Embedded video player with controls
- Formats: mp4, webm, ogg, avi, mov

#### Audio
- Audio player with controls
- Formats: mp3, wav, ogg, m4a

#### Documents & Files
- File icon with name and size
- Download button
- Supported: pdf, doc, docx, xls, xlsx, ppt, pptx, txt, zip, rar, 7z

### File Information
Each file message displays:
- Original filename
- File size (formatted)
- File type icon
- Upload timestamp

## API Endpoints

### Upload File
```
POST /api/files/upload
Headers: Authorization: Bearer {token}
Body: multipart/form-data
  - file: the file to upload
  - conversationId: the conversation ID
  - caption: optional caption/message
```

### Download File
```
GET /api/files/download/{fileName}
```

### View File
```
GET /api/files/view/{fileName}
```

## Database Schema Changes

New columns added to `messages` table:
- `file_url` - URL/path to the uploaded file
- `file_name` - Original filename
- `file_type` - MIME type
- `file_size` - File size in bytes
- `thumbnail_url` - Optional thumbnail for videos/images

Message types:
- `TEXT` - Regular text message
- `IMAGE` - Image file
- `VIDEO` - Video file
- `AUDIO` - Audio file
- `DOCUMENT` - Document file (pdf, office files, txt)
- `FILE` - Other file types

## Security Considerations

1. **File Validation**: Files are validated for invalid path sequences
2. **Unique Filenames**: Files are renamed with UUIDs to prevent conflicts
3. **Authentication**: All upload/download endpoints require JWT authentication
4. **File Type Detection**: MIME types are detected automatically

## Storage

Files are stored in the local filesystem in the `uploads` directory. For production, consider:
- Using cloud storage (AWS S3, Azure Blob, Google Cloud Storage)
- Implementing file cleanup for deleted messages
- Adding virus scanning
- Implementing CDN for faster file delivery

## Troubleshooting

### Upload fails
- Check file size limits in `application.properties`
- Ensure `uploads` directory exists and is writable
- Verify JWT token is valid

### Files not displaying
- Check CORS configuration in `application.properties`
- Ensure backend URL is correct in frontend (localhost:8080)
- Check browser console for errors

### Permission denied
- Ensure the backend process has write permissions to the `uploads` directory
- On Windows, check folder permissions
- On Linux/Mac, run: `chmod 755 uploads`
