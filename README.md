# 🤖 AI Assistant

A full-stack **Spring Boot + Spring AI** web application that serves as an intelligent coding companion and creative image generator. It features multi-user authentication, per-user conversation memory, Google Search grounding, and Stability AI image generation — all wrapped in a sleek dark-themed UI.

---

## ✨ Features

- 💬 **Coding Assistant** — Context-aware chat powered by Google Gemini (Vertex AI) with structured responses (title + explanation + code block)
- 🎨 **AI Image Generation** — Text-to-image via Stability AI's SDXL (`stable-diffusion-xl-base-1.0`, 1024×1024)
- 🧠 **Per-user Memory** — Conversation history persisted in PostgreSQL via Spring AI's JDBC Chat Memory
- 🔍 **Google Search Grounding** — Real-time web retrieval augments Gemini's responses
- 🔐 **User Authentication** — Signup / Login with username & password stored in PostgreSQL
- 🛡️ **Sensitive Data Advisor** — Custom advisor that intercepts and blocks requests containing PII (passwords, Aadhaar, PAN, etc.)
- 📋 **Copy to Clipboard** — One-click copy button on every code block
- 🌊 **Streaming Chat** — SSE-based streaming endpoint (`/api/stream-chat`) using Reactor `Flux<String>`
- 🔄 **Multi-model Support** — Gemini, Ollama, OpenAI, and ZhipuAI are all wired in and switchable

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.5.6, Java 21 |
| AI Orchestration | Spring AI 1.0.2 |
| Primary LLM | Google Vertex AI — Gemini |
| Image Generation | Stability AI — SDXL |
| Additional AI | Ollama, OpenAI, ZhipuAI |
| Database | PostgreSQL |
| ORM | Spring Data JPA |
| Frontend | HTML, CSS, Vanilla JavaScript |
| Build | Maven |
| Utilities | Lombok, SLF4J |

---

## 📁 Project Structure

```
src/main/
├── java/com/spring/aiproject/Spring/AI/
│   ├── Controller/
│   │   ├── AiController.java       # /api/chat, /api/image, /api/stream-chat
│   │   └── AuthController.java     # /api/auth/login, /api/auth/signup
│   ├── Service/
│   │   ├── ChatService.java        # Interface
│   │   ├── ChatServiceImpl.java    # Gemini + advisor chain logic
│   │   └── AuthService.java        # User CRUD and validation
│   ├── Entity/
│   │   ├── Content.java            # AI response DTO {title, content, code}
│   │   └── Users.java              # JPA user entity
│   ├── Repository/
│   │   └── UsersRepository.java
│   ├── Config/
│   │   ├── AiModelConfig.java      # Named ChatClient beans (Gemini, Ollama, OpenAI)
│   │   └── CorsConfig.java
│   ├── advisors/
│   │   └── SensitiveDataAdvisor.java  # Custom CallAdvisor for PII blocking
│   └── tools/
│       └── DateTimeTool.java           # Spring AI @Tool for current date/time
└── resources/
    └── static/
        ├── chat.html               # Main chat + image UI
        ├── login.html
        └── signup.html
```

---

## ⚙️ Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL (running locally or remotely)
- Google Cloud account with Vertex AI API enabled
- Stability AI API key
- (Optional) Ollama installed locally for local LLM support

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/AI_Assistant.git
cd AI_Assistant
```

### 2. Configure `application.properties`

Create or edit `src/main/resources/application.properties`:

```properties
# Server
server.port=8082

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/ai_assistant
spring.datasource.username=YOUR_DB_USER
spring.datasource.password=YOUR_DB_PASSWORD
spring.jpa.hibernate.ddl-auto=update

# Spring AI — Vertex AI Gemini
spring.ai.vertex.ai.gemini.project-id=YOUR_GCP_PROJECT_ID
spring.ai.vertex.ai.gemini.location=us-central1
spring.ai.vertex.ai.gemini.chat.options.model=gemini-2.0-flash

# Spring AI — Stability AI
spring.ai.stabilityai.api-key=YOUR_STABILITY_AI_KEY

# Spring AI — OpenAI (optional)
spring.ai.openai.api-key=YOUR_OPENAI_KEY

# Spring AI — Ollama (optional, local)
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.options.model=llama3
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

### 4. Open in browser

```
http://localhost:8082/signup.html   # Create an account
http://localhost:8082/login.html    # Log in
http://localhost:8082/chat.html     # Start chatting
```

---

## 🔌 API Reference

### Auth

| Method | Endpoint | Body | Description |
|---|---|---|---|
| `POST` | `/api/auth/signup` | `{ "username": "", "password": "" }` | Register a new user |
| `POST` | `/api/auth/login` | `{ "username": "", "password": "" }` | Authenticate a user |

### Chat

| Method | Endpoint | Body | Description |
|---|---|---|---|
| `POST` | `/api/chat` | `{ "username": "", "message": "" }` | Send a message; returns structured `List<Content>` |
| `POST` | `/api/stream-chat` | `{ "username": "", "message": "" }` | Streaming chat via SSE |

### Image

| Method | Endpoint | Params | Description |
|---|---|---|---|
| `GET` | `/api/image` | `?prompt=<text>` | Generate an image; returns URL or base64 data URI |

### Response Schema (`Content`)

```json
[
  {
    "title": "Overview",
    "content": "Explanation of the answer...",
    "code": "// Generated code block"
  }
]
```

---

## 🧩 Architecture Overview

```
Browser (HTML/CSS/JS)
        │
        ├── POST /api/chat  ──►  ChatServiceImpl
        │                              │
        │                     Gemini (Vertex AI)
        │                     + Google Search Grounding
        │                     + SensitiveDataAdvisor  (PII guard)
        │                     + SimpleLoggerAdvisor   (observability)
        │                     + MessageChatMemoryAdvisor (PostgreSQL history)
        │
        ├── GET  /api/image  ──►  StabilityAiImageModel (SDXL 1024×1024)
        │
        ├── POST /api/auth/signup  ──►  AuthService  ──►  PostgreSQL
        └── POST /api/auth/login   ──►  AuthService  ──►  PostgreSQL
```

---

## 🔮 Future Scope

- [ ] JWT-based auth with token refresh
- [ ] BCrypt password hashing
- [ ] RAG with vector database (pgvector / Pinecone)
- [ ] File upload for code review
- [ ] Runtime model selector (Gemini / GPT-4o / Ollama)
- [ ] Rate limiting per user
- [ ] Docker Compose deployment

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
