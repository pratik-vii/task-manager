# ============================================================
# Stage 1: Build
# ============================================================
FROM maven:3.9-eclipse-temurin-25-alpine AS build

WORKDIR /app
COPY pom.xml lombok.config ./
RUN mvn dependency:go-offline -B

COPY src ./src
ENV MAVEN_OPTS="--add-opens java.base/sun.misc=ALL-UNNAMED"
RUN mvn package -DskipTests -B

# ============================================================
# Stage 2: Runtime
# ============================================================
FROM eclipse-temurin:25-jre-alpine

RUN addgroup -S app && adduser -S app -G app

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

RUN mkdir -p /app/data && chown -R app:app /app

USER app

EXPOSE 8080

# ---- JVM Tuning for Containers ----
# -XX:+UseContainerSupport       : respect container memory/CPU limits
# -XX:MaxRAMPercentage=75.0      : use up to 75% of container memory for heap
# -XX:InitialRAMPercentage=50.0  : start with 50% to reduce GC pressure at boot
# -XX:+UseZGC                    : low-latency GC, ideal for APIs
# -XX:+ZGenerational             : generational ZGC for better throughput
# -XX:+UseStringDeduplication    : reduce memory for duplicate strings
# -XX:+OptimizeStringConcat      : faster string concatenation
# -Djava.security.egd            : faster startup (non-blocking random)

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=50.0", \
    "-XX:+UseZGC", \
    "-XX:+ZGenerational", \
    "-XX:+UseStringDeduplication", \
    "-XX:+OptimizeStringConcat", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=prod", \
    "-jar", "app.jar"]
