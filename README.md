# Football Standing Microservice

A production-ready Spring Boot microservice that provides football team standings information with offline mode capabilities, following SOLID principles and 12-factor app methodology.

## 🏗️ Architecture Overview

This microservice implements a robust architecture designed for scalability, maintainability, and resilience. The application follows microservice best practices and can operate in both online and offline modes.

[asset:1]

## 🎯 Features

### Core Features
- **Team Standing Lookup**: Find team standings by country, league, and team name
- **Offline Mode**: Toggle between online API calls and cached offline data
- **RESTful API**: HATEOAS-compliant REST endpoints
- **Real-time Data**: Integration with APIFootball.com
- **Responsive UI**: Modern web interface with real-time updates

### Technical Features
- **SOLID Principles**: Clean, maintainable code architecture
- **Design Patterns**: Factory, Strategy, Builder, and Singleton patterns
- **12-Factor App**: Production-ready configuration management
- **Security**: API key encryption and secure configuration
- **Monitoring**: Health checks and application metrics
- **Documentation**: OpenAPI 3.0 specification with Swagger UI

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- Docker (optional)
- Git

### Running Locally

1. **Clone the repository**
```bash
git clone https://github.com/deepakmishra1994/football-standing-service
cd football-standing-service
```

2. **Configure API Key**
```bash
# Set environment variable
export API_FOOTBALL_KEY=9bb66184e0c8145384fd2cc0f7b914ada57b4e8fd2e4d6d586adcc27c257a978

# Or edit application.yml
vim src/main/resources/application.properties
```

3. **Build and Run**
```bash
mvn clean package
java -jar target/football-0.0.1.jar
```

4. **Access the Application**
- Web UI: http://localhost:8080
- API Documentation: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

## 📊 System Flow

The following sequence diagram illustrates the complete interaction flow:

[image:1]

## 🔧 API Endpoints

### Core Endpoints

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/countries` | Get all available countries | List of countries |
| GET | `/leagues/{countryId}` | Get leagues by country | List of leagues |
| GET | `/teams/{leagueId}` | Get teams by league | List of teams |
| GET | `/standings/{leagueId}` | Get league standings | Complete standings table |
| GET | `/team-standing/{country}/{leagueId}/{team}` | Get specific team standing | Team position and stats |

### System Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/offline-mode/{enabled}` | Toggle offline mode |
| GET | `/actuator/health` | Health check |
| GET | `/swagger-ui.html` | API documentation |

### HATEOAS Example Response

```json
{
    "country_name": "England",
    "league_id": "149",
    "league_name": "Non League Premier",
    "team_id": "3035",
    "team_name": "Horsham",
    "overall_league_position": "1",
    "overall_league_payed": "42",
    "overall_league_W": "28",
    "overall_league_D": "3",
    "overall_league_L": "11",
    "overall_league_GF": "82",
    "overall_league_GA": "40",
    "overall_league_PTS": "87",
    "team_badge": "https://apiv3.apifootball.com/badges/3035_horsham.jpg",
    "_links": {
        "self": {
            "href": "http://localhost:8080/team-standing/England/149/Horsham"
        },
        "league-standings": {
            "href": "http://localhost:8080/standings/149"
        }
    }
}
```

## 🏛️ Architecture & Design Patterns

### SOLID Principles Implementation

**Single Responsibility Principle (SRP)**
- Each class has a single, well-defined responsibility
- Controllers handle HTTP requests only
- Services contain business logic
- Repositories manage data access

**Open/Closed Principle (OCP)**
- Strategy pattern for online/offline data retrieval
- Factory pattern for API client creation
- Extensible without modifying existing code

**Liskov Substitution Principle (LSP)**
- Interface-based design
- Interchangeable implementations for different data sources

**Interface Segregation Principle (ISP)**
- Focused interfaces for specific concerns
- No forced dependencies on unused methods

**Dependency Inversion Principle (DIP)**
- Dependency injection throughout
- Abstractions over concrete implementations

### Design Patterns Used

1. **Factory Pattern**
   ```java
    @Component
    @RequiredArgsConstructor
    public class DataRetrievalStrategyFactory {

        private final OnlineDataRetrievalStrategy onlineStrategy;
        private final OfflineDataRetrievalStrategy offlineStrategy;

        public DataRetrievalStrategy getStrategy(boolean isOfflineMode) {
            return isOfflineMode ? offlineStrategy : onlineStrategy;
        }
    }
   ```

2. **Strategy Pattern**
   ```java
   public interface DataRetrievalStrategy {
       List<StandingResponse> getStandings(String leagueId);
   }
   
   @Service
   public class OnlineDataStrategy implements DataRetrievalStrategy {
       // Online implementation
   }
   
   @Service  
   public class OfflineDataStrategy implements DataRetrievalStrategy {
       // Offline implementation
   }
   ```

3. **Builder Pattern**
   ```java
   public class StandingResponse {
       public static Builder builder() {
           return new Builder();
       }
       
       public static class Builder {
           // Builder implementation
       }
   }
   ```

## 📝 12-Factor App Implementation

| Factor | Implementation |
|--------|----------------|
| **Codebase** | Single Git repository with multiple deployment environments |
| **Dependencies** | Maven dependency management, no system dependencies |
| **Config** | Environment variables and external configuration |
| **Backing Services** | External API treated as attached resource |
| **Build, Release, Run** | Separate build (Maven), release (Docker), run (Container) |
| **Processes** | Stateless application processes |
| **Port Binding** | Self-contained with embedded Tomcat |
| **Concurrency** | Horizontal scaling via container orchestration |
| **Disposability** | Fast startup, graceful shutdown |
| **Dev/Prod Parity** | Identical environments using containers |
| **Logs** | Structured logging to stdout |
| **Admin Processes** | Management endpoints via Actuator |

## 🛡️ Security

### API Key Protection
```yaml
security:
  api:
    football:
      key: ${API_FOOTBALL_KEY:#{null}}
      encryption:
        enabled: true
        algorithm: AES/GCB/NoPadding
```


## 🐳 Docker Deployment

### Dockerfile
```dockerfile
FROM openjdk:17-jdk-alpine

# Create app directory
WORKDIR /app

# Copy JAR file
COPY target/football-0.0.1.jar app.jar

# Expose port
EXPOSE 8080

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  football-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      - API_FOOTBALL_KEY=${API_FOOTBALL_KEY}
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

### Building and Running
```bash
# Build image
docker build -t football-standing-service:latest .

# Run container
docker run -d \
  --name football-service \
  -p 8080:8080 \
  -e API_FOOTBALL_KEY=your-api-key \
  football-standing-service:latest

# Using Docker Compose
docker-compose up -d
```

## 🚦 Error Handling

### Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(TeamNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTeamNotFound(TeamNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Team Not Found")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
            
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<ErrorResponse> handleExternalApiError(ExternalApiException ex) {
        // Handle external API errors
    }
}
```


## 🤝 Contributing

### Development Workflow
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Write tests for your changes
4. Implement your changes
5. Ensure all tests pass (`mvn test`)
6. Commit your changes (`git commit -m 'Add amazing feature'`)
7. Push to the branch (`git push origin feature/amazing-feature`)
8. Open a Pull Request

### Code Standards
- Follow SOLID principles
- Write comprehensive tests (minimum 80% coverage)
- Include Javadoc for public methods
- Use meaningful variable and method names
- Follow Spring Boot best practices

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [APIFootball Documentation](https://apifootball.com/documentation/)
- [12-Factor App Methodology](https://12factor.net/)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙋‍♂️ Support

For support and questions:
- Create an issue on GitHub
- Contact the development team
- Check the FAQ in the Wiki

---

**Made with ❤️ by the Mishra's Team**
