# Immersion Hub — frontend

React 19 + TypeScript + Vite single-page app for Immersion Hub.

## Development

```bash
npm install
cp .env.example .env   # set VITE_GOOGLE_CLIENT_ID
npm run dev
```

The dev server runs on `http://localhost:5173` and proxies `/api` to the backend on `http://localhost:8080`.

## Scripts

| Command | Purpose |
|---|---|
| `npm run dev` | Start Vite with HMR |
| `npm run build` | Type-check with `tsc -b` and build to `dist/` |
| `npm run lint` | Run ESLint |
| `npm run preview` | Serve the production build locally |

## Layout

```
src/
├── api/          Axios client and shared API types
├── components/   layout, cue list, clickable text, word popup, route guard
├── context/      auth and settings providers
├── pages/        login, library, new content, player, saved words, settings
└── utils/        active cue lookup
```

Full setup, environment variables and API reference live in the [root README](../README.md).
