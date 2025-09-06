# Birthday Notification System (BNS) 🎂

A robust Spring Boot application for managing automated birthday notifications via email and SMS.

## ✨ Features

- 🎯 **Automated Birthday Detection**: Automatically detects upcoming birthdays
- 📧 **Email Notifications**: Sends formatted email notifications
- 📱 **SMS Integration**: Twilio SMS support for notifications
- 📊 **Member Management**: Complete CRUD operations for members
- 📋 **CSV Import**: Bulk member import via CSV files
- 🔒 **Security**: Password-protected API endpoints
- 📝 **Logging**: Comprehensive logging and error handling
- 🏥 **Health Checks**: Application health monitoring
- ⏰ **Scheduled Tasks**: Automated daily notification checks

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Maven 3.6+
- Valid email account (SMTP)
- Twilio account (optional, for SMS)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yalleyfred/bns.git
   cd bns
   ```

2. **Environment Setup**
   ```bash
   cp .env.example .env
   # Edit .env with your actual values
   ```

3. **Data Setup**
   ```bash
   cp data.json.example data.json
   # Add your member data
   ```

4. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

## 🔧 Configuration

### Environment Variables

#### Twilio Configuration
- `TWILIO_ACCOUNT_SID`: Your Twilio Account SID
- `TWILIO_AUTH_TOKEN`: Your Twilio Auth Token
- `TWILIO_PHONE_NUMBER`: Your Twilio phone number
- `TWILIO_VALIDATION_PHONE`: Phone number for validation requests
- `TWILIO_CALLBACK_URL`: Callback URL for Twilio webhooks

#### Email Configuration
- `MAIL_HOST`: SMTP server host (e.g., smtp.gmail.com)
- `MAIL_PORT`: SMTP server port (usually 587)
- `MAIL_FROM`: Your email address (sender)
- `MAIL_PASSWORD`: Your email password or app password
- `MAIL_TO`: Recipient email address for notifications

#### Organization Configuration
- `ORGANIZATION_NAME`: Your organization name
- `ORGANIZATION_EMAIL`: Your organization email

#### Security
- `API_PASSWORD`: Secure password for API endpoints

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Endpoints

#### Health Check
```http
GET /api/health
```

#### Member Management
```http
# Get all members
GET /api/members?password=your_password

# Add single member
POST /api/member?password=your_password
Content-Type: application/json

# Add members from CSV
POST /api/members?password=your_password
Content-Type: multipart/form-data

# Delete all members
DELETE /api/members?password=your_password
```

#### Notifications
```http
# Trigger manual notification check
GET /api/notification
```

### Member Data Format
```json
{
  "name": "John Doe",
  "dateOfBirth": "15/08/1990",
  "email": "john.doe@example.com",
  "position": "President",
  "address": "123 Main St",
  "phoneNumber": "+1234567890",
  "gender": "Male",
  "occupation": "Software Developer",
  "emergencyContact": "+1234567891",
  "skills": "Leadership, Communication",
  "pictures": "profile_url",
  "consent": "I Agree"
}
```

## ⏰ Scheduled Tasks

The application automatically runs birthday checks:
- **12:00 PM** daily (with API endpoint)
- **6:00 PM** daily (background task)

Notifications are sent **2 days before** the actual birthday.

## 📋 Logging

Logs are written to:
- **Console**: Formatted output for development
- **File**: `logs/bns-application.log` (rotated daily, 7-day retention)

Log levels:
- `INFO`: Application events
- `WARN`: Authentication failures, data issues
- `ERROR`: System errors, service failures
- `DEBUG`: Detailed debugging information

## 🧪 Testing

Run the test suite:
```bash
./mvnw test
```

## 🔒 Security Best Practices

### ✅ Implemented Security Features
- Environment variable configuration
- Password-protected API endpoints
- Input validation
- Error handling without information disclosure
- Personal data protection (PII excluded from version control)
- Comprehensive logging for security monitoring

### 🛡️ Security Recommendations
1. **Use strong, unique passwords** for all credentials
2. **Regularly rotate** API tokens and passwords
3. **Enable 2FA** on email accounts used for SMTP
4. **Monitor logs** for unauthorized access attempts
5. **Use HTTPS** in production deployments
6. **Regularly update** dependencies

## 🐳 Docker Support

Build and run with Docker:
```bash
# Build image
docker build -t bns-app .

# Run container
docker run -p 8080:8080 --env-file .env bns-app
```

## 📊 Monitoring

The application provides health check endpoints for monitoring:
- `/api/health` - Application health status
- Built-in metrics via Spring Boot Actuator

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For support and questions:
- Create an issue on GitHub
- Check the logs for error details
- Verify environment configuration

---

**⚠️ Important Security Notes**:
- Never commit sensitive credentials to version control
- All secrets are stored in environment variables
- Personal data (`data.json`) is excluded from version control
- Use strong, unique passwords for all credentials
- Regularly rotate API tokens and passwords