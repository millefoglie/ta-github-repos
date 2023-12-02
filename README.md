# TA GitHub Repos

This is a sample Spring Webflux app that demonstrates an API for querying
some data from Github, namely all public repositories of some user. The responses
thus contain
- a repository name
- a repository owner login
- a list of branch names together with the last commit SHA.

The API classes are generated from the provided OpenAPI specification file.
However, the OpenAPI generator plugin seems to not support reactive clients,
so instead of generating a GitHub client, it is coded as an HTTP interface client.
Alternatively, we could just use `WebClient` directly, but HTTP interface clients
added in the latest version of Spring look much nicer and better suited for
production-ready apps.

## Notes

1. Spring has a standard way of formatting error responses. In particular, cases
like handling wrong `Accept` values or calling non-existing endpoints are handled
by Spring internally. Since the task mentions that error responses must contain
only `status` and `message` fields, the default error handling had to be modified.
However, the original error messages were kept.

2. The app does not contain any real business logic, only querying an external service
and formatting the responses. Thus, there is not much to test with unit tests, because
integration tests cover all the test cases anyway. Having both test suits would lead
to duplication of tests and surplus code. For our scenario, using Wiremock stubs and
`WebTestClient` allows to test all the possible interactions with the API, inlcuding
error handling.

3. Wiremock seems to be broken with the latest version of Spring. So, as a workaround
a standalone version of Wiremock has to be used.

## Build

### Maven

```bash
./mvnw clean package
```

### Docker

```bash
docker build -t ta-github-repos:latest .
```

## Run

### Maven

```bash
./mvnw spring-boot:run
```

### Docker

```bash
docker run -p 8080:8080 ta-github-repos:latest
```
