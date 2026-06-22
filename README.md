# LightWeight

LightWeight is an Android fitness application for creating, managing, scheduling and sharing workout plans.

## Requirements

Before running the project, install:

- Docker Desktop
- Android Studio
- Git
- Android Emulator or a physical Android device

## Project Structure

```text
LightWeight/ 
    backend/            # Node.js / Express backend 
    frontend/           # Android app 
    db/init/            # PostgreSQL init scripts and seed data 
    docker-compose.yml  # Docker setup for backend and database 
    .env                # Environment variables
```

## Environment Variables

Create a `.env` file in the project root with the help of following example:

```env
POSTGRES_DB=lightweight
POSTGRES_USER=lightweight
POSTGRES_PASSWORD=lightweight
POSTGRES_PORT=5432

BACKEND_PORT=3000
DATABASE_URL=postgresql://lightweight:lightweight@db:5432/lightweight

JWT_SECRET=lightweight_super_secret
JWT_EXPIRES_IN=7d
```

## Start Backend and Database

From the project root, run:
```bash
docker compose up --build
```

## Reset Backend and Database

From the project root, run:
```bash
docker compose down -v
docker compose up --build
```

Warning: This deletes all database data and recreates the database from the init scripts.

## Run Android App

Open the `frontend/` folder in Android Studio and wait for the Gradle sync to finish.

Before running the app, check the backend URL in:

```text
frontend/app/src/main/java/com/example/lightweight/data/remote/RetrofitClient.kt
```

For an Android Emulator, use:

```kotlin
const val BASE_URL = "http://10.0.2.2:3000/"
```

For a physical Android device, replace `BASE_URL` with the local IP address of the computer running Docker, for example:

```kotlin
const val BASE_URL = "http://10.0.0.15:3000/"
```

The physical device and the computer must be connected to the same network.
Afterwards, select an emulator or physical device in Android Studio and run the app.
