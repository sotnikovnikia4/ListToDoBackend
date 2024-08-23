# List to do backend

## Description

This project is backend api of my project "List to do". Front end part will be written later.

## Functionality

- Registration and authentication with JWT
- CRUD operations with tasks
- CRUD operations with users
- Searching tasks with multiple criteria and sorting with several fields at one moment

## Stack

- Java
- Spring Framework (Boot, Security, Web, JPA)
- JUnit 5 and Mockito for testing
- PostgreSQL
- MongoDB
- Docker for assembling databases and API together

## How to run the project?

1. Download [**Docker**](https://www.docker.com/products/docker-desktop/)
2. Open *cmd* in project's folder
3. Enter command ```mvnw.cmd clean compile package```
4. Enter command ```docker-compose build```
5. Enter command ```docker-compose up -d```
6. Go to ```localhost:8081/api-docs/swagger``` for documentation
