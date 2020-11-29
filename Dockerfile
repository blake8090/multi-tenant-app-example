FROM openjdk:11.0-jre

COPY docker/wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

COPY /target/multitenant-*.jar multitenant-*.jar
ENTRYPOINT /wait-for-it.sh db-master:5432 -- java -jar multitenant-*.jar
