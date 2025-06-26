# Testing Guide for Cloudinary Setup

This guide will help you test your Cloudinary configuration and file upload functionality step by step.

## Prerequisites

1. ✅ Spring Boot application is running
2. ✅ Cloudinary account is created
3. ✅ Environment variables are set up
4. ✅ Test files are ready (image and PDF)

## Step 1: Set Up Environment Variables

### Option A: Using .env File (Recommended)
```bash
# Create .env file
cp env.example .env

# Edit .env with your actual credentials
CLOUDINARY_CLOUD_NAME=your_actual_cloud_name
CLOUDINARY_API_KEY=your_actual_api_key
CLOUDINARY_API_SECRET=your_actual_api_secret
```

### Option B: System Environment Variables
```bash
# Windows PowerShell
$env:CLOUDINARY_CLOUD_NAME="your_cloud_name"
$env:CLOUDINARY_API_KEY="your_api_key"
$env:CLOUDINARY_API_SECRET="your_api_secret"

# macOS/Linux
export CLOUDINARY_CLOUD_NAME="your_cloud_name"
export CLOUDINARY_API_KEY="your_api_key"
export CLOUDINARY_API_SECRET="your_api_secret"
```

## Step 2: Start the Application

```bash
mvn spring-boot:run
```

Look for these log messages:
```
INFO  c.z.c.config.CloudinaryConfig - Cloudinary configured successfully
INFO  c.z.c.service.FileUploadService - File upload service initialized
```

## Step 3: Test Environment Variables

### Test 1: Environment Variables Check
```bash
GET http://localhost:8080/api/test/env-test
```

**Expected Response:**
```json
{
  "CLOUDINARY_CLOUD_NAME": "your_cloud_name",
  "CLOUDINARY_API_KEY": "SET",
  "CLOUDINARY_API_SECRET": "SET",
  "app_cloud_name": "your_cloud_name",
  "app_api_key": "SET",
  "app_api_secret": "SET"
}
```

**If you see "NOT_SET" values:**
- Check your `.env` file exists and has correct values
- Verify environment variables are set in your terminal
- Restart the application after setting environment variables

## Step 4: Test Cloudinary Configuration

### Test 2: Cloudinary Configuration
```bash
GET http://localhost:8080/api/test/cloudinary-config
```

**Expected Response:**
```json
{
  "cloudName": "your_cloud_name",
  "apiKey": "***1234",
  "apiSecret": "***5678",
  "cloudinaryConnected": true,
  "status": "SUCCESS",
  "message": "Cloudinary configuration is working correctly"
}
```

**If you see errors:**
- Double-check your Cloudinary credentials
- Verify your Cloudinary account is active
- Check internet connection

## Step 5: Test File Upload

### Prepare Test Files

1. **Test Image** (for profile picture):
   - Format: JPEG, PNG, GIF, or WebP
   - Size: Less than 5MB
   - Example: `test-profile.jpg`

2. **Test PDF** (for resume):
   - Format: PDF only
   - Size: Less than 10MB
   - Example: `test-resume.pdf`

### Test 3: Upload Profile Picture
```bash
POST http://localhost:8080/api/test/upload-test
Content-Type: multipart/form-data

Form Data:
- file: [Select your test image file]
```

**Expected Response:**
```json
{
  "fileName": "test-profile.jpg",
  "fileSize": 123456,
  "contentType": "image/jpeg",
  "isValidImage": true,
  "isValidPdf": false,
  "uploadedImageUrl": "https://res.cloudinary.com/your-cloud/image/upload/v1234567890/zidioconnect/profile-pictures/filename.jpg",
  "uploadType": "profile_picture",
  "status": "SUCCESS",
  "message": "File uploaded successfully"
}
```

### Test 4: Upload Resume PDF
```bash
POST http://localhost:8080/api/test/upload-test
Content-Type: multipart/form-data

Form Data:
- file: [Select your test PDF file]
```

**Expected Response:**
```json
{
  "fileName": "test-resume.pdf",
  "fileSize": 234567,
  "contentType": "application/pdf",
  "isValidImage": false,
  "isValidPdf": true,
  "uploadedPdfUrl": "https://res.cloudinary.com/your-cloud/raw/upload/v1234567890/zidioconnect/resumes/filename.pdf",
  "uploadType": "resume",
  "status": "SUCCESS",
  "message": "File uploaded successfully"
}
```

## Step 6: Test Student Profile Endpoints

### Test 5: Create Student Profile with Files
```bash
POST http://localhost:8080/api/student/profile
Authorization: Bearer <your_jwt_token>
Content-Type: multipart/form-data

Form Data:
- profilePicture: [Select image file]
- resume: [Select PDF file]
- profileData: {
    "dateOfBirth": "1995-05-15",
    "gender": "MALE",
    "nationality": "American",
    "address": "123 Main St, City, State 12345",
    "linkedinProfile": "https://linkedin.com/in/testuser",
    "githubProfile": "https://github.com/testuser",
    "portfolioUrl": "https://testuser.dev"
  }
```

**Expected Response:**
```json
{
  "id": 1,
  "userId": 1,
  "profilePicture": "https://res.cloudinary.com/your-cloud/image/upload/v1234567890/zidioconnect/profile-pictures/filename.jpg",
  "resume": "https://res.cloudinary.com/your-cloud/raw/upload/v1234567890/zidioconnect/resumes/filename.pdf",
  "basicInfo": { ... },
  "academicInfo": null,
  "professionalInfo": null,
  "personalInfo": null,
  "careerInfo": null,
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

## Step 7: Verify in Cloudinary Dashboard

1. **Login to Cloudinary Dashboard**
2. **Check Media Library**
   - Look for `zidioconnect/profile-pictures/` folder
   - Look for `zidioconnect/resumes/` folder
3. **Verify Files**
   - Profile pictures should be transformed (400x400)
   - Resumes should be raw PDF files

## Testing with Postman

### Postman Collection Setup

1. **Create a new collection** called "ZidioConnect Testing"
2. **Set up environment variables**:
   - `base_url`: `http://localhost:8080`
   - `jwt_token`: Your JWT token after login

### Test Requests

#### 1. Environment Test
```
GET {{base_url}}/api/test/env-test
```

#### 2. Cloudinary Config Test
```
GET {{base_url}}/api/test/cloudinary-config
```

#### 3. File Upload Test
```
POST {{base_url}}/api/test/upload-test
Body: form-data
Key: file, Type: File, Value: [select file]
```

#### 4. Student Profile Upload
```
POST {{base_url}}/api/student/profile
Headers: Authorization: Bearer {{jwt_token}}
Body: form-data
Keys: profilePicture (File), resume (File), profileData (Text)
```

## Troubleshooting

### Common Issues and Solutions

#### 1. "Environment variables not found"
```bash
# Check if .env file exists
ls -la .env

# Check environment variables
echo $CLOUDINARY_CLOUD_NAME
echo $CLOUDINARY_API_KEY
echo $CLOUDINARY_API_SECRET
```

#### 2. "Cloudinary configuration error"
- Verify Cloudinary credentials are correct
- Check Cloudinary account is active
- Ensure internet connection

#### 3. "File upload failed"
- Check file size limits (5MB for images, 10MB for PDFs)
- Verify file types (images for profile pictures, PDFs for resumes)
- Check Cloudinary storage limits

#### 4. "Invalid file type"
- Profile pictures: JPEG, PNG, GIF, WebP only
- Resumes: PDF only
- Check file extension and content type

#### 5. "Authentication required"
- Login first to get JWT token
- Include `Authorization: Bearer <token>` header
- Ensure token is valid and not expired

### Debug Mode

Enable debug logging in `application.properties`:
```properties
logging.level.com.zidioconnect=DEBUG
logging.level.com.zidioconnect.config=DEBUG
logging.level.com.zidioconnect.service.FileUploadService=DEBUG
```

## Success Criteria

✅ **Environment variables are loaded correctly**  
✅ **Cloudinary configuration is successful**  
✅ **Profile picture upload works**  
✅ **Resume upload works**  
✅ **Files appear in Cloudinary dashboard**  
✅ **Student profile creation with files works**  
✅ **No errors in application logs**  

## Next Steps

After successful testing:
1. Remove test endpoints from production code
2. Set up proper error handling
3. Implement file cleanup for failed uploads
4. Add file size and type validation on frontend
5. Set up monitoring for Cloudinary usage

This testing ensures your Cloudinary integration is working correctly before proceeding with frontend development! 