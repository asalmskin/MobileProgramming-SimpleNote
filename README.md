# SimpleNoteApp

A modern, offline-first note-taking Android app built with Kotlin and Jetpack Compose, backed by a Django REST API.

## 🚀 Features

- **User Authentication**: JWT-based login, registration, change-password, and token refresh  
- **Notes CRUD**: Create, read, update, and delete notes with paginated listing  
- **Search**: Filter notes by title or description  
- **Offline-First**: Local caching with Room for seamless use without network  
- **Profile Management**: View user info and logout

## 🛠 Tech Stack

- **Android**:  
  - Kotlin & Jetpack Compose  
  - Hilt for dependency injection  
  - Retrofit & OkHttp (with interceptor + authenticator)  
  - Room (SQLite) & DataStore (token storage)  
  - Coroutines & Flow  
- **Backend**:  
  - Django REST Framework  
  - PostgreSQL  
  - Docker & Docker Compose  
