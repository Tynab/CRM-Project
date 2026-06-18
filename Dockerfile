# Tom tat: Image phuc vu chay CRM Spring Boot bang Maven Wrapper trong container.
FROM eclipse-temurin:17-jdk-focal
 
WORKDIR /app
 
# Copy Maven Wrapper va pom truoc de cache dependency layer.
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline
 
# Copy source sau khi cache dependency de build/run nhanh hon khi code thay doi.
COPY src ./src
 
CMD ["./mvnw", "spring-boot:run"]
