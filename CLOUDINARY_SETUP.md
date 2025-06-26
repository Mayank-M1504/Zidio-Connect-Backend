# Cloudinary Setup Guide for ZidioConnect

This guide will help you set up Cloudinary for storing resume PDFs and profile pictures in your ZidioConnect application.

## 1. Create a Cloudinary Account

1. Go to [Cloudinary's website](https://cloudinary.com/)
2. Click "Sign Up" and create a free account
3. Verify your email address

## 2. Get Your Cloudinary Credentials

After signing up and logging in:

1. Go to your **Dashboard**
2. You'll see your **Cloud Name**, **API Key**, and **API Secret**
3. Copy these values - you'll need them for configuration

## 3. Update Application Properties

Update your `src/main/resources/application.properties` file with your Cloudinary credentials:

```properties
# Cloudinary Configuration
cloudinary.cloud-name=your_cloud_name_here
cloudinary.api-key=your_api_key_here
cloudinary.api-secret=your_api_secret_here

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

**Replace the placeholder values with your actual Cloudinary credentials.**

## 4. Cloudinary Folder Structure

The application will automatically create the following folder structure in your Cloudinary account:

```
zidioconnect/
├── profile-pictures/    # Profile images (400x400, face-focused)
└── resumes/            # PDF resumes (raw files)
```

## 5. File Upload Features

### Profile Pictures
- **Supported formats**: JPEG, JPG, PNG, GIF, WebP
- **Maximum size**: 5MB
- **Automatic transformations**: 
  - Resized to 400x400 pixels
  - Face-focused cropping
  - Automatic quality optimization
- **Storage location**: `zidioconnect/profile-pictures/`

### Resumes
- **Supported format**: PDF only
- **Maximum size**: 10MB
- **Storage location**: `zidioconnect/resumes/`
- **No transformations applied** (raw file storage)

## 6. API Endpoints

### Create/Update Profile with Files
```
POST /api/student/profile
Content-Type: multipart/form-data
Authorization: Bearer <jwt_token>

Form Data:
- profilePicture: File (optional)
- resume: File (optional)
- profileData: JSON string (optional)
```

### Update Profile Picture Only
```
PUT /api/student/profile/picture
Content-Type: multipart/form-data
Authorization: Bearer <jwt_token>

Form Data:
- profilePicture: File (required)
```

### Update Resume Only
```
PUT /api/student/profile/resume
Content-Type: multipart/form-data
Authorization: Bearer <jwt_token>

Form Data:
- resume: File (required)
```

## 7. Testing with Postman

### Test Profile Picture Upload
1. Create a new POST request to `http://localhost:8080/api/student/profile`
2. Set Authorization header: `Bearer <your_jwt_token>`
3. In the Body tab, select "form-data"
4. Add key: `profilePicture` (type: File)
5. Select an image file (JPEG, PNG, etc.)
6. Send the request

### Test Resume Upload
1. Create a new POST request to `http://localhost:8080/api/student/profile`
2. Set Authorization header: `Bearer <your_jwt_token>`
3. In the Body tab, select "form-data"
4. Add key: `resume` (type: File)
5. Select a PDF file
6. Send the request

### Test Combined Upload
1. Create a new POST request to `http://localhost:8080/api/student/profile`
2. Set Authorization header: `Bearer <your_jwt_token>`
3. In the Body tab, select "form-data"
4. Add keys:
   - `profilePicture` (type: File)
   - `resume` (type: File)
   - `profileData` (type: Text) - JSON string with profile information
5. Select your files and add profile data
6. Send the request

## 8. Response Format

### Successful Upload Response
```json
{
  "id": 1,
  "userId": 1,
  "profilePicture": "https://res.cloudinary.com/your-cloud/image/upload/v1234567890/zidioconnect/profile-pictures/filename.jpg",
  "resume": "https://res.cloudinary.com/your-cloud/raw/upload/v1234567890/zidioconnect/resumes/filename.pdf",
  "basicInfo": { ... },
  "academicInfo": { ... },
  "professionalInfo": { ... },
  "personalInfo": { ... },
  "careerInfo": { ... },
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### Error Responses
```json
{
  "error": "Bad Request",
  "message": "Resume must be a PDF file"
}
```

## 9. Security Features

### File Validation
- **Profile pictures**: Only image files allowed
- **Resumes**: Only PDF files allowed
- **File size limits**: Enforced on both client and server
- **Content type validation**: Prevents malicious file uploads

### Automatic Cleanup
- Old files are automatically deleted when new ones are uploaded
- Prevents storage bloat and unnecessary costs
- Maintains clean Cloudinary account

### Secure URLs
- All uploaded files get secure HTTPS URLs
- Files are stored in organized folders
- Public access is controlled through Cloudinary settings

## 10. Cloudinary Dashboard Features

### Media Library
- View all uploaded files in your Cloudinary dashboard
- Organize files by folders
- Monitor storage usage

### Transformations
- Profile pictures are automatically transformed for optimal display
- Resumes are stored as raw files for download

### Analytics
- Monitor upload/download statistics
- Track bandwidth usage
- View performance metrics

## 11. Troubleshooting

### Common Issues

1. **"Invalid file type" error**
   - Ensure profile pictures are images (JPEG, PNG, etc.)
   - Ensure resumes are PDF files

2. **"File too large" error**
   - Profile pictures: Maximum 5MB
   - Resumes: Maximum 10MB

3. **"Cloudinary credentials invalid" error**
   - Double-check your cloud name, API key, and API secret
   - Ensure credentials are correctly set in application.properties

4. **"Upload failed" error**
   - Check your internet connection
   - Verify Cloudinary account is active
   - Check Cloudinary dashboard for any account issues

### Debug Mode
Enable debug logging by adding to `application.properties`:
```properties
logging.level.com.zidioconnect.service.FileUploadService=DEBUG
```

## 12. Production Considerations

### Environment Variables
For production, use environment variables instead of hardcoded values:
```properties
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api-key=${CLOUDINARY_API_KEY}
cloudinary.api-secret=${CLOUDINARY_API_SECRET}
```

### Backup Strategy
- Consider implementing a backup strategy for uploaded files
- Monitor Cloudinary usage and costs
- Set up alerts for storage limits

### CDN Configuration
- Cloudinary automatically provides CDN for faster file delivery
- Configure custom domains if needed
- Optimize delivery settings for your region

## 13. Cost Optimization

### Free Tier Limits
- Cloudinary free tier includes 25 GB storage
- 25 GB bandwidth per month
- Monitor usage to avoid exceeding limits

### Optimization Tips
- Profile pictures are automatically optimized
- Consider implementing lazy loading for images
- Use appropriate file formats for better compression

This setup provides a robust file upload system for your ZidioConnect application with proper validation, security, and optimization features. 