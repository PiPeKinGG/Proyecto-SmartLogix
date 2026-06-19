<div align="center">

# SmartLogix — Auth Service

**Microservicio de autenticación y gestión de seguridad para el ecosistema SmartLogix.**

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

</div>

---

## Descripción General

El **Auth Service** es el microservicio encargado de gestionar la seguridad, la autenticación de usuarios y la emisión de tokens JWT dentro del ecosistema de SmartLogix.

> **Nota:** Este servicio **no almacena** la información completa de los usuarios. En su lugar, se comunica de forma interna con el `user-service` a través de **OpenFeign** para validar las credenciales ingresadas.

---

## Integración en la Arquitectura

Este servicio actúa como puerta de entrada al sistema de seguridad. El flujo de comunicación es el siguiente:

```
React (Frontend)  →  API Gateway (:8080)  →  Auth Service (:8081)  →  OpenFeign  →  User Service
```

---

## Tecnologías Utilizadas

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework Core | Spring Boot 3 |
| Seguridad | Spring Security + JWT |
| Base de Datos | PostgreSQL |
| Comunicación Interna | Spring Cloud OpenFeign |
| Service Discovery | Spring Cloud Netflix Eureka Client |
| Testing | JUnit 5 + Mockito |

---

## Requisitos Previos

Para levantar este microservicio de forma **local** (fuera de Docker), asegúrate de tener instalado:

- ☕ **JDK 17** o superior
- 📦 **Maven 3.8** o superior
- 🐘 **PostgreSQL** corriendo con una base de datos llamada `smartlogix_auth`
- 🔍 **Eureka Server** corriendo en el puerto `8761`

---

## Variables de Entorno

El servicio corre en el puerto **`8081`** y se configura mediante el archivo `application.yml`. Las siguientes variables de entorno son requeridas:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `SPRING_DATASOURCE_URL` | Ruta de conexión a PostgreSQL | `jdbc:postgresql://localhost:5432/smartlogix_auth` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos | `yourpassword` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL del servidor Eureka | `http://localhost:8761/eureka/` |
| `JWT_SECRET` | Clave secreta para la firma de tokens JWT | `your-secret-key` |

---

## Instrucciones de Ejecución

### 1. Ejecución Local (Desarrollo)

Para correr el servicio directamente desde tu IDE o terminal:

**Paso 1 — Compilar el proyecto y descargar dependencias:**
```bash
mvn clean install -DskipTests
```

**Paso 2 — Levantar la aplicación:**
```bash
mvn spring-boot:run
```

### 2. Ejecución vía Docker (Producción / Integración)

Este microservicio está configurado para desplegarse junto a la arquitectura completa a través de **Docker Compose**. Desde la raíz del repositorio principal (donde se encuentra el `docker-compose.yml`):

```bash
docker-compose up -d auth-service
```

---

## Pruebas Unitarias

El servicio cuenta con una cobertura de pruebas unitarias del **100%**, validando de forma íntegra:

- **`AuthController`** — Capa de controlador
- **`AuthService`** — Lógica de generación de tokens

Los componentes son aislados mediante **Mocks**, evitando llamadas reales a la base de datos.

Para ejecutar la suite de pruebas:

```bash
mvn clean test
```

---

## Endpoints Principales

> Aunque el servicio opera en el puerto `8081`, el acceso desde el exterior debe realizarse siempre a través del **API Gateway** en el puerto `8080`.

### `POST` `/auth/login` — Autenticación y Generación de Token

**URL vía Gateway:** `http://localhost:8080/auth/login`

**Request Body:**
```json
{
  "email": "admin@empresa.cl",
  "password": "password"
}
```

**Respuesta Exitosa — `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```
