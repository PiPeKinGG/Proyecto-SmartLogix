# SmartLogix - Backend

SmartLogix es una plataforma de gestión logística construida bajo una arquitectura de microservicios orientada a eventos. Este repositorio contiene el backend completo del sistema, incluyendo los servicios de negocio, infraestructura de mensajería, bases de datos y descubrimiento de servicios.

---

## Tecnologías principales

- **Java / Spring Boot** — Framework principal para los microservicios.
- **PostgreSQL 15** — Base de datos relacional, una instancia con múltiples bases de datos lógicas.
- **Apache Kafka 3.8.0** — Broker de mensajería para comunicación asíncrona (modo KRaft, sin Zookeeper).
- **Netflix Eureka** — Registro y descubrimiento de servicios.
- **Spring Cloud Gateway** — API Gateway para enrutamiento centralizado.
- **Docker y Docker Compose** — Contenerización y orquestación local.

---

## Arquitectura de servicios

### Infraestructura base

| Servicio | Puerto | Descripción |
|---|---|---|
| `postgres-db` | 5432 | Instancia central de PostgreSQL |
| `kafka` | 9092 | Nodo Kafka autónomo para eventos del sistema |
| `eureka-server` | 8761 | Registro central de microservicios |

### API Gateway

| Servicio | Puerto | Descripción |
|---|---|---|
| `api-gateway` | 8080 | Punto de entrada único para clientes (Frontend/Móvil) |

### Microservicios de negocio

| Servicio | Descripción |
|---|---|
| `user-service` | Gestión de usuarios y perfiles |
| `auth-service` | Autenticación, autorización y emisión de tokens |
| `order-service` | Ciclo de vida de los pedidos |
| `inventory-service` | Control de stock y catálogo de productos |
| `shipping-service` | Gestión de envíos, despachos y estrategias de entrega |
| `notification-service` | Alertas por email/SMS basadas en eventos del sistema |

---

## Requisitos previos

- Docker y Docker Compose instalados
- Java 17 o superior (para desarrollo local)
- Maven (para compilación local)

---

## Despliegue local

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd Proyecto-SmartLogix
```

### 2. Levantar el entorno completo

```bash
docker-compose up -d --build
```

Docker Compose construye las imágenes de los microservicios y levanta la infraestructura en el orden correcto mediante `depends_on` y `healthcheck`.

### 3. Verificar el estado

- **Eureka Dashboard:** `http://localhost:8761` — muestra los servicios registrados y activos.
- **API Gateway:** `http://localhost:8080` — punto de entrada para todas las peticiones.

### 4. Detener el entorno

```bash
docker-compose down
```

Para borrar también los volúmenes de base de datos:

```bash
docker-compose down -v
```

---

## Inicialización de datos

Al levantar el contenedor de PostgreSQL por primera vez, el script `init-databases.sql` se ejecuta automáticamente y aprovisiona las bases de datos necesarias para cada microservicio.

Las credenciales por defecto para el entorno local son `postgres` / `password`.