# Pets API — Jakarta EE on WildFly

A small REST API for managing virtual pets. It demonstrates validation, filtering, sorting, and pagination using Jakarta EE 10 running on WildFly.

Base URL when running locally with Maven:
- http://localhost:8080/api

## Quick start

Prerequisites:
- Java 21 (JDK 21)
- Maven 3.9+

Run the app (Dev):
- Windows/PowerShell, macOS, or Linux:
  mvn clean wildfly:run

Once started, open:
- Health check: http://localhost:8080/api/pets


Stop the app:
- Use Ctrl+C in the terminal running Maven.

Run tests:
- mvn test


## Run with Docker

Build the WAR first:
- mvn clean package

Build the image:
- docker build -t pets-api .

Run the container (maps 8080 -> 8080):
- docker run --rm -p 8080:8080 pets-api

Then access the API at:
- http://localhost:8080/api/pets


## Data model

PetDTO
- name: string (required, not blank)
- species: string (required, not blank)
- hungerLevel: integer 0..5 (required)
- happiness: integer 0..5 (required)

Example payload:
{
  "name": "Luna",
  "species": "Dog",
  "hungerLevel": 2,
  "happiness": 4
}


## Endpoints

All endpoints produce and consume application/json unless otherwise stated.

- POST /pets
  Create/adopt a pet.
  Request body: PetDTO (see example above)
  Responses:
  - 201 Created, body: { "id": number }
  - 400 Bad Request (validation error)

- GET /pets
  List pets with optional filtering, sorting, and pagination.
  Query parameters:
  - species: string (optional) — case-insensitive filter (e.g., Cat or Dog)
  - sortBy: one of name, species, hungerLevel, happiness (default: name)
  - order: asc or desc (default: asc)
  - offset: integer >= 0 (default: 0)
  - limit: integer >= 0 (default: 10)

  Response:
  - 200 OK, body: array of PetDTO
  - Pagination headers:
    - X-Total-Count: total number of items (filtered if species is provided)
    - X-Offset: the offset used
    - X-Limit: the limit used
  - 400 Bad Request if sortBy/order/offset/limit are invalid

  Notes:
  - species filter matches case-insensitively.
  - sortBy values are case-insensitive.
  
- GET /pets/{id}
  Fetch a single pet by id.
  Responses:
  - 200 OK, body: PetDTO
  - 404 Not Found if id does not exist

- PUT /pets/{id}/feed
  Decrease hungerLevel by 1 down to a minimum of 0.
  Responses: 204 No Content or 404 Not Found

- PUT /pets/{id}/play
  Increase happiness by 1 up to a maximum of 5.
  Responses: 204 No Content or 404 Not Found

- DELETE /pets/{id}
  Release a pet (remove).
  Responses: 204 No Content or 404 Not Found

## Error Handling

The API uses custom exception mappers (@Provider) for consistent error responses:
- ConstraintViolationExceptionMapper - Bean Validation errors (400)
- NotFoundExceptionMapper - Resource not found (404)
- BadRequestExceptionMapper - Invalid query parameters (400)

All errors return JSON with fieldErrors array when applicable.

## Filtering, sorting, and pagination in detail

Filtering (species):
- GET /pets?species=Cat
- GET /pets?species=dog

Sorting:
- GET /pets?sortBy=hungerLevel&order=desc
- GET /pets?sortBy=name&order=asc

Pagination:
- GET /pets?offset=0&limit=10
- GET /pets?offset=20&limit=10
- Response headers include X-Total-Count, X-Offset, X-Limit to help clients paginate.

Combined examples:
- GET /pets?species=Cat&sortBy=happiness&order=desc&offset=0&limit=5


## Example curl commands

Create a pet:
- curl -i -X POST "http://localhost:8080/api/pets" \
  -H "Content-Type: application/json" \
  -d '{"name":"Luna","species":"Dog","hungerLevel":2,"happiness":4}'

List pets (first page):
- curl -i "http://localhost:8080/api/pets?offset=0&limit=10"

List cats, sorted by happiness desc:
- curl -i "http://localhost:8080/api/pets?species=Cat&sortBy=happiness&order=desc"

Get by id:
- curl -i "http://localhost:8080/api/pets/1"

Feed a pet:
- curl -i -X PUT "http://localhost:8080/api/pets/1/feed"

Play with a pet:
- curl -i -X PUT "http://localhost:8080/api/pets/1/play"

Delete a pet:
- curl -i -X DELETE "http://localhost:8080/api/pets/1"


## Error responses

Errors are returned as JSON with this structure:
{
  "status": 400,
  "error": "Invalid sortBy. Allowed values: name, species, hungerLevel, happiness",
  "timestamp": 1710000000000,
  "fieldErrors": null
}

Common cases:
- 400 Bad Request: invalid query parameters or validation failures
- 404 Not Found: when a pet id is not found


