
# QR-Based Text & File Sharing System

A web application built using Spring Boot that allows users to share text and files through QR codes.

## Features

- Share text using QR codes
- Upload and share files
- Generate QR codes for sharing links
- Store uploaded files
- Docker and Docker Compose support

## Technologies Used

- Java
- Spring Boot
- Maven
- Docker
- Docker Compose
- QR Code Generation Library

## Project Structure

```text
QuickShare/
├── src/
├── uploads/
├── .mvn/
├── Dockerfile
├── docker-compose.yaml
├── mvnw
├── mvnw.cmd
├── pom.xml
├── .gitignore
└── README.md
```

## Prerequisites

- Java JDK
- Docker Desktop
- Git

## Run Locally

Clone the repository:

```bash
git clone https://github.com/AmolRBorude/QR-Based-Text-File-Sharing-Spring-Boot.git
cd QR-Based-Text-File-Sharing-Spring-Boot
```

Run the application on Windows:

```cmd
mvnw.cmd spring-boot:run
```

## Run with Docker

Make sure Docker Desktop is running.

```bash
docker compose up --build
```

Open your browser at:

```text
http://localhost:8080
```

The port may differ depending on your configuration.

## Stop Docker Containers

```bash
docker compose down
```

## How to Use

1. Enter the text you want to share or upload a file.
2. Generate a QR code.
3. Scan the QR code using a smartphone.
4. Open the generated link to access the shared content.

## Future Enhancements

- User authentication
- Expiring sharing links
- Automatic deletion of uploaded files
- File size restrictions
- Cloud deployment

## Author

**Amol Ramdas Borude**

GitHub: https://github.com/AmolRBorude

## License

This project is intended for educational and portfolio purposes.
