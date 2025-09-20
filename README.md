# Starbucks Menu AI Chat Application

A Spring AI-powered chat application that helps users find Starbucks drinks based on their preferences, dietary requirements, and nutritional needs.

## Features

- 🤖 AI-powered chat interface using Ollama (Llama 3.2)
- ☕ Complete Starbucks menu database with nutritional information
- 💬 Context-aware conversations with chat history
- 🔍 Smart menu recommendations based on user preferences
- 📊 Nutritional filtering (calories, caffeine, protein, etc.)
- 🌐 REST API with simple web interface

## Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Ollama (installed via Homebrew)

## Setup Instructions

### 1. Database Setup

Create a PostgreSQL database:
```sql
CREATE DATABASE starbucks_menu;
```

Update database credentials in `src/main/resources/application.yml` if needed.

### 2. Ollama Setup

Ollama is already installed and configured with Llama 3.2 model. The service should be running on `http://localhost:11434`.

To verify Ollama is working:
```bash
ollama list
```

### 3. Run the Application

```bash
cd starbucks-menu
mvn spring-boot:run
```

The application will:
- Start on port 8080
- Create database tables automatically
- Load Starbucks menu data from CSV files
- Be ready to accept chat requests

### 4. Access the Application

- Web Interface: http://localhost:8080
- API Endpoint: http://localhost:8080/api/chat

## API Usage

### Start a new chat session
```bash
curl -X POST http://localhost:8080/api/chat/start
```

### Send a message
```bash
curl -X POST http://localhost:8080/api/chat/message \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "your-session-id",
    "message": "I want a low-calorie latte"
  }'
```

## Example Queries

Try asking the AI assistant:

- "I want a low-calorie drink with high protein"
- "What's the best decaf option?"
- "Show me all frappuccinos under 300 calories"
- "I need something with less than 100mg caffeine"
- "What tea options do you have?"
- "Recommend a drink for someone on a diet"

## Architecture

- **Spring Boot 3.2** - Main framework
- **Spring Data JDBC** - Database access
- **Spring AI** - AI integration with Ollama
- **PostgreSQL** - Database
- **Ollama + Llama 3.2** - Local AI model
- **OpenCSV** - CSV data loading

## Project Structure

```
src/main/java/com/starbucks/menuaichat/
├── model/           # Data models (DrinkItem, ChatSession, ChatMessage)
├── repository/      # Data access layer
├── service/         # Business logic (MenuService, StarbucksAiChatService, DataLoaderService)
├── controller/      # REST controllers
└── StarbucksMenuAiChatApplication.java

src/main/resources/
├── application.yml  # Configuration
├── schema.sql      # Database schema
└── static/         # Web interface
```

## Data Sources

The application loads data from:
- `starbucks_drinkMenu_expanded.csv` - Complete drink menu with nutrition
- `starbucks-menu-nutrition-drinks.csv` - Additional drink nutrition data
- `starbucks-menu-nutrition-food.csv` - Food items (for future expansion)

## Configuration

Key configuration options in `application.yml`:

```yaml
spring:
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3.2
          temperature: 0.7
```

## Development Setup

### IDE Configuration

**IntelliJ IDEA:**
1. Import as Maven project
2. Set Project SDK to Java 17+
3. Enable annotation processing: `Settings > Build > Compiler > Annotation Processors`
4. Install Spring Boot plugin for better support

### Development Environment

**Environment Variables:**
Create a `.env` file or set system variables:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/starbucks_menu
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export OLLAMA_BASE_URL=http://localhost:11434
```

**Development Profile:**
Create `application-dev.yml` for development-specific settings:
```yaml
spring:
  profiles:
    active: dev
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/starbucks_menu_dev}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:}
  sql:
    init:
      mode: always  # Recreate schema on each restart during development
logging:
  level:
    com.starbucks.menuaichat: DEBUG
    org.springframework.ai: DEBUG
```

**Hot Reload:**
Enable Spring Boot DevTools for automatic restarts:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

### Running in Development Mode

**Option 1: Maven**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Option 2: IDE**
- Set VM options: `-Dspring.profiles.active=dev`
- Set main class: `com.starbucks.menuaichat.StarbucksMenuAiChatApplication`

### Database Development Setup

**Local PostgreSQL with Docker:**
```bash
docker run --name starbucks-postgres \
  -e POSTGRES_DB=starbucks_menu_dev \
  -e POSTGRES_USER=dev \
  -e POSTGRES_PASSWORD=devpass \
  -p 5432:5432 \
  -d postgres:15
```

**Database Migrations:**
For schema changes, update `schema.sql` and restart the application with `spring.sql.init.mode=always`.

## Troubleshooting

1. **Ollama not responding**: Ensure Ollama service is running
   ```bash
   brew services restart ollama
   ```

2. **Database connection issues**: Check PostgreSQL is running and credentials are correct

3. **CSV loading fails**: Ensure CSV files are in the correct location relative to the JAR

4. **Out of memory**: Increase JVM heap size for large datasets
   ```bash
   java -Xmx2g -jar target/menu-ai-chat-0.0.1-SNAPSHOT.jar
   ```

5. **Port already in use**: Kill existing processes
   ```bash
   lsof -ti:8080 | xargs kill -9
   ```

6. **Vector store issues**: Check PostgreSQL pgvector extension
   ```sql
   CREATE EXTENSION IF NOT EXISTS vector;
   ```