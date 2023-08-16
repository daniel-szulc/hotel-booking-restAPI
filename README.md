<div align="center">

# 🏨 RoomReserve - Hotel Booking REST API 

![Java](https://img.shields.io/badge/Java-ED8B00?logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?logo=spring-boot&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?logo=JSON-web-tokens&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-FFCA28?logo=lombok&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit5-25A162?logo=junit5&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?logo=swagger&logoColor=white)
![Project Status](https://img.shields.io/badge/Status-Under%20Development-yellow)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

</div>
Welcome to RoomReserve - your one-stop solution for managing hotel bookings! 🌍 
RoomReserve is a REST API designed for managing room reservations in a hotel. This application allows user registration and management, provides up-to-date information on room availability, pricing, and amenities in your hotel. It's built on Spring Boot, utilizing the power of Java and MySQL to ensure efficient data management and a smooth booking process.

## Status 🚧

🚀 This repository is currently under development. More features will be added soon!


## Features 🌟

- Search and book available hotel rooms
- Get detailed information about room amenities and pricing
- Seamless integration with websites and applications
- Efficient data management with Spring Boot and MySQL
- Easy-to-use REST API endpoints for quick development


## Installation 🛠️

### Using Docker:

Run the Application using Docker Compose

```sh
  docker-compose up
```

All necessary configurations are included in the `docker-compose.yml` file and environment variables are defined in the `.env` file.

### Running Locally:

1. Update application.properties

Set the database URL, username, and password in the `application.properties` file.

2. Run the Application

```sh
  ./mvnw spring-boot:run
```

## API Endpoints 🌐

| Method | Endpoint                      | Description                                   | Access      |
|--------|-------------------------------|-----------------------------------------------|-------------|
| POST   | `/api/auth/signup`            | Register a new user                           | Public      |
| POST   | `/api/auth/signin`            | Login a user and get the authentication token | Public      |
| GET    | `/api/rooms`                  | Retrieve a list of all rooms                  | Public      |
| POST   | `/api/rooms`                  | Create a new room                             | HOTEL       |
| GET    | `/api/user`                   | Retrieve the authenticated user’s details     | Authenticated |
| PUT    | `/api/user/update/password`   | Update the authenticated user’s password      | Authenticated |
| PUT    | `/api/user/update/personal`   | Update the authenticated user’s personal data | Authenticated |
| POST   | `/api/user/create`            | Create a new user with specific role          | ADMIN       |

The API is also documented using Swagger UI, which allows you to test endpoints directly from your browser.

🔗 The Swagger UI page will then be available at `http://server:port/context-path/swagger-ui.html`  and the OpenAPI description will be available at the following url for json format: `http://server:port/context-path/v3/api-docs`

For example, if you are running the application locally, it would be:
http://localhost:8080/swagger-ui.html

## Technologies Used 💡

- Spring Boot
- JWT (JSON Web Token)
- Lombok
- MySQL (Database)
- Docker
- JUnit (for testing)
- H2 (Database for testing)

## Default Admin Account 🧑‍💼

By default, the application is initialized with an administrator account:
- Username: `admin`
- Password: `password`

## License 📄

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
