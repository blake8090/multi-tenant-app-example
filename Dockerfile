FROM openjdk:11.0-jre

COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

COPY /target/multitenant-*.jar multitenant-*.jar
ENTRYPOINT /wait-for-it.sh database:5432 -- java -jar multitenant-*.jar
