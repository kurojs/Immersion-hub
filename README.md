# Immersion Hub

A self-hosted language immersion tool: watch video or listen to audio with subtitles, click any word to get its translation, save it with the sentence where you found it, and review everything later.

Built as a learning project with a small, explicit stack: a Spring Boot API and a React SPA. No AI, no scraping, and no data leaves your database except the text you explicitly translate.

## Features

- **Google sign-in** with a JWT session, plus a dev login for local work.
- **Library** of contents: local files or media URLs, each with `SUB` subtitles attached.
- **Subtitle parsing** for `.srt` and `.ass`, including ASS override tags, `\N` line breaks and commas inside dialogue text.
- **Player** that follows the video: the current cue is highlighted, the cue list auto-scrolls, and clicking a line seeks to it.
- **Word lookup**: click a word in the active cue to get a translation in a popup, then save the word together with its sentence and position.
- **Saved words**: paginated list with the original sentence, its translation and the content title; delete individually.
- **Per-user settings**: native language for translation and pause-on-word-click, persisted on the server.

## Tech stack

| Layer | Choice |
|---|---|
| Backend | Java 21, Spring Boot 4, Spring Security (JWT), Spring Data JPA, PostgreSQL |
| Frontend | React 19, TypeScript, Vite, TanStack Query, Tailwind CSS 4, React Router, Axios |
| Translation | Google Cloud Translation API v2 (real) or an offline fake provider (dev) |

## Project structure

```
immersion-hub/
├── backend/                 Spring Boot API
│   └── src/main/java/com/immersion/hub/
│       ├── controller/      REST endpoints
│       ├── service/         business logic
│       ├── model/           JPA entities
│       ├── repository/      Spring Data repositories
│       ├── subtitle/        SRT and ASS parsers
│       ├── translation/     real and fake translation providers
│       └── security/        JWT filter and token service
└── frontend/                React SPA
    └── src/
        ├── api/             Axios client and shared types
        ├── components/      layout, cue list, clickable text, word popup
        ├── context/         auth and settings providers
        ├── pages/           login, library, new content, player, words, settings
        └── utils/           active cue lookup
```

## Prerequisites

- Java 21
- Node.js 20 or newer
- PostgreSQL 14 or newer running on `localhost:5432`

## Getting started

### 1. Database

```bash
createdb immersion
```

The tables are created automatically on first start (`spring.jpa.hibernate.ddl-auto=update`).

### 2. Backend

```bash
cd backend
GOOGLE_CLIENT_ID=your-google-client-id \
JWT_SECRET=at-least-32-characters-long-random-string \
./mvnw spring-boot:run
```

The API listens on `http://localhost:8080`. The default profile is `dev`, which enables the fake translation provider and the dev login endpoint.

### 3. Frontend

```bash
cd frontend
npm install
cp .env.example .env      # then put your Google OAuth client ID in VITE_GOOGLE_CLIENT_ID
npm run dev
```

The app runs on `http://localhost:5173` and proxies `/api` to the backend.

### 4. Google sign-in

1. Create an OAuth 2.0 Client ID of type *Web application* in Google Cloud Console.
2. Add `http://localhost:5173` to the authorized JavaScript origins.
3. Set the same client ID as `VITE_GOOGLE_CLIENT_ID` in `frontend/.env` and as `GOOGLE_CLIENT_ID` in the backend environment.

Without Google configured you can still use the app in dev mode: the login page shows an **Enter as Dev** button that calls `POST /api/dev/login` and creates a local `dev@local` user.

## Environment variables

### Backend

| Variable | Required | Default | Purpose |
|---|---|---|---|
| `GOOGLE_CLIENT_ID` | Yes for Google login | `your-google-client-id` | OAuth client ID used to verify Google ID tokens |
| `GOOGLE_TRANSLATE_KEY` | Only for real translation | `your-google-translate-key` | Cloud Translation API v2 key |
| `JWT_SECRET` | Yes in production | `development-jwt-secret-change-me-at-least-32-characters-123456` | HMAC signing key (32+ characters) |
| `SPRING_PROFILES_ACTIVE` | No | `dev` | Set to `real` to use Google Translation instead of the fake provider |
| `SPRING_DATASOURCE_URL` | No | `jdbc:postgresql://localhost:5432/immersion` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | No | `immersion` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | No | `immersion` | Database password |

### Frontend

| Variable | Required | Purpose |
|---|---|---|
| `VITE_GOOGLE_CLIENT_ID` | Yes | Google OAuth client ID rendered by `GoogleOAuthProvider` |

`frontend/.env` is git-ignored; commit only `.env.example`.

## API reference

All endpoints except the two login routes require `Authorization: Bearer <token>`.

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/auth/google` | Exchange a Google ID token for a JWT |
| `POST` | `/api/dev/login` | Dev-only login as `dev@local` (profile `dev`) |
| `GET` | `/api/me` | Current user |
| `GET` | `/api/me/settings` | Read settings |
| `PUT` | `/api/me/settings` | Update `nativeLang` and `pauseOnWordClick` |
| `GET` | `/api/contents` | List contents with cue counts |
| `POST` | `/api/contents` | Create a content (`LOCAL_FILE` or `MEDIA_URL`) |
| `GET` | `/api/contents/{id}` | Read one content |
| `DELETE` | `/api/contents/{id}` | Delete a content with its cues and translations |
| `POST` | `/api/contents/{id}/subtitles` | Upload an `.srt` or `.ass` file (multipart `file`) |
| `GET` | `/api/contents/{id}/cues?lang=` | Cues with the translation for `lang`, when it exists |
| `POST` | `/api/contents/{id}/translate?lang=` | Translate and store every cue for `lang` |
| `POST` | `/api/translate/word` | Translate a single word: `{ "text", "targetLang" }` |
| `GET` | `/api/words?page=&size=` | Paginated saved words |
| `POST` | `/api/words` | Save a word with its sentence and position |
| `DELETE` | `/api/words/{id}` | Delete a saved word |

Errors return `{ "status", "message", "timestamp" }`.

## Quality checks

```bash
cd backend && ./mvnw test
cd frontend && npm run build && npm run lint
```

## Production notes

- Set a strong `JWT_SECRET`; the default value is for local development only.
- Run with `SPRING_PROFILES_ACTIVE=real` so the dev login endpoint and the fake translation provider are disabled.
- Use real database credentials instead of the `immersion/immersion` defaults.
- Serve the built frontend (`npm run build`) from any static host and point `/api` at the backend.

## License

Released under the MIT License. See [LICENSE](LICENSE).
