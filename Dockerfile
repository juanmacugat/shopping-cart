FROM openjdk:8-jre-alpine
MAINTAINER Juan Manuel Cugat

ADD ./target/shopping-cart.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/shopping-cart.jar"]

EXPOSE 8080