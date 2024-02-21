FROM maven:3.6.3-jdk-11 AS build

WORKDIR /app
COPY ./WebApp/pom.xml .
RUN mvn dependency:resolve

COPY ./WebApp/src ./src
RUN mvn package

FROM tomee:9-jre11-alpine-plus

WORKDIR /usr/local/tomee/database
ENV DB_URL="jdbc:sqlite:/usr/local/tomee/database/db.sqlite3"

# Install dependencies
RUN apk --update-cache add sqlite

RUN adduser -D tomcat tomcat
RUN chown -R tomcat:tomcat /usr/local/tomee

# Delete Tomcat default applications
RUN rm -rf /usr/local/tomee/webapps/*

COPY ./init_db.sql .
RUN sqlite3 db.sqlite3 < init_db.sql

RUN chown tomcat:tomcat /usr/local/tomee/database/db.sqlite3
RUN chmod 700 /usr/local/tomee/database/db.sqlite3

COPY --from=build /app/target/ROOT.war /usr/local/tomee/webapps/ROOT.war

USER tomcat
EXPOSE 8080
