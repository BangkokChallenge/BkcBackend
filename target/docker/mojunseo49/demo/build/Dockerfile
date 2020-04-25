FROM openjdk:11-jre
MAINTAINER "jayas" <mojunseo49@naver.com>
COPY target/demo-*.jar app.jar


ENV DBPATH jdbc:postgresql://54.180.180.121:5432/postgres
ENV DBUSER postgres
ENV DBPWD pass
ENTRYPOINT ["java","-jar","app.jar"]
