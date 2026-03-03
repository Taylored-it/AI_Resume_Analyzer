# 🤖 AI Resume Analyzer

An AI-powered resume analysis tool built with **Java Spring Boot** that extracts text from uploaded resumes and provides intelligent feedback using the **OpenAI API**.

---

## Screenshots

### Upload Page
![Upload Page](screenshots/upload-page.png)

### Results Page
![Results Page](screenshots/results-page.png)

---

## Features

- 📄 Upload PDF or DOCX resumes (up to 10MB)
- 🔍 Automatic text extraction using **Apache PDFBox** and **Apache POI**
- 🤖 AI-powered analysis via **OpenAI GPT-4o-mini**
- ✅ Skills detection
- 💡 Actionable improvement suggestions
- 📋 Professional summary of the resume
- 💾 Results stored in **MySQL** database
- 🖨 Print-friendly results page
- ⚠️ Full error handling with user-friendly messages

---

## Tech Stack

| Layer        | Technology                    |
|--------------|-------------------------------|
| Backend      | Java 17, Spring Boot 4.0      |
| Template     | Thymeleaf                     |
| Database     | MySQL + Spring Data JPA       |
| PDF parsing  | Apache PDFBox 3.0             |
| DOCX parsing | Apache POI 5.3                |
| AI           | OpenAI API (GPT-4o-mini)      |
| Build tool   | Maven                         |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8+
- An [OpenAI API key](https://platform.openai.com/api-keys) with billing enabled

---

## Setup & Run

### 1. Clone the project
```bash
git clone <your-repo-url>
cd resume-analyzer
```

### 2. Create the MySQL database
```sql
CREATE DATABASE resume_analyzer_db;
```

### 3. Configure `application.properties`
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password

openai.api.key=sk-...your-openai-key...
```

### 4. Run the application
```bash
./mvnw spring-boot:run
```

### 5. Open in browser
```
http://localhost:8080
```

---

## API Endpoints

| Method | Endpoint              | Description                        |
|--------|-----------------------|------------------------------------|
| GET    | `/`                   | Upload form (web UI)               |
| POST   | `/upload`             | Upload resume and view results     |
| POST   | `/api/upload-resume`  | REST API — returns JSON response   |

### REST API example (`POST /api/upload-resume`)
```bash
curl -X POST http://localhost:8080/api/upload-resume \
  -F "file=@/path/to/resume.pdf"
```

**Response:**
```json
{
  "message": "Resume uploaded successfully.",
  "id": 1,
  "fileName": "resume.pdf",
  "uploadedAt": "2026-03-04T10:00:00",
  "analysis": "{\"summary\":\"...\",\"skills\":[...],\"suggestions\":[...]}"
}
```

---

## Project Structure

```
src/main/java/com/karabo/resume_analyzer/
├── controller/
│   ├── PageController.java        # Thymeleaf web pages
│   └── ResumeController.java      # REST API
├── service/
│   ├── ResumeService.java         # File upload + text extraction
│   └── AIAnalysisService.java     # OpenAI integration
├── entity/
│   └── Resume.java                # JPA entity
├── repository/
│   └── ResumeRepository.java      # Database access
└── exception/
    ├── AIAnalysisException.java
    ├── FileProcessingException.java
    └── GlobalExceptionHandler.java

src/main/resources/
├── templates/
│   ├── index.html                 # Upload page
│   ├── result.html                # Results page
│   └── error.html                 # Error page
└── application.properties
```

---

## Error Handling

| Scenario                      | Behaviour                                      |
|-------------------------------|------------------------------------------------|
| Invalid file type             | Error message shown on upload page             |
| File exceeds 10MB             | Redirect with "File too large" message         |
| Empty / unreadable file       | "Could not read file content" message          |
| Invalid OpenAI API key        | "Invalid API key" message                      |
| OpenAI rate limit hit         | "Rate limit reached, try again" message        |
| OpenAI service down           | "Service temporarily unavailable" message      |
| Unexpected server error       | Generic error page with status code            |

---

## Adding Screenshots

After running the app, take screenshots of:
1. The upload page (`http://localhost:8080`)
2. The results page after uploading a resume

Save them as:
- `screenshots/upload-page.png`
- `screenshots/results-page.png`

---

## License

MIT License — free to use and modify.
