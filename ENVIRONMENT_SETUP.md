# Environment Variables Setup Guide

This guide explains how to set up environment variables for the ZidioConnect application, particularly for Cloudinary credentials and other sensitive configuration.

## Why Use Environment Variables?

✅ **Security**: Keep sensitive credentials out of your codebase  
✅ **Flexibility**: Different configurations for different environments  
✅ **Best Practices**: Follow industry standards for configuration management  
✅ **CI/CD Friendly**: Easy to integrate with deployment pipelines  
✅ **Team Collaboration**: Share code without sharing credentials  

## 1. Environment Variables Structure

### Required Variables
```bash
# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=your_cloud_name_here
CLOUDINARY_API_KEY=your_api_key_here
CLOUDINARY_API_SECRET=your_api_secret_here
```

### Optional Variables (with defaults)
```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/zidioconnect?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=root

# JWT Configuration
JWT_EXPIRATION=86400000

# Server Configuration
SERVER_PORT=8080
```

## 2. Setup Methods

### Method 1: Using .env File (Recommended for Development)

1. **Create a `.env` file** in your project root:
```bash
# Copy the example file
cp env.example .env
```

2. **Edit the `.env` file** with your actual values:
```bash
# Cloudinary Configuration
CLOUDINARY_CLOUD_NAME=my-awesome-cloud
CLOUDINARY_API_KEY=123456789012345
CLOUDINARY_API_SECRET=abcdefghijklmnopqrstuvwxyz123456
```

3. **Install environment variable support** (if not already installed):
```bash
# For Maven, add to pom.xml
<dependency>
    <groupId>me.paulschwarz</groupId>
    <artifactId>spring-dotenv</artifactId>
    <version>4.0.0</version>
</dependency>
```

### Method 2: System Environment Variables

#### Windows (PowerShell)
```powershell
# Set environment variables
$env:CLOUDINARY_CLOUD_NAME="my-awesome-cloud"
$env:CLOUDINARY_API_KEY="123456789012345"
$env:CLOUDINARY_API_SECRET="abcdefghijklmnopqrstuvwxyz123456"

# Start the application
mvn spring-boot:run
```

#### Windows (Command Prompt)
```cmd
# Set environment variables
set CLOUDINARY_CLOUD_NAME=my-awesome-cloud
set CLOUDINARY_API_KEY=123456789012345
set CLOUDINARY_API_SECRET=abcdefghijklmnopqrstuvwxyz123456

# Start the application
mvn spring-boot:run
```

#### macOS/Linux
```bash
# Set environment variables
export CLOUDINARY_CLOUD_NAME="my-awesome-cloud"
export CLOUDINARY_API_KEY="123456789012345"
export CLOUDINARY_API_SECRET="abcdefghijklmnopqrstuvwxyz123456"

# Start the application
mvn spring-boot:run
```

### Method 3: IDE Configuration

#### IntelliJ IDEA
1. Go to **Run** → **Edit Configurations**
2. Select your Spring Boot configuration
3. In **Environment variables**, add:
```
CLOUDINARY_CLOUD_NAME=my-awesome-cloud;CLOUDINARY_API_KEY=123456789012345;CLOUDINARY_API_SECRET=abcdefghijklmnopqrstuvwxyz123456
```

#### VS Code
1. Create `.vscode/launch.json`:
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Launch ZidioConnect",
            "request": "launch",
            "mainClass": "com.zidioconnect.ZidioConnectApplication",
            "env": {
                "CLOUDINARY_CLOUD_NAME": "my-awesome-cloud",
                "CLOUDINARY_API_KEY": "123456789012345",
                "CLOUDINARY_API_SECRET": "abcdefghijklmnopqrstuvwxyz123456"
            }
        }
    ]
}
```

## 3. Environment-Specific Configuration

### Development Environment
Create `application-dev.properties`:
```properties
# Development-specific settings
spring.jpa.show-sql=true
logging.level.com.zidioconnect=DEBUG
spring.profiles.active=dev
```

### Production Environment
Create `application-prod.properties`:
```properties
# Production-specific settings
spring.jpa.show-sql=false
logging.level.com.zidioconnect=WARN
spring.profiles.active=prod
```

## 4. Testing Environment Variables

### Verify Configuration
Add this to your controller temporarily to test:
```java
@GetMapping("/test-config")
public ResponseEntity<?> testConfig() {
    return ResponseEntity.ok(Map.of(
        "cloudName", cloudName,
        "apiKey", apiKey != null ? "***" + apiKey.substring(apiKey.length() - 4) : "null",
        "apiSecret", apiSecret != null ? "***" + apiSecret.substring(apiSecret.length() - 4) : "null"
    ));
}
```

### Check Application Startup
Look for these log messages:
```
INFO  c.z.c.config.CloudinaryConfig - Cloudinary configured successfully
INFO  c.z.c.service.FileUploadService - File upload service initialized
```

## 5. Security Best Practices

### ✅ Do's
- Use environment variables for all sensitive data
- Keep `.env` files out of version control
- Use different credentials for different environments
- Rotate credentials regularly
- Use strong, unique passwords

### ❌ Don'ts
- Never commit credentials to Git
- Don't use hardcoded values in production
- Don't share credentials in chat or email
- Don't use the same credentials across environments

## 6. Troubleshooting

### Common Issues

1. **"Cloudinary credentials invalid" error**
   ```bash
   # Check if environment variables are set
   echo $CLOUDINARY_CLOUD_NAME
   echo $CLOUDINARY_API_KEY
   echo $CLOUDINARY_API_SECRET
   ```

2. **Environment variables not loading**
   - Ensure `.env` file is in the project root
   - Check file permissions
   - Verify variable names match exactly

3. **Application not starting**
   ```bash
   # Check for missing required variables
   mvn spring-boot:run -Dspring.profiles.active=dev
   ```

### Debug Mode
Enable debug logging:
```properties
logging.level.com.zidioconnect.config=DEBUG
logging.level.com.zidioconnect.service.FileUploadService=DEBUG
```

## 7. Production Deployment

### Docker
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/zidioconnect-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Run with environment variables
docker run -p 8080:8080 \
  -e CLOUDINARY_CLOUD_NAME=your-cloud \
  -e CLOUDINARY_API_KEY=your-key \
  -e CLOUDINARY_API_SECRET=your-secret \
  zidioconnect:latest
```

### Cloud Platforms

#### Heroku
```bash
heroku config:set CLOUDINARY_CLOUD_NAME=your-cloud-name
heroku config:set CLOUDINARY_API_KEY=your-api-key
heroku config:set CLOUDINARY_API_SECRET=your-api-secret
```

#### AWS
```bash
aws ssm put-parameter --name "/zidioconnect/cloudinary/cloud-name" --value "your-cloud-name" --type "SecureString"
aws ssm put-parameter --name "/zidioconnect/cloudinary/api-key" --value "your-api-key" --type "SecureString"
aws ssm put-parameter --name "/zidioconnect/cloudinary/api-secret" --value "your-api-secret" --type "SecureString"
```

## 8. Quick Start Checklist

- [ ] Copy `env.example` to `.env`
- [ ] Update `.env` with your Cloudinary credentials
- [ ] Verify `.env` is in `.gitignore`
- [ ] Test the application startup
- [ ] Verify file upload functionality
- [ ] Remove any hardcoded credentials from code

This setup ensures your application is secure, flexible, and ready for production deployment! 