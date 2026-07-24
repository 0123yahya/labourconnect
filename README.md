# LabourConnect — Stage 1 (Core backend, no WhatsApp yet)

This is the Stage 1 skeleton from the build roadmap: entities, repositories, and REST
endpoints for creating and matching jobs manually via Postman/curl — no WhatsApp
integration yet. That's Stage 2.

## What's here

- **Entities:** `Worker`, `Client`, `Job`, `JobOffer`, `Booking` — matches the schema
  in the design document exactly.
- **Repositories:** Spring Data JPA interfaces for each entity.
- **MatchingService:** the core logic — finds eligible workers for a job, broadcasts
  offers to all of them, and confirms a booking for whichever worker accepts first
  (auto-expiring the rest).
- **Controllers:** REST endpoints to create workers/clients, create a job, trigger
  matching, and simulate a worker accepting/declining.

`ConversationState` isn't here yet on purpose — it's only needed once the WhatsApp
webhook (Stage 2) has to track multi-step chat flows. Nothing here depends on it.

## Setup

1. Install MySQL locally if you don't have it, and make sure it's running.
2. Open `src/main/resources/application.properties` and update the username/password
   to match your local MySQL setup. (`createDatabaseIfNotExist=true` means you don't
   need to manually create the `labourconnect` database — it'll be created on first run.)
3. Open the project in IntelliJ IDEA or VS Code (with the Java extension pack).
4. Run `LabourConnectApplication.java`, or from the terminal: `mvn spring-boot:run`

A note on this being generated outside a normal dev environment: I wrote this
carefully against standard Spring Boot 3.2 / Jakarta Persistence patterns, but I
wasn't able to actually compile or run it myself here — my sandbox doesn't have
access to Maven Central to download dependencies. Run `mvn clean install` as your
first step and paste me any error output if something doesn't compile; it's very
likely a small, fixable thing (e.g. a Java version mismatch or a MySQL driver quirk).

## Testing it end to end (Postman, curl, or Postman-style REST client)

**1. Register a few workers** (do this for your real 100 contacts too, once this is solid):
```
POST http://localhost:8080/api/workers
Content-Type: application/json

{
  "name": "Ramesh Kumar",
  "phoneNumber": "+919876500001",
  "skill": "PAINTER",
  "area": "South Ex"
}
```

**2. Create a job request** (this is what the WhatsApp intake flow will call in Stage 2):
```
POST http://localhost:8080/api/jobs
Content-Type: application/json

{
  "clientPhoneNumber": "+919876512345",
  "clientName": "Anita Sharma",
  "serviceType": "PAINTER",
  "area": "South Ex",
  "preferredDate": "2026-07-25",
  "budget": "800/day"
}
```
Note the returned `id` — you'll need it for the next steps.

**3. Trigger matching:**
```
POST http://localhost:8080/api/jobs/{jobId}/match
```
This finds every `ACTIVE` worker with matching skill + area and creates a
`JobOffer` for each. Check `GET /api/jobs/{jobId}/offers` to see them, all `PENDING`.

**4. Simulate a worker accepting:**
```
POST http://localhost:8080/api/jobs/{jobId}/accept/{workerId}
```
This confirms the booking, sets the job status to `CONFIRMED`, and auto-expires
any other pending offers for the same job — try registering two matching workers
and accepting with one, then check `GET /api/jobs/{jobId}/offers` again to see the
other one flip to `EXPIRED`.

**5. Check the job's final state:**
```
GET http://localhost:8080/api/jobs/{jobId}
```

## What's next (Stage 2)

Once this is working end to end and feels solid, the next step is wiring the
`ClientController`/`JobController` logic into a WhatsApp webhook instead of Postman —
same underlying services, just triggered by incoming chat messages instead of
manual API calls. That needs the `ConversationState` entity and a `MessageRouterService`
to track where each phone number is in the conversation.
