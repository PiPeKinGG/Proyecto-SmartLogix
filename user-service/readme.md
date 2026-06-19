# User Service - SmartLogix

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-C00?style=for-the-badge&logo=java&logoColor=white)



## Descripción General

El User Service es el componente encargado de centralizar el dominio de los usuarios dentro de la plataforma. Persiste los datos de las cuentas, registra nuevos clientes u operadores, y gestiona la información de contacto y perfiles.

A diferencia de un sistema monolítico, este servicio expone controladores dedicados para peticiones externas del cliente, así como endpoints internos (Internal Controllers) optimizados para que otros microservicios (como el Auth Service) validen la existencia y credenciales de los usuarios de manera segura.

> El puerto asignado para este servicio es `8082`
## Stack Tecnológico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework Core | Spring Boot 3 |
| Persistencia de Datos | Spring Data JPA / Hibernate |
| Base de Datos | PostgreSQL (patrón Database-per-service) |
| Mensajería Asíncrona | Apache Kafka |
| Service Discovery | Spring Cloud Netflix Eureka Client |
| Testing y Cobertura | JUnit 5, Mockito, JaCoCo |

## Integración en la Arquitectura

Este microservicio se acopla con el ecosistema a través de los siguientes flujos de comunicación:

### Flujo de Exposición REST (Público / Protegido)

```text
React (Frontend) -> API Gateway (:8080) -> User Service (:8082) -> PostgreSQL (smartlogix_users)
```

### Flujo de Autenticación Inter-Servicio (Síncrono)

```text
Auth Service (:8081) -> (OpenFeign) -> User Service (:8082/internal/users) -> Verificacion de Credenciales
```

## Requisitos Previos

Para levantar este microservicio de forma local (fuera de Docker), asegúrate de tener instalado:

- JDK 17 o superior
- Maven 3.8 o superior
- PostgreSQL corriendo localmente con una base de datos llamada `smartlogix_users`
- Apache Kafka configurado en el puerto 9092
- Eureka Server activo en el puerto 8761

## Variables de Entorno

El servicio se configura mediante el archivo `application.yml`. Cuando se ejecuta dentro de contenedores, Docker Compose inyecta dinámicamente las siguientes variables clave:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `SPRING_DATASOURCE_URL` | URL de conexión a la BD PostgreSQL | `jdbc:postgresql://postgres:5432/smartlogix_users` |
| `SPRING_DATASOURCE_USERNAME` | Usuario administrador de la BD | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de acceso a la BD | `password` |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Dirección del broker de Apache Kafka | `kafka:9092` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Endpoint de registro en Eureka | `http://eureka-server:8761/eureka/` |

## Instrucciones de Ejecución

### 1. Ejecución Local (Desarrollo)

Paso 1 — Compilar el proyecto y descargar dependencias:

```bash
mvn clean install -DskipTests
```

Paso 2 — Levantar la aplicación:

```bash
mvn spring-boot:run
```

### 2. Ejecución vía Docker (Producción / Integración)

Desde la raíz del repositorio principal, puedes compilar e iniciar el contenedor de forma aislada ejecutando:

```bash
docker-compose up -d user-service
```

## Pruebas Unitarias y Cobertura (JaCoCo)

El microservicio cuenta con una batería de pruebas automatizadas destinadas a asegurar que las capas de negocio (`UserService`), persistencia (`UserRepository`) y exposición de endpoints manejen correctamente las excepciones globales y los flujos de datos.

```bash
mvn clean test jacoco:report
```

> El reporte visual (HTML) se genera en `target/site/jacoco/`. Abre `index.html` en tu navegador para auditar la cobertura interactiva de código.

## Endpoints Principales

### POST /users — Registro de Usuario (Tráfico Externo)

URL vía Gateway: `http://localhost:8080/users`

Request Body:

```json
{
  "name": "Matias Guzman",
  "email": "matias.guzman@smartlogix.com",
  "password": "password123",
  "role": "CLIENTE"
}
```

Respuesta exitosa — 201 Created:

```json
{
  "id": 42,
  "name": "Matias Guzman",
  "email": "matias.guzman@smartlogix.com",
  "role": "CLIENTE"
}
```

### POST /internal/users/verify — Verificar Credenciales (Uso Interno - OpenFeign)

Endpoint expuesto de manera interna para consumo del `auth-service`.

URL de consulta interna: `http://user-service:8082/internal/users/verify`

Request Body:

```json
{
  "email": "matias.guzman@smartlogix.com",
  "password": "password"
}
```

Respuesta exitosa — 200 OK:

```json
{
  "verified": true,
  "userId": 42,
  "role": "ADMIN"
}
```
