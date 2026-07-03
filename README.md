# Livestate — Encuentra tu espacio

Plataforma full-stack para rentar o comprar propiedades únicas. Catálogo visual con autenticación, reservas, pagos con Stripe y mensajería en tiempo real.

## Live

[**livestate.up.railway.app**](https://livestate.up.railway.app)

## Stack

| Capa | Tecnología |
|---|---|
| Frontend | Angular 17, TypeScript, SCSS |
| Backend | Java 17, Spring Boot 4, Maven |
| Base de datos | MySQL 8.0 |
| Infraestructura | Docker, Railway |

### Funcionalidades

- Catálogo de propiedades con imágenes y búsqueda
- Autenticación JWT (registro/login)
- Perfil de agente inmobiliario
- Solicitud de visitas y reservas
- Pagos integrados con Stripe
- Mensajería en tiempo real (WebSocket)
- Panel de administración (dashboard)
- Diseño responsivo con modo oscuro

## Ejecutar local

```bash
# Backend
cd backend && ./mvnw package -DskipTests && java -jar target/*.jar

# Frontend
cd frontend && npm install && npm start
```

Requiere MySQL corriendo en `localhost:3306` y variables de entorno (ver `.env.example`).
