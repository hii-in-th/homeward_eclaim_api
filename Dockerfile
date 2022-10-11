FROM openjdk:17-jdk-slim
WORKDIR /webservice
COPY build/libs/JettyWebServiceTemplate-0.1-all.jar ./webservice.jar
COPY src/main/resources/log4j2.xml ./config
EXPOSE 8080/tcp
CMD ["java","-Dlog4j.configurationFile=config/log4j2.xml","-jar","webservice.jar"]