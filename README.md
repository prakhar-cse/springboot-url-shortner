# URL Shortener

A minimal Spring Boot service that generates deterministic short URLs from a full URL and stores the mapping in PostgreSQL.

The application exposes two REST endpoints:

- `POST /url` to create a short URL
- `GET /url/{shortCode}` to look up the original URL

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Springdoc OpenAPI UI

## Current Behavior

- Short URLs are generated from an MD5-based hash of the original URL.
- The generated short URL is returned in the form `tiny.com/<8-char-key>`.
- URL lookup is backed by PostgreSQL via JPA.
- `GET /url/{sUrl}` returns JSON with the stored original URL and short key.

Important implementation note:

- The lookup endpoint does not redirect the client yet. It returns the stored mapping as JSON.
- Redis dependencies are present in `pom.xml`, but there is no Redis-backed logic in the current code.

## Project Structure

```text
src/main/java/com/project/url_shortner
├── controller     # REST endpoints
├── dto            # Request payloads
├── entity         # JPA entity
├── repository     # Spring Data repository
└── service        # URL generation and lookup logic
```

## Prerequisites

- JDK 21
- PostgreSQL running locally on port `5432`
- Maven 3.9+ or the included Maven Wrapper

The project is configured for Java 21 in [pom.xml](/Users/prakharrastogi/Desktop/url-sb/url-shortner/pom.xml). A local test run on this machine failed under Java 17 with `release version 21 not supported`.

## Configuration

The default datasource settings are in [src/main/resources/application.properties](/Users/prakharrastogi/Desktop/url-sb/url-shortner/src/main/resources/application.properties):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/url_shortner
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
```

Create a local PostgreSQL database named `url_shortner`, or override these values through environment variables or Spring configuration before starting the app.

## Running the Application

With Java 21 installed:

```bash
./mvnw spring-boot:run
```

Or build and run the jar:

```bash
./mvnw clean package
java -jar target/url-shortner-0.0.1-SNAPSHOT.jar
```

By default, the app runs on `http://localhost:8080`.

## API

### Create a Short URL

`POST /url`

Request body:

```json
{
  "url": "https://gitlab.com/presto"
}
```

Example:

```bash
curl -X POST http://localhost:8080/url \
  -H "Content-Type: application/json" \
  -d '{"url":"https://gitlab.com/presto"}'
```

Example response:

```json
{
  "id": null,
  "url": "https://gitlab.com/presto",
  "shortUrl": "tiny.com/abcdefgh",
  "key": null,
  "count": 0,
  "creationDateTime": null,
  "expirationDateTime": null
}
```

Note: the exact `shortUrl` value depends on the hash output for the input URL.

### Resolve a Short URL

`GET /url/{sUrl}`

In the current implementation, `{sUrl}` is the 8-character key stored in the database, not the full `tiny.com/...` string.

Example:

```bash
curl http://localhost:8080/url/abcdefgh
```

Example response:

```json
{
  "id": null,
  "url": "https://gitlab.com/presto",
  "shortUrl": "abcdefgh",
  "key": null,
  "count": 0,
  "creationDateTime": null,
  "expirationDateTime": null
}
```

## API Documentation

Springdoc OpenAPI UI is included in the project. After the app starts, check:

- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/v3/api-docs`

## Testing

Run:

```bash
./mvnw test
```

Status from verification in this workspace:

- Maven Wrapper runs correctly after it can populate its local cache.
- The build currently fails here because the installed JDK is `17.0.14`, while the project requires Java 21.

## Known Limitations

- No collision handling if two URLs produce the same 8-character key.
- No redirect response for resolved short URLs.
- No request validation beyond a null or empty string check.
- No access count or expiration handling, even though fields exist on the entity.
- No automated tests beyond Spring context startup.

## Next Improvements

- Return an HTTP redirect for short URL resolution.
- Add validation for malformed URLs.
- Store and expose creation timestamps consistently.
- Track hit counts and support expiration.
- Add service and controller tests.
