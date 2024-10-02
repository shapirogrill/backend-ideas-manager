# Backend Ideas Manager

Back of the ideas-manager app, users can register and organized their ideas.


## How it works
Copy/paste the `application-local-template.yml` file to an `application-local.yml` one and update values. Except if specified, all values are mendatories and should be updated.

### Run the app
```
docker compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

# Environment
## DB
Recommanded Postgres version for Flyway is 15. Newer versions have not been tested then it's recommanded to pull this one.
## Variables
| Variable  | Default  | Mandatory  | Information  |
|-------------|-------------|-------------|-------------|
| LOG_LEVEL     | INFO   | No   | Logging level, default INFO, could be turned to DEBUG in dev mode   |
| POSTGRES_DB     | null   | Yes   | Postgres database name, in dev mode, should be the same as the `docker-compose` file.   |
| POSTGRES_USER     | null   | Yes   | Postgres user, in dev mode, should be the same as the `docker-compose` file.   |
| POSTGRES_PWD     | null   | Yes   | Postgres user's password, in dev mode, should be the same as the `docker-compose` file.   |
| POSTGRES_HOST     | null   | Yes   | Postgres host, in dev mode, should be the same as the `docker-compose` file.   |
| POSTGRES_PORT     | 5432   | No   | Postgres opened port, in dev mode, should be the same as the `docker-compose` file.   |
