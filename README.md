# multi-tenant-app-example
This is a basic example of a Spring Boot application using a multi-tenant architecture for the data layer.

A database-per-tenant strategy is used, where a master database only contains info on connecting to separate tenant databases. This includes info such as the JDBC URL, username, password, and driver class.

Process for connecting to tenant database:
* Client makes a request, passing a specific tenant identifier
* Data layer connects to master database
* Using the tenant identifier, the app queries the master database for a tenant record
* A connection is then made with the tenant database

Integration testing is done using H2 database (https://www.h2database.com/html/main.html).

# Running
This project requires Maven and Docker to be installed on your computer.
1. Start Docker daemon
2. Run the following commands:
    * `mvn verify`
    * `docker-compose build && docker-compose up`

# TODO
- Better error handling
- Pass tenant identifier through path parameter instead of a header
- Unit tests
