<div align="center">

# SmartLogix — API Gateway

**Punto de entrada único (BFF) y enrutador principal para el ecosistema SmartLogix.**

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Cloud Gateway](https://img.shields.io/badge/Spring_Cloud_Gateway-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-C00?style=for-the-badge&logo=java&logoColor=white)

</div>

---

## Descripción General

El **API Gateway** es el componente perimetral de la arquitectura. Actúa como un **Backend For Frontend (BFF)**, centralizando todo el acceso público. Sus responsabilidades principales son:

- Enrutamiento dinámico hacia los microservicios internos mediante descubrimiento de servicios.
- Gestión unificada de las políticas CORS para el cliente web.
- Validación global de tokens a través de un filtro perimetral (`Global JWT Auth Filter`) que protege la red interna.

---

## Integración en la Arquitectura

Este servicio es la **única cara visible** para el cliente web. Ningún microservicio interno es accesible directamente desde el exterior.

```
React (Frontend)  →  API Gateway (:8080)  →  Validación JWT  →  Microservicios (vía Eureka)
```

---

## Tecnologías Utilizadas

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework Core | Spring Boot 3 |
| Enrutamiento | Spring Cloud Gateway |
| Seguridad | Custom Global Filter (JWT Validation) |
| Service Discovery | Spring Cloud Netflix Eureka Client |
| Testing y Cobertura | JUnit 5, Mockito y JaCoCo |

---

## Requisitos Previos

Para levantar este microservicio de forma local (fuera de Docker), asegúrate de tener instalado:

- **JDK 17** o superior
- **Maven 3.8** o superior
- **Eureka Server** corriendo en el puerto `8761` (requerido para el descubrimiento de rutas)

> **Nota:** Este componente no requiere conexión directa a bases de datos.

---

## Variables de Entorno

El servicio corre en el puerto **`8080`** y se configura mediante el archivo `application.yml`. Las siguientes variables son requeridas para su correcto funcionamiento:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | URL del servidor Eureka para localizar los servicios | `http://localhost:8761/eureka/` |
| `JWT_SECRET` | Clave secreta para desencriptar y validar las firmas de los tokens JWT emitidos por el Auth Service | `smartlogix-super-secret-key...` |

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
docker-compose up -d api-gateway
```

---

## Pruebas Unitarias y Cobertura (JaCoCo)

El servicio cuenta con pruebas automatizadas enfocadas en la seguridad perimetral, validando que el filtro `GlobalJwtAuthFilter` rechace tokens inválidos, expirados o ausentes con un error `401 Unauthorized` antes de tocar la red interna.

Para ejecutar la suite de pruebas y generar el reporte de cobertura:

```bash
mvn clean test jacoco:report
```

**Visualización de los resultados:**

- **Consola:** Al finalizar el comando, Maven imprimirá un resumen en la terminal con la cantidad de pruebas ejecutadas, fallidas y exitosas.
- **Reporte HTML:** JaCoCo generará un reporte visual detallado en la ruta `target/site/jacoco/`. Para visualizarlo, abre el archivo `index.html` en cualquier navegador web.

---

## Rutas Configuradas (Routing)

El API Gateway enruta automáticamente las peticiones hacia los microservicios utilizando los IDs registrados en **Eureka**.

| Ruta | Método(s) | Destino | Acceso |
|---|---|---|---|
| `/auth/**` | `POST` | Auth Service | Público |
| `/users/**` | `GET`, `POST` | User Service | Protegido |
| `/products/**`, `/inventory/**` | `GET`, `POST` | Inventory Service | Protegido |
| `/orders/**` | `GET`, `POST` | Order Service | Protegido |
| `/shipping/**` | `GET`, `POST` | Shipping Service | Protegido |
| `/notifications/**` | `POST` | Notification Service *(Node.js, :8086)* | Protegido |
