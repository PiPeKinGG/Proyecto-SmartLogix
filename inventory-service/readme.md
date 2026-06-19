<div align="center">

# SmartLogix — Inventory Service

**Microservicio de gestión de catálogo de productos y control de stock para el ecosistema SmartLogix.**

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-C00?style=for-the-badge&logo=java&logoColor=white)

</div>

---

## Descripción General

El **Inventory Service** es el componente responsable de administrar el inventario físico y la disponibilidad de los productos. A diferencia de un CRUD tradicional, este servicio implementa consultas nativas (JPQL) en su capa de datos para garantizar actualizaciones atómicas, previniendo condiciones de carrera evitando la sobreventa de productos en momentos de alta concurrencia.

---

## Integración en la Arquitectura

Este servicio recibe tráfico tanto desde el exterior (para listar productos en el Frontend) como de forma interna (cuando se procesa una compra).

**Flujo de Lectura / Catálogo:**
```
React (Frontend)  →  API Gateway (:8080)  →  Inventory Service (:8083)
```

**Flujo Transaccional:**
```
Order Service  →  OpenFeign  →  Inventory Service (:8083)  →  Reserva de Stock
```

---

## Tecnologías Utilizadas

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework Core | Spring Boot 3 |
| Persistencia de Datos | Spring Data JPA / Hibernate |
| Base de Datos | PostgreSQL *(Patrón Database-per-service)* |
| Service Discovery | Spring Cloud Netflix Eureka Client |
| Testing y Cobertura | JUnit 5, Mockito y JaCoCo |

---

## Requisitos Previos

Para levantar este microservicio de forma local (fuera de Docker), asegúrate de tener instalado:

- **JDK 17** o superior
- **Maven 3.8** o superior
- **PostgreSQL** corriendo de forma local con una base de datos llamada `smartlogix_inventory`
- **Eureka Server** corriendo en el puerto `8761` para permitir el registro en la red

---

## Variables de Entorno

El servicio corre internamente en el puerto **`8083`** y se configura mediante el archivo `application.yml`. Requiere las siguientes variables de entorno:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `SPRING_DATASOURCE_URL` | Ruta de conexión a la BD PostgreSQL | `jdbc:postgresql://localhost:5432/smartlogix_inventory` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos | `tu_contraseña` |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL del servidor Eureka | `http://localhost:8761/eureka/` |

---

## Instrucciones de Ejecución

### 1. Ejecución Local (Desarrollo)

**Paso 1 — Compilar el proyecto y descargar dependencias:**
```bash
mvn clean install -DskipTests
```

**Paso 2 — Levantar la aplicación:**
```bash
mvn spring-boot:run
```

### 2. Ejecución vía Docker (Producción / Integración)

Este microservicio está configurado para desplegarse junto a la arquitectura completa a través de **Docker Compose**. Desde la raíz del repositorio principal:

```bash
docker-compose up -d inventory-service
```

---

## Pruebas Unitarias y Cobertura (JaCoCo)

El servicio cuenta con una robusta suite de pruebas unitarias que supera el **60% de cobertura**, validando:

- **`ProductController`** — Respuesta correcta de los endpoints HTTP.
- **`ProductService`** — Lógica algorítmica estricta para la asignación, reserva, confirmación y cancelación de stock.

Se utilizan Mocks para aislar el `ProductRepository`, agilizando los tiempos de compilación y asegurando que las reglas de negocio sean independientes del motor de base de datos.

Para ejecutar la suite de pruebas y generar el reporte de cobertura:

```bash
mvn clean test jacoco:report
```

**Visualización de los resultados:**

- **Consola:** El resumen de las pruebas exitosas se imprimirá al finalizar la ejecución.
- **Reporte HTML:** JaCoCo generará un reporte visual detallado en `target/site/jacoco/index.html`. Ábrelo en cualquier navegador web.

---

## Endpoints Principales

> **Importante:** Debido a la arquitectura Multi-Tenant del sistema, todos los endpoints de este microservicio requieren obligatoriamente el header `pyme_id` para identificar de qué tienda es el inventario.

### `GET /products` — Listar Catálogo

**URL vía Gateway:** `http://localhost:8080/products`

**Headers requeridos:**
```
Authorization: Bearer <token>
pyme_id: 1
```

**Respuesta Exitosa — `200 OK`:**
```json
[
  {
    "id": 1,
    "name": "Teclado Mecánico",
    "availableQuantity": 100,
    "price": 45000.0
  }
]
```

---

### `POST /products/reserve` — Reservar Stock *(Uso Interno)*

**URL vía Gateway:** `http://localhost:8080/products/reserve?productId=1&quantity=2`

**Headers requeridos:**
```
Authorization: Bearer <token>
pyme_id: 1
```

**Respuesta Exitosa — `200 OK`:**
```
Stock reservado
```
