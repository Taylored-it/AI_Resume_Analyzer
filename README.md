# 🤖 AI Resume Analyzer

An AI-powered resume analysis tool built with **Java Spring Boot** that extracts text from uploaded resumes (PDF/DOCX) and provides intelligent, structured feedback using the **OpenAI API**.

---

## Screenshots

### Upload Page
![Upload Page](screenshots/upload-page.png)

### Results Page
![Results Page](screenshots/results-page.png)

> To add screenshots: run the app, visit `http://localhost:8080`, take screenshots, and save them as `screenshots/upload-page.png` and `screenshots/results-page.png`.

---

## Features

- 📄 Upload PDF or DOCX resumes (up to 10MB)
- 🔍 Automatic text extraction using **Apache PDFBox** and **Apache POI**
- 🤖 AI-powered analysis via **OpenAI GPT-4o-mini**
- ✅ Skills detection
- 💡 Numbered, actionable improvement suggestions
- 📋 Professional resume summary
- 💾 All results stored in **MySQL** database
- 🖨 Print-friendly results page
- 🎨 Modern, responsive UI with loading spinner
- ⚠️ Full error handling with user-friendly messages

---

## Tech Stack

| Layer        | Technology                  |
|--------------|-----------------------------|
| Backend      | Java 17, Spring Boot 4.0    |
| UI Templates | Thymeleaf                   |
| Database     | MySQL 8 + Spring Data JPA   |
| PDF parsing  | Apache PDFBox 3.0           |
| DOCX parsing | Apache POI 5.3              |
| AI           | OpenAI API (GPT-4o-mini)    |
| Build tool   | Maven                       |

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
git clone https://github.com/Taylored-it/AI_Resume_Analyzer.git
cd AI_Resume_Analyzer
```

### 2. Create the MySQL database
```sql
CREATE DATABASE resume_analyzer_db;
```

### 3. Configure application.properties
Copy the example file and fill in your credentials:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```
Then edit `application.properties`:
```properties
spring.datasource.username=your_mysql_username
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

| Method | Endpoint             | Description                      |
|--------|----------------------|----------------------------------|
| GET    | `/`                  | Upload form (web UI)             |
| POST   | `/upload`            | Upload resume and view results   |
| POST   | `/api/upload-resume` | REST API — returns JSON response |

### REST API Example

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
resume-analyzer/
│
├── src/main/java/com/karabo/resume_analyzer/
│   ├── controller/
│   │   ├── PageController.java          # Thymeleaf web pages (upload + results)
│   │   └── ResumeController.java        # REST API endpoint
│   │
│   ├── service/
│   │   ├── ResumeService.java           # File upload, storage & text extraction
│   │   └── AIAnalysisService.java       # OpenAI API integration
│   │
│   ├── repository/
│   │   └── ResumeRepository.java        # Database access (Spring Data JPA)
│   │
│   ├── model/
│   │   └── Resume.java                  # JPA entity (resumes table)
│   │
│   ├── exception/
│   │   ├── AIAnalysisException.java
│   │   ├── FileProcessingException.java
│   │   └── GlobalExceptionHandler.java  # Centralised error handling
│   │
│   └── ResumeAnalyzerApplication.java
│
├── src/main/resources/
│   ├── templates/
│   │   ├── index.html                   # Upload page
│   │   ├── result.html                  # Analysis results page
│   │   └── error.html                   # Error page
│   │
│   ├── application.properties           # ⚠️ Not committed — contains credentials
│   └── application.properties.example  # ✅ Safe template to copy from
│
├── screenshots/                         # Add your screenshots here
├── pom.xml
└── README.md
```

---

## Error Handling

| Scenario                  | Behaviour                                    |
|---------------------------|----------------------------------------------|
| Wrong file type           | Error message shown on upload page           |
| File exceeds 10MB         | Redirect with "File too large" message       |
| Empty / unreadable file   | "Could not read file content" message        |
| Invalid OpenAI API key    | "Invalid API key" message                    |
| OpenAI rate limit hit     | "Wait and try again" message                 |
| OpenAI service down       | "Service temporarily unavailable" message    |
| Unexpected server error   | Clean error page with HTTP status code       |

---

## Security

`application.properties` is listed in `.gitignore` and will **never** be committed.
Always use `application.properties.example` as the template and fill in your own credentials locally.

---

## License

MIT License — free to use and modify.
