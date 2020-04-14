FROM maven:3.5.4-jdk-8-alpine as builder

WORKDIR /home

COPY . .

RUN mvn clean package

FROM openjdk:8u171-jre-alpine

RUN apk add curl

COPY --from=builder /home/src/main/resources/application.properties application.properties
COPY --from=builder /home/target/datahub-admin-api-1.0.0.jar datahub-admin-api-1.0.0.jar

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /datahub-admin-api-1.0.0.jar" ]
