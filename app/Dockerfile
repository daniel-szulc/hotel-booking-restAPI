FROM maven:3.8.7-openjdk-18

WORKDIR /app
COPY ../.. .

RUN mvn install
CMD mvn spring-boot:run
