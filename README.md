# Call Monitoring — Take-Home Test

Implements `THT-MON-US-001`: a Supervisor-facing Monitoring page with a searchable,
filterable, sortable, paginated call-monitoring table.

- **Backend:** Java 17, Spring Boot 3, Spring Data JPA, PostgreSQL
- **Frontend:** Vue 3 (Composition API), Vite, Axios
- **Database:** PostgreSQL (schema + seed data in `backend/src/main/resources`)

## Project Structure

```
thtmon/
├── backend/    Spring Boot REST API
├── frontend/   Vue 3 SPA
└── README.md
```

## Prerequisites

- JDK 17+
- Maven 3.9+ (or use the wrapper if you add one)
- Node.js 18+
- PostgreSQL 14+ running locally

## 1. Database Setup

```bash
createdb call_monitoring
```

Schema and seed data are applied automatically on backend startup via
`spring.sql.init.mode=always` (see `backend/src/main/resources/schema.sql`
and `data.sql`). No manual migration step is required, but a database named
`call_monitoring` must already exist.

If your Postgres credentials differ from the defaults, update
`backend/src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/call_monitoring
    username: postgres
    password: postgres
```

## 2. Run the Backend

```bash
cd backend
mvn spring-boot:run
```

API will be available at `http://localhost:8080`.

**Endpoint:**

```
GET /api/call-monitoring
  ?search=<keyword>
  &startDate=<yyyy-MM-dd>
  &endDate=<yyyy-MM-dd>
  &sentiment=BELOW_70 | AT_OR_ABOVE_70
  &sortBy=callId | callTimestamp | csName | customerName | sentimentScore
  &sortDir=asc | desc
  &page=<zero-based page index>
```

Response:

```json
{
  "content": [ { "id": 1, "callId": "CALL-0001", "callTimestamp": "...", "csName": "...", "customerName": "...", "sentimentScore": 42.50 } ],
  "page": 0,
  "size": 5,
  "totalElements": 60,
  "totalPages": 12
}
```

### Backend unit tests

```bash
cd backend
mvn test
```

`CallMonitoringServiceTest` uses an in-memory H2 database to verify search,
period, sentiment filtering, and that all filters combine correctly.

## 2b. Run the Backend with Docker (alternative to step 2)

```bash
docker compose up --build
```

This starts two containers:

- `postgres` — Postgres 16, database `call_monitoring`, port `5432` published to the host.
- `backend` — built from `backend/Dockerfile` (multi-stage Maven build → JRE
  runtime image), reads `SPRING_DATASOURCE_URL` / `_USERNAME` / `_PASSWORD`
  from the compose file (Spring's relaxed env-var binding overrides
  `application.yml`), port `8080` published to the host.

`schema.sql` and `data.sql` still run automatically via
`spring.sql.init.mode=always`, so the table and seed rows are created the
first time the `postgres` volume is initialized. To reset to a clean seed
state, tear down the volume: `docker compose down -v`.

**Known bug this setup exposes:** `data.sql` is plain `INSERT`, not
`INSERT ... ON CONFLICT DO NOTHING` / `ON CONFLICT DO UPDATE`. With a
persistent named volume (`pgdata`), every `docker compose up` after the
first one re-runs `data.sql` against a database that already has the rows,
duplicating all 60 seed calls per restart. Locally this was easy to miss
because most people don't restart the JVM as often as they restart a
container. Fix before relying on row counts: either make the insert
idempotent, or set `spring.sql.init.mode=embedded` in a Docker-specific
profile so seeding only happens against a throwaway database.

The backend image was not built or run in this sandbox — its network
egress only allows a fixed domain allowlist (npm/PyPI/crates/GitHub
mirrors), not Maven Central or Docker Hub, so `docker compose up --build`
has not actually been executed against these files. Run it yourself before
submitting and fix whatever Maven or Alpine package resolution issues
surface.

## 3. Run the Frontend

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

App will be available at `http://localhost:5173`.

### Frontend unit tests

```bash
cd frontend
npm run test
```

`useCallMonitoring.spec.js` covers loading state, filter-triggered page
reset, sort toggling, and error handling — with the API layer mocked.

## Design Notes

- **Search, filter, sort, and pagination are all server-side.** The frontend
  never holds the full dataset or filters/sorts it in JavaScript; every
  state change (`filters`, `sort`, `page`) triggers a fresh `GET
  /api/call-monitoring` call. This satisfies the "no hardcoded main data on
  the frontend" / "data must come from PostgreSQL via the backend API"
  constraints.
- **Filters are combined via a single JPA `Specification`**
  (`CallMonitoringSpecifications`), so search + period + sentiment always
  compose into one SQL query rather than three separate in-memory passes.
- **Sortable columns are whitelisted** in `CallMonitoringService` to avoid
  exposing arbitrary entity fields to sort injection.
- **Period restriction** ("latest three months") is currently enforced only
  as a frontend `<input type="date">` min/max constraint. It is **not**
  re-validated on the backend — a hand-crafted request could pass a wider
  range. If this matters for the evaluation, add a backend-side date-range
  check in `CallMonitoringController` or the service layer.
- Page size is fixed at 5 per the acceptance criteria.


## AI Usage

- **AI tool used:** Claude (Anthropic).
- **Backend:** AI was used to assist with generating parts of the backend implementation based on the provided requirements and user stories. All generated code was reviewed, validated, tested, and adjusted as necessary to ensure that the implementation met the expected functionality, API behavior, data flow, and project requirements.
- **Frontend:** AI was used primarily as a development aid for generating and structuring table components and related UI implementation. The generated output was reviewed and adapted to fit the application's existing architecture, requirements, and user interface.
- **Verification:** AI-generated output was not accepted without review. The implementation was manually inspected, integrated into the project, and validated through testing and functional verification to ensure the submitted solution behaved as expected
- **Responsibility** The final implementation, technical decisions, integration, and verification remain the responsibility of the candidate. AI was used as a productivity and development assistance tool rather than as a replacement for understanding or reviewing the submitted code.