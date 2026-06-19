# Order Service - SmartLogix

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-C00?style=for-the-badge&logo=java&logoColor=white)

## Descripción General

El Order Service es el microservicio central encargado de la gestión del ciclo de vida de los pedidos dentro de la plataforma SmartLogix. Actúa como orquestador que valida la intención de compra, verifica el inventario disponible y emite eventos para desencadenar el despacho y las notificaciones.

Implementa un enfoque de Saga / Coreografía mediante eventos para asegurar la consistencia de los datos distribuidos sin acoplar fuertemente los microservicios.

> Este servicio no expone tráfico directo: toda petición externa debe llegar enrutada a través del `api-gateway`, que ya valida el JWT antes de reenviar la solicitud.

## Stack Tecnológico

| Tecnología | Versión / Detalle |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 3.x |
| Base de datos | PostgreSQL |
| Mensajería | Apache Kafka |
| Service Discovery | Eureka Client |
| Comunicación síncrona | OpenFeign |
| Testing | JUnit 5, Mockito |
| Cobertura | JaCoCo |

## Arquitectura y Comunicación

Este microservicio mantiene una comunicación híbrida (síncrona y asíncrona) con el resto de la plataforma:

- **Recepción de peticiones:** recibe tráfico enrutado exclusivamente a través del `api-gateway`.
- **Comunicación síncrona (OpenFeign):** utiliza un cliente Feign (`InventoryClient`) para realizar consultas directas al `inventory-service`, por ejemplo validar la existencia de un producto antes de iniciar la transacción.
- **Comunicación asíncrona (Apache Kafka):**
  - Produce los eventos `OrderCreatedEvent` y `OrderConfirmedEvent`.
  - Consume los eventos `InventoryReservedSuccessEvent` e `InventoryReservedFailedEvent` para confirmar o compensar (cancelar) un pedido en curso.
- **Seguridad:** cuenta con su propio `SecurityConfig.java` para procesar peticiones que llegan con el JWT ya validado desde el Gateway.

### Flujo de la Orden

```text
Cliente -> API Gateway -> Order Service -> (Sincrono: OpenFeign) -> Inventory Service
                                      |
                                      +-> (Asincrono: Kafka) -> Emite 'OrderCreatedEvent'
                                      |
                                      +<- (Asincrono: Kafka) <- Escucha 'InventoryReservedSuccessEvent'
                                      |
                                      +-> (Asincrono: Kafka) -> Emite 'OrderConfirmedEvent' (capturado por Shipping/Notification)
```

> En caso de recibir `InventoryReservedFailedEvent`, el servicio ejecuta la transacción de compensación: el pedido pasa a estado `CANCELLED` y no se emite `OrderConfirmedEvent`.

## Estructura Principal del Código

| Carpeta | Contenido |
|---|---|
| `/controller` | Endpoints REST (`OrderController.java`) |
| `/service` | Lógica de negocio y gestión de la máquina de estados del pedido |
| `/entity` | Modelos de base de datos (`Order.java`, `OrderItem.java`) |
| `/event` | Listeners y records/DTOs de los eventos de Kafka |
| `/feign` | Clientes HTTP declarativos para comunicación directa entre microservicios |
| `/repository` | Interfaces de acceso a datos de Spring Data JPA |

## Requisitos Previos

- Java 21 
- Maven 3.8+
- Docker y Docker Compose (para ejecución en contenedores)
- Base de datos PostgreSQL corriendo localmente o en Docker
- Apache Kafka y Zookeeper activos
- Eureka Server activo en el puerto 8761

## Variables de Entorno

El servicio corre internamente en el puerto **`8084`** y se configura mediante el archivo `application.yml`. Las siguientes variables son requeridas:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `SPRING_DATASOURCE_URL` | Ruta de conexión a la BD PostgreSQL | `jdbc:postgresql://localhost:5432/smartlogix_orders` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos | `password` |
| `KAFKA_BOOTSTRAP_SERVERS` | Ruta del broker de Kafka | `localhost:9092` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL del servidor Eureka | `http://localhost:8761/eureka/` |

```yaml
server:
  port: 8084

spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/smartlogix_orders}
    username: ${SPRING_DATASOURCE_USERNAME:postgres}
    password: ${SPRING_DATASOURCE_PASSWORD:password}
  kafka:
    bootstrap-servers: ${SPRING_KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
eureka:
  client:
    serviceUrl:
      defaultZone: ${EUREKA_CLIENT_SERVICEURL_DEFAULTZONE:http://localhost:8761/eureka/}

jwt:
  secret: ${JWT_SECRET_KEY}
```

> `JWT_SECRET_KEY` debe ser idéntico al configurado en `auth-service` y en `api-gateway`, de lo contrario la validación del token fallará.

## Ejecución del Proyecto

### Modo Desarrollo (Local)

Asegúrate de que la base de datos, Kafka y Eureka estén corriendo. Luego ejecuta:

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

### Modo Producción (Docker)

Si estás utilizando la red de microservicios completa con el archivo `docker-compose.yml` raíz:

```bash
docker build -t smartlogix/order-service .
docker-compose up -d order-service
```

## Pruebas y Cobertura (Testing)

El microservicio cuenta con una suite de pruebas unitarias que cubre controladores, servicios, repositorios y listeners de eventos, utilizando JUnit 5 y Mockito.

```bash
mvn clean test jacoco:report
```

> El reporte HTML de cobertura se genera en `target/site/jacoco/index.html`.
