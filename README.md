# Birthday Notification System (BNS)

A Spring Boot application for managing birthday notifications via email and SMS.

## Setup

### Environment Variables

Before running the application, you need to set up your environment variables. Copy the `.env.example` file to `.env` and fill in your actual values:

```bash
cp .env.example .env
```

Required environment variables:

#### Twilio Configuration
- `TWILIO_ACCOUNT_SID`: Your Twilio Account SID
- `TWILIO_AUTH_TOKEN`: Your Twilio Auth Token
- `TWILIO_PHONE_NUMBER`: Your Twilio phone number
- `TWILIO_VALIDATION_PHONE`: Phone number for validation requests
- `TWILIO_CALLBACK_URL`: Callback URL for Twilio webhooks

#### Email Configuration
- `MAIL_HOST`: SMTP server host
- `MAIL_PORT`: SMTP server port (usually 587)
- `MAIL_FROM`: Your email address (sender)
- `MAIL_PASSWORD`: Your email password or app password
- `MAIL_TO`: Recipient email address for notifications

#### Organization Configuration
- `ORGANIZATION_NAME`: Your organization name
- `ORGANIZATION_EMAIL`: Your organization email

#### Security
- `API_PASSWORD`: Secure password for API endpoints

### Data Configuration

Copy `data.json.example` to `data.json` and populate it with your member data:

```bash
cp data.json.example data.json
```

### Running the Application

```bash
./mvnw spring-boot:run
```

## Security

⚠️ **Important Security Notes**:
- Never commit sensitive credentials to version control
- All secrets are stored in environment variables
- Personal data (`data.json`) is excluded from version control
- Use strong, unique passwords for all credentials
- Regularly rotate API tokens and passwords

### Protected Information
The following files contain sensitive information and are excluded from git:
- `.env` - Environment variables with secrets
- `data.json` - Member personal information
- `application.properties` - Application configuration