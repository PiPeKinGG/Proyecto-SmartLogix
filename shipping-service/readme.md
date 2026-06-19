# Shipping Service - SmartLogix

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-C00?style=for-the-badge&logo=java&logoColor=white)

## Descripción General

El Shipping Service se encarga de la logística una vez que una orden ha sido validada y pagada. Genera los números de seguimiento (Tracking IDs), asigna los tiempos estimados de entrega y calcula los costos de despacho.

Este servicio destaca por la implementación de los patrones de diseño Factory y Strategy en su capa de negocio, permitiendo calcular dinámicamente las condiciones del envío dependiendo de si el cliente seleccionó despacho STANDARD o EXPRESS, facilitando la escalabilidad si en el futuro se añaden nuevos métodos de envío.

> Las consultas externas a este servicio requieren el header `pyme_id` además del token de autenticación, siguiendo el esquema Multi-Tenant ya definido en `inventory-service`.

## Stack Tecnológico

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework Core | Spring Boot 3 |
| Patrones de Diseño | Strategy Pattern, Factory Pattern |
| Base de Datos | PostgreSQL |
| Mensajería Asíncrona | Apache Kafka |
| Service Discovery | Spring Cloud Netflix Eureka Client |
| Testing y Cobertura | JUnit 5, Mockito, JaCoCo |

## Integración en la Arquitectura

Este servicio reacciona a los eventos de la plataforma de forma asíncrona, pero también expone endpoints síncronos para la consulta de estado de los paquetes.

### Flujo Asíncrono (Generación de Envío)

```text
Order Service -> (Kafka) -> Emite 'order-confirmed'
                                |
                                +-> Shipping Service (escucha el evento)
                                |
                                +-> Calcula estrategia (Factory: Express/Standard) -> Guarda en BD
                                |
                                +-> (Kafka) -> Emite 'shipment-dispatched' (capturado por Notification Service)
```

### Flujo Síncrono (Consulta de Tracking)

```text
React (Frontend) -> API Gateway (:8080) -> Shipping Service (:8085) -> Consulta BD PostgreSQL
```

## Requisitos Previos

Para levantar este microservicio de forma local (fuera de Docker), asegúrate de tener instalado:

- JDK 17 o superior
- Maven 3.8 o superior
- PostgreSQL corriendo localmente con una base de datos destinada a los envíos (`smartlogix_shipping`)
- Apache Kafka y Zookeeper corriendo en el puerto 9092
- Eureka Server activo en el puerto 8761

## Variables de Entorno

El servicio corre internamente en el puerto 8085 y se configura mediante el archivo `application.yml`. Las siguientes variables son requeridas:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `SPRING_DATASOURCE_URL` | Ruta de conexión a la BD PostgreSQL | `jdbc:postgresql://localhost:5432/smartlogix_shipping` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos | `password` |
| `KAFKA_BOOTSTRAP_SERVERS` | Ruta del broker de Kafka | `localhost:9092` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL del servidor Eureka | `http://localhost:8761/eureka/` |

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

Este microservicio está configurado para desplegarse junto a la arquitectura completa a través de Docker Compose. Desde la raíz del repositorio principal:

```bash
docker-compose up -d shipping-service
```

## Pruebas Unitarias y Cobertura (JaCoCo)

El servicio cuenta con una sólida suite de pruebas unitarias enfocada en validar la inyección de dependencias, la lógica algorítmica de los patrones Strategy/Factory y la correcta recepción/emisión de eventos en Kafka a través de `ShippingEventListener`.

```bash
mvn clean test jacoco:report
```

> El reporte visual (HTML) se genera en `target/site/jacoco/`. Abre `index.html` en tu navegador para auditar la cobertura de código.

## Endpoints Principales

Las consultas externas deben ser enrutadas mediante el API Gateway y deben contener los headers obligatorios de autenticación y tenencia (Multi-Tenant).

### GET /shipping/tracking/{trackingId} — Rastrear un Envío

URL vía Gateway: `http://localhost:8080/shipping/tracking/a1b2c3d4-5678-90ef...`

Headers requeridos:

```text
Authorization: Bearer <token>
pyme_id: 50
```

Respuesta exitosa — 200 OK:

```json
{
  "id": 10,
  "orderId": 505,
  "pymeId": 1,
  "userId": 42,
  "shippingType": "EXPRESS",
  "cost": 5000.0,
  "estimatedDays": 1,
  "trackingId": "a1b2c3d4-5678-90ef...",
  "status": "DESPACHADO",
  "createdAt": "2026-06-19T10:00:00"
}
```
