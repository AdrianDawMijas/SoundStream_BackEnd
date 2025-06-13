# SoundStream Backend

Este repositorio contiene el backend de **SoundStream**, una plataforma para la generación y gestión de música mediante inteligencia artificial. Implementado con **Java + Spring Boot**, este backend ofrece autenticación JWT, integración con una API externa de generación musical y un sistema completo de gestión de usuarios, canciones y playlists.

---

## 🚀 Tecnologías principales

* Java 17
* Spring Boot 3
* Spring Security + JWT
* Hibernate / JPA
* Lombok
* JavaCV / FFmpeg (procesado de audio)
* Swagger / OpenAPI

---

## 📁 Estructura del proyecto

```
com.iesvegademijas.soundstream_backend
├── config            # Seguridad, Swagger, CORS y almacenamiento
├── controller        # Controladores REST para usuarios, canciones, playlists, etc.
├── dto               # Clases de transferencia de datos (Login, Registro, Canciones)
├── model             # Entidades JPA (User, Song, Playlist, etc.)
├── repository        # Interfaces de acceso a datos
├── service           # Lógica de negocio
├── utils             # Utilidades para generación AI, audio y seguridad
└── SoundStreamBackendApplication.java
```

---

## 🔐 Seguridad y autenticación

Configurada mediante **Spring Security + JWT**, la autenticación incluye:

* **Login clásico** con email y password
* **Login con Google Token**
* **Roles:** `USER` y `ADMIN`
* **Filtros protegidos** por roles (`/admin/**`)

Archivos relevantes:

* `SecurityConfig.java`: define los filtros, CORS, rutas protegidas
* `JwtAuthenticationFilter.java`: extrae y valida el JWT por petición
* `JwtService.java`: genera y valida tokens
* `UserDetailsServiceImpl.java`: implementación personalizada para cargar usuarios

---

## 🌐 API REST

Documentada automáticamente con Swagger en `/swagger-ui.html`

Principales endpoints:

### Usuarios

* `POST /v1/api/users` → Registro
* `POST /v1/api/users/login` → Login clásico
* `POST /v1/api/users/login/google-token` → Login con Google

### Canciones

* `POST /v1/api/songs/generate` → Genera música con IA
* `GET /v1/api/songs/random` → Canciones públicas aleatorias

### Playlist

* `POST /v1/api/playlist/add` → Añadir canción favorita
* `GET /v1/api/playlist/me` → Mis canciones

### Admin (sólo ADMIN)

* `GET /v1/api/admin/songs/all` → Listado completo de canciones generadas

---

## 🎵 Integración con IA externa (PIAPI)

En `PiapiDiffRhythmClient.java` se encuentra el cliente HTTP que:

* Envia prompts generados al modelo `music-u` o `Qubico/diffrhythm`
* Recupera y procesa los resultados generados por la IA
* Implementa fallback si falla el primer modelo

Resultado encapsulado en `GeneratedSongResult.java`

---

## 🎧 Procesado de audio

En `AudioTrimmerService.java` se implementa:

* Descarga de archivo desde una URL
* Recorte de audio usando **JavaCV + FFmpeg**
* Genera archivos `.mp3` temporales para reproducibilidad en frontend

---

## 📊 DTOs principales

* `LoginRequestDTO`, `RegisterRequestDTO` → Login / Registro
* `LoginResponseDTO` → Contiene el JWT + datos del usuario
* `SongDTO` → Prompt completo para generación musical
* `SongAddDTO` → ID de canción para añadir a favoritos

---

## 📃 Configuración personalizada

### OpenAPI / Swagger

`OpenApiConfig.java` define título, versión y licencia.

### CORS

Permitido sólo desde `http://localhost:4200` (Angular frontend).

### Propiedades externas

`SongStorageProperties.java` incluye ruta de almacenamiento y URL base. Puede eliminarse si ya no se usa localmente.

---

## ✅ Pendiente o posible mejora

* Implementar refresh tokens (opcional)
* Test unitarios con MockMvc / Mockito
* Validar recorte de audio de forma asíncrona
* Mejorar control de errores y logs

---

## 🎨 Autor y créditos

Desarrollado como parte del Proyecto Integrado del ciclo **DAW**.

> "SoundStream busca democratizar la generación de música usando IA a través de una interfaz accesible y moderna."

---

## 🔗 Enlaces relacionados

* [Frontend Angular (Vercel)](https://soundstream.vercel.app)
* [API Swagger UI](http://localhost:8080/swagger-ui.html)

---

© 2025 SoundStream. Todos los derechos reservados.
