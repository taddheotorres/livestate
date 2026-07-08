# Livestate — Plataforma de Bienes Raíces

Aplicación full-stack para rentar y comprar propiedades, con catálogo visual, autenticación JWT, reservas, pagos con Stripe, mensajería en tiempo real y dashboard multirol.

## Stack

| Capa | Tecnología |
|---|---|
| Frontend | Angular 17, TypeScript, SCSS, RxJS, SSR |
| Backend | Java 17, Spring Boot 4, Maven, JPA/Hibernate |
| Base de datos | MySQL 8.0 |
| Tiempo real | STOMP over WebSocket |
| Pagos | Stripe Checkout Sessions |
| Infraestructura | Docker, Docker Compose, Railway |

## Funcionalidades

### Catálogo y búsqueda
- Galería de propiedades con imágenes, filtros por tipo/precio/habitaciones
- Vista detallada con carrusel de imágenes, especificaciones y mapa
- Modo oscuro / claro

### Autenticación y roles
- Registro e inicio de sesión con JWT (roles: ADMIN, AGENT, USER)
- Protección de rutas con `authGuard`
- Interceptor HTTP que adjunta token automáticamente

### Perfil de agente
- Panel de administración con CRUD de propiedades
- Gestión de solicitudes de renta y visitas
- Perfil público editable con reseñas y calificaciones

### Usuario inquilino
- Favoritos (lista de deseos)
- Solicitud de visitas y reservas
- Seguimiento de estado de solicitudes

### Pagos
- Integración con Stripe Checkout Sessions (MXN)
- Pago con tarjeta y transferencia

### Mensajería
- Chat en tiempo real vía WebSocket (STOMP)
- Notificaciones push vía WebSocket
- Historial de conversaciones

### Diseño responsivo
- UI moderna con paleta earth-toned
- Animaciones suaves y transiciones
- Totalmente responsivo (mobile/tablet/desktop)

## Demo

### Credenciales de prueba

| Rol | Email | Contraseña |
|---|---|---|
| Agente | `nelva@livestate.com` | `nelva123` |
| Usuario | Regístrate en `/register` | — |

> La BD se seedea automáticamente al primer inicio con 3 propiedades de ejemplo y el agente "Nelva Torres".

### Stripe
Usa una tarjeta de prueba de Stripe: `4242 4242 4242 4242`, cualquier fecha futura, cualquier CVC.

## Inicio rápido con Docker

```bash
# 1. Clonar
git clone https://github.com/taddheotorres/livestate.git
cd livestate

# 2. Variables de entorno (opcional solo para Stripe)
cp .env.example .env
# Editar STRIPE_SECRET_KEY si se desea pagos reales

# 3. Levantar todo
docker compose up -d --build

# 4. Abrir
http://localhost:80
```

> Sin `STRIPE_SECRET_KEY` real, la pasarela usará una clave dummy; el resto de la app funciona completo.

## Desarrollo local

### Backend

Requiere MySQL 8.0 corriendo en `localhost:3306`.

```bash
cd backend
./mvnw spring-boot:run
# API en http://localhost:8081
# Health: http://localhost:8081/actuator/health
```

### Frontend

```bash
cd frontend
npm install
npm start  # ng serve con proxy a :8081
# App en http://localhost:4200
```

> `proxy.conf.json` redirige `/api/*` y `/ws` al backend automáticamente.

### Tests

```bash
# Backend (requiere Docker para Testcontainers)
cd backend && ./mvnw test

# Frontend
cd frontend && npm test
```

## Arquitectura

```
livestate/
├── backend/                      # Spring Boot 4 + Maven
│   ├── src/main/java/
│   │   └── com/realestate/api/
│   │       ├── config/           # Seguridad, WebSocket, CORS, seed data
│   │       ├── controller/       # REST endpoints (8 controladores)
│   │       ├── dto/              # Request/Response + MapStruct mappers
│   │       ├── exception/        # GlobalExceptionHandler
│   │       ├── model/            # Entidades JPA (7 entidades)
│   │       ├── repository/       # Spring Data JPA repositories
│   │       ├── security/         # JWT filter, service, SecurityUtils
│   │       └── service/          # Lógica de negocio (8 servicios)
│   ├── src/test/                 # Tests de integración con Testcontainers
│   └── pom.xml
│
├── frontend/                     # Angular 17 standalone + SSR
│   ├── src/app/
│   │   ├── core/
│   │   │   ├── auth/             # AuthService, guard, interceptor
│   │   │   ├── models/           # Interfaces TypeScript
│   │   │   ├── services/         # ApiService + servicios específicos
│   │   │   ├── navbar/           # Navbar con theme toggle
│   │   │   └── footer/
│   │   ├── features/
│   │   │   ├── home/             # Hero carrusel + propiedades destacadas
│   │   │   ├── catalog/          # Catálogo con filtros
│   │   │   ├── auth/             # Login / Register
│   │   │   ├── property-detail/  # Galería, booking, visitas, chat
│   │   │   ├── property-form/    # CRUD con preview + drag-drop imágenes
│   │   │   ├── dashboard/        # Panel host/tenant con tabs
│   │   │   └── agent-profile/    # Perfil público del agente
│   │   └── shared/               # PropertyCard, Toast
│   ├── public/images/            # Imágenes de ejemplo (15 assets)
│   └── angular.json
│
├── docker-compose.yml            # MySQL + Backend + Frontend
└── .github/workflows/ci.yml      # CI/CD: build, test, docker, deploy
```

### API REST

| Método | Endpoint | Auth | Descripción |
|---|---|---|---|
| POST | `/api/auth/register` | — | Registro |
| POST | `/api/auth/login` | — | Login → JWT |
| GET | `/api/properties` | — | Listar propiedades |
| GET | `/api/properties/{id}` | — | Detalle |
| POST | `/api/properties` | JWT | Crear propiedad (AGENT) |
| PUT | `/api/properties/{id}` | JWT | Editar propiedad |
| DELETE | `/api/properties/{id}` | JWT | Eliminar propiedad |
| POST | `/api/bookings` | JWT | Crear reserva |
| POST | `/api/visits` | JWT | Agendar visita |
| POST | `/api/messages` | JWT | Enviar mensaje |
| POST | `/api/payments/create-checkout-session` | JWT | Cobro Stripe |
| POST | `/api/favorites/{id}/toggle` | JWT | Favorito on/off |
| GET | `/api/users/me` | JWT | Perfil actual |
| PUT | `/api/users/me` | JWT | Editar perfil |

## Decisiones técnicas

- **Standalone Components** — Angular 17 sin NgModules, lazy loading en todas las rutas
- **MapStruct** — Mapeo automático entity ↔ DTO, evitando boilerplate
- **Testcontainers** — Tests de integración con MySQL real (no H2), Docker requerido
- **WebSocket con STOMP** — Mensajería en tiempo real con autenticación JWT en el handshake
- **Imágenes en base64** — Soporte para upload por drag & drop, URL o presets
- **Stripe Checkout** — Sesiones de pago en MXN, modo test con tarjeta 4242
- **SSR** — Angular Universal para renderizado del lado servidor (Express)

## Roadmap

- [ ] Notificaciones push (Web Push API)
- [ ] Búsqueda por ubicación (Google Maps / Mapbox)
- [ ] Panel de administración global
- [ ] Gestión de reseñas y calificaciones
- [ ] Subida de imágenes a S3/Cloudinary

---

Desarrollado por [Taddheo Torres](https://github.com/taddheotorres)
