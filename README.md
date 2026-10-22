# Employee Management Portal

A Spring Boot REST API for managing employee records. The application
supports creating, viewing, updating, searching, filtering, and deleting
employees. It uses Spring Data JPA with MySQL, validates incoming
requests, returns consistent error responses, and provides API
documentation through Swagger UI.

## Technology Stack

-   Java 21
-   Spring Boot
-   Spring MVC
-   Spring Data JPA / Hibernate
-   MySQL
-   Jakarta Bean Validation
-   Lombok
-   Springdoc OpenAPI / Swagger UI
-   Maven

## Project Structure

The application uses a layered structure to keep HTTP handling, business
logic, and database access separate.

``` text
src/main/java/com/employee/employee_management_portal/
├── config/       # OpenAPI configuration
├── controller/   # REST endpoints
├── converter/    # Data convertion logics
├── dto/          # Request and response objects
├── entity/       # JPA entities and employee status enum
├── exception/    # Custom exceptions and global exception handling
├── repository/   # Database access
└── service/      # Business logic and service implementation
```

## Requirements

Before running the application, make sure you have:

-   JDK 21 or later
-   MySQL 8.0 or later running locally, or access to a MySQL server
-   The Maven Wrapper files included in the project (`mvnw` and
    `mvnw.cmd`)

## Database Configuration

The default database connection is configured in
`src/main/resources/application.properties`:

``` properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/employee_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:root}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

The settings mean:

  ---------------------------------------------------------------------------
  Property                                Purpose
  --------------------------------------- -----------------------------------
  `spring.datasource.url`                 JDBC URL for the MySQL database.
                                          The default connects to
                                          `employee_db` on `localhost:3306`
                                          and asks MySQL to create the
                                          database if it does not exist.

  `spring.datasource.username`            MySQL username. The default is
                                          `root`.

  `spring.datasource.password`            Password for the configured MySQL
                                          user. The default is `root`.

  `spring.datasource.driver-class-name`   MySQL JDBC driver class.
  ---------------------------------------------------------------------------

The first three properties support environment-variable overrides:

-   `SPRING_DATASOURCE_URL`
-   `SPRING_DATASOURCE_USERNAME`
-   `SPRING_DATASOURCE_PASSWORD`

The value after the colon inside `${...:...}` is the fallback used when
the environment variable is not set. For example, if
`SPRING_DATASOURCE_PASSWORD` is not defined, Spring uses `root`.

### Before starting

1.  Start MySQL.
2.  Confirm the username and password in `application.properties` match
    your local MySQL configuration.
3.  If your MySQL credentials differ from the defaults, either update
    the fallback values or set the environment variables above.
4.  The default JDBC URL uses `createDatabaseIfNotExist=true`, so the
    MySQL user must have permission to create the database if
    `employee_db` does not already exist.

The project also configures Hibernate schema updates and SQL
initialization in `application.properties`. Review those settings before
using a database containing important data.

## Run the Application

Open a terminal in the project root directory---the folder containing
`pom.xml` and `mvnw.cmd`.

### Windows

Using Command Prompt or PowerShell:

``` powershell
.\mvnw.cmd spring-boot:run
```

If you use Git Bash:

``` bash
./mvnw spring-boot:run
```

### Build the project

``` bash
# Run the tests
./mvnw test

# Build the application
./mvnw clean package
```

On Windows Command Prompt or PowerShell, use `.\mvnw.cmd` instead of
`./mvnw`.

Wait for Spring Boot to report that the application has started. The
application will on port `8080`..

## Swagger and OpenAPI

Once the application is running, open:

-   **Swagger UI:** <http://localhost:8080/swagger-ui/index.html#/>
-   **OpenAPI JSON:** <http://localhost:8080/v3/api-docs>

Swagger UI lets you inspect the available endpoints, review request and
response models, and try requests directly from your browser. Expand an
endpoint, select **Try it out**, provide any required values or JSON,
and click **Execute**.


## Employee API

Base URL:

``` text
http://localhost:8080/api/v1/employees
```

  ---------------------------------------------------------------------------------
  Method                  Endpoint                          Description
  ----------------------- --------------------------------- -----------------------
  `POST`                  `/api/v1/employees`               Create an employee

  `GET`                   `/api/v1/employees`               List employees, with
                                                            pagination and
                                                            search/filter options

  `GET`                   `/api/v1/employees/{id}`          Get an employee by ID

  `PUT`                   `/api/v1/employees/{id}`          Update employee details

  `PATCH`                 `/api/v1/employees/{id}/status`   Change an employee's
                                                            status

  `DELETE`                `/api/v1/employees/{id}`          Delete an employee
  ---------------------------------------------------------------------------------

Common response codes include `200 OK` for successful reads and updates,
`201 Created` when an employee is created, `400 Bad Request` for invalid
input, `404 Not Found` when an employee does not exist, and
`409 Conflict` when a request conflicts with existing data. Check the
Swagger documentation for the exact parameters and request body expected
by each endpoint.

## Sample Data

The project includes `src/main/resources/data.sql` with sample employee
records. The application is configured to run SQL initialization after
Hibernate creates or updates the schema.

## Tests

Run the test suite from the project root:

``` bash
./mvnw test
```

The service tests cover employee creation, duplicate email handling,
retrieval, pagination and filtering, updates, deletion, and status
changes.

