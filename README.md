# Darbak | دربك

<p align="center">
  <img src="docs/images/darbak-logo.png" alt="Darbak logo — دربك" width="280">
</p>

A ride-sharing and match-planning platform for football fans attending the Asian Cup in Saudi Arabia. Fans can offer seats, request rides, save matches, and use AI to help plan their matchday.

## Features

- Ride search, car availability checks, and seat reservations.
- Driver acceptance/rejection of requests and passenger management.
- Saved matches, ride recommendations, and matches without arranged rides.
- Google Maps meeting points and directions links.
- AI matchday plans, two-match attendance estimates, review moderation, and review summaries.
- User ratings/statistics, automatic WhatsApp notifications, and welcome emails.
- Match/stadium management, Football API fixture import, and user banning.

## Technology

Java 17 · Spring Boot · Spring Data JPA/Hibernate · MySQL · Jakarta Validation · Lombok · Maven

Integrations: OpenRouter AI, Google Maps JavaScript, API-Football, UltraMsg WhatsApp, Spring Mail/Gmail SMTP, and OpenPDF. The demo frontend uses plain HTML, CSS, and JavaScript.

## Extra endpoints

All routes below use the prefix **`/api/v1`**. This list follows the team's agreed classification: **45 feature endpoints**, plus **one newly added email-plan route pending correction**. The 34 basic CRUD endpoints are listed separately in [Endpoint allocation](ENDPOINT_ALLOCATION.md).

### Users and analytics — 11

| Method | Path | Purpose |
|---|---|---|
| POST | `/user/register` | Register and send welcome email |
| POST | `/user/login/{email}/{password}` | Login |
| GET | `/user/search/{name}` | Search users by name |
| GET | `/user/above-average-rating` | Users above average rating |
| GET | `/user/below-average-rating` | Users below average rating |
| GET | `/user/average-rating/{userId}` | User's average rating |
| GET | `/user/ride-statistics/{userId}` | User's ride statistics |
| GET | `/user/match-statistics/{userId}` | Statistics for matches connected through rides |
| GET | `/user/upcoming-rides/{userId}` | User's upcoming rides |
| GET | `/user/review-summary/{userId}` | AI review summary |
| GET | `/user/ai-matchday-plan/{userId}/{lang}` | AI matchday plan in `en` or `ar` |

### Saved matches and email plans — 5

| Method | Path | Purpose |
|---|---|---|
| GET | `/user/{userId}/matches` | Get saved matches |
| POST | `/user/{userId}/matches/{matchId}` | Save a match |
| DELETE | `/user/{userId}/matches/{matchId}` | Remove a saved match |
| GET | `/user/{userId}/matches/without-rides` | Find upcoming saved matches without arranged rides |
| POST | `/user/send-plan/{userId}` | Email a PDF plan — pending controller/service correction |

### Rides, maps, and vehicle availability — 10

| Method | Path | Purpose |
|---|---|---|
| POST | `/Ride/add/{driverId}/{matchId}/{carId}` | Create a ride with ownership/capacity/conflict checks |
| GET | `/Ride/search/{matchId}/{date}/{seats}` | Find suitable rides |
| GET | `/Ride/available` | List available rides |
| GET | `/Ride/match/{matchId}` | Get rides for a match |
| GET | `/Ride/driver/{driverId}` | Get a driver's rides |
| PUT | `/Ride/complete/{rideId}/{driverId}` | Complete a departed ride |
| PUT | `/Ride/cancel/{rideId}/{driverId}` | Cancel a ride before departure |
| GET | `/Ride/map/{rideId}` | Get coordinates and Google Maps directions links |
| GET | `/Ride/recommendations/user/{userId}` | Recommend available rides for saved matches |
| GET | `/car/availability-conflict/{carId}` | Check whether a car has an overlapping ride |

Car availability requires `departure` and `expectedArrival` query parameters in `yyyy-MM-dd'T'HH:mm:ss` format. Ride search uses `yyyy-MM-dd` dates. Match and ride times use the backend's local timezone.

### Booking requests and participants — 11

| Method | Path | Purpose |
|---|---|---|
| POST | `/RideRequest/add/{user_id}/{ride_id}` | Request a seat |
| GET | `/RideRequest/ride/{ride_id}` | Get requests for a ride |
| GET | `/RideRequest/user/{user_id}` | Get a user's requests |
| POST | `/RideRequest/accept/{request_id}/{driver_id}` | Accept a request and reserve a seat |
| POST | `/RideRequest/reject/{request_id}/{driver_id}` | Reject a request |
| DELETE | `/RideRequest/cancel/{request_id}/{passenger_id}` | Cancel a pending request |
| GET | `/RideRequest/user/{user_id}/pending` | Get pending requests |
| GET | `/RidePerticipant/user/{user_id}/ride/{ride_id}` | Get a specific participant |
| GET | `/RidePerticipant/ride/{ride_id}` | Get a ride's participants |
| GET | `/RidePerticipant/ride/{ride_id}/role/{role}` | Filter participants by `driver` or `passenger` |
| GET | `/RidePerticipant/passenger-count/{rideId}` | Count passengers |

`RidePerticipant` is the current URL spelling. Keep it exactly as shown.

### Reviews, matches, and administration — 9

| Method | Path | Purpose |
|---|---|---|
| POST | `/review/add` | Review a completed ride with AI comment moderation |
| PUT | `/review/update/{id}` | Update a review with AI comment moderation |
| GET | `/match/stadium/{stadiumId}` | Get matches at a stadium |
| GET | `/match/team/{teamName}` | Get matches involving a team |
| GET | `/match/city/{city}` | Get matches in a city |
| GET | `/match/attendance-check/{match1_id}/{match2_id}` | Get an AI two-match attendance estimate |
| POST | `/stadium/add-all` | Add multiple stadiums; skip existing names |
| POST | `/football/import-test-matches` | Import historical Asian Cup fixtures with shifted test dates |
| PUT | `/admin/ban/{userId}` | Ban a user from logging in |

Review creation requires `rideId`, `reviewerId`, `reviewedUserId`, and `rating` (1–5); `comment` is optional. Reviews are allowed between a completed ride's driver and an enrolled passenger. Written comments are moderated by AI.

AI attendance results are estimates; the current check does not use live traffic data. WhatsApp and welcome emails are triggered automatically by the relevant workflows.
