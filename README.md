# SmartLogix - BackEnd

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Node.js](https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=node.js&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Eureka](https://img.shields.io/badge/Eureka_Discovery-Service-007396?style=for-the-badge)
![JUnit5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-C00?style=for-the-badge&logo=java&logoColor=white)

Plataforma logística y de comercio electrónico orientada a la arquitectura Multi-Tenant.

## Descripción del Proyecto

SmartLogix es una arquitectura distribuida basada en microservicios, diseñada para gestionar inventarios, procesar órdenes de compra, coordinar envíos y emitir notificaciones. El sistema implementa un enfoque Multi-Tenant, permitiendo que múltiples PYMEs operen sobre la misma infraestructura de manera aislada, identificadas mediante su `pyme_id` el cual se asigna respectivamente a la hora de crear cada usuario.

La comunicación entre dominios utiliza un modelo híbrido: peticiones síncronas vía API Gateway y OpenFeign para consultas de lectura/validación, y mensajería asíncrona a través de Apache Kafka para el patrón de coreografía de eventos (Saga).

> Todas las peticiones externas deben dirigirse exclusivamente al API Gateway (`:8080`). Ningún microservicio de dominio debe ser expuesto directamente fuera de la red privada de Docker.

## Topología del Ecosistema

El entorno está compuesto por los siguientes contenedores interconectados mediante una red privada de Docker (`smartlogix-net`).

### Infraestructura Base

| Componente | Puerto | Descripción |
|---|---|---|
| PostgreSQL | 5432 | Instancia única que inicializa bases de datos independientes por servicio mediante el script `init-databases.sql` |
| Apache Kafka | 9092 | Broker de mensajería para eventos asíncronos (`order-created`, `shipment-dispatched`, etc.) |
| Eureka Server | 8761 | Servidor de descubrimiento de servicios |

### Microservicios

| Servicio | Puerto | Stack | Responsabilidad |
|---|---|---|---|
| API Gateway | 8080 | Java 21, Spring Cloud Gateway | Único punto de entrada público. Enruta tráfico y valida firmas JWT |
| Auth Service | 8081 | Java 21, Spring Security, JWT | Emisión de tokens y control de acceso |
| User Service | 8082 | Java 21, Spring Data JPA | Gestión de perfiles y credenciales de usuarios |
| Inventory Service | 8083 | Java 21, JPA/Hibernate | Control transaccional de stock de productos (Multi-Tenant) |
| Order Service | 8084 | Java 21, OpenFeign, Kafka | Orquestador central de compras (Saga/Coreografía) |
| Shipping Service | 8085 | Java 21, Strategy/Factory | Logística y cálculo dinámico de despachos |
| Notification Service | 8086 | Node.js, Express.js | Consumidor de eventos para alertas vía email/SMS |

> Eureka Server no requiere variables de entorno obligatorias adicionales para su arranque; los demás servicios dependen de su disponibilidad en el puerto `8761` para registrarse correctamente.

### Diagrama de Flujo General

```text
Cliente -> API Gateway (:8080) -> [Auth | User | Inventory | Order | Shipping] Service
                                          |
                                          +-> (Sincrono: OpenFeign) -> Validaciones entre servicios
                                          |
                                          +-> (Asincrono: Kafka) -> Order Created / Inventory Reserved
                                          |                          / Shipment Dispatched / etc.
                                          |
                                          +-> Notification Service (consumidor Kafka, Node.js)
```

## Guía de Despliegue Rápido (Docker Compose)

El proyecto está diseñado para levantarse con un solo comando gracias a la configuración de Docker Compose, la cual maneja los healthchecks para encender la infraestructura en el orden correcto.

Requisitos previos:

- Docker Desktop o Docker Engine instalado y en ejecución

Paso 1 — Ubícate en la raíz del proyecto.

Paso 2 — Ejecuta el entorno completo en segundo plano:

```bash
docker-compose up -d --build
```

> La primera ejecución tomará varios minutos mientras Docker descarga las imágenes base de Alpine, Java, Node, Postgres y Kafka, y compila los contenedores locales.

Paso 3 — Monitorea el despliegue. Verifica que todos los servicios se hayan registrado correctamente accediendo al panel de Eureka:

```text
http://localhost:8761
```

Para detener y limpiar completamente el ecosistema:

```bash
docker-compose down -v
```

> El flag `-v` asegura la limpieza de los volúmenes de base de datos para pruebas limpias.

## Automatización de Pruebas y Cobertura

Cada microservicio ha sido desarrollado con un estándar de calidad que supera, en promedio, el 60% de cobertura de código. Los servicios Java utilizan JaCoCo y el servicio Node.js utiliza Jest.

Para ejecutar la suite de pruebas de un microservicio individual:

```bash
# Servicios Java
mvn clean test jacoco:report

# Notification Service (Node.js)
npm test
```

Para agilizar la revisión de los reportes HTML generados por JaCoCo y Jest, puedes utilizar el siguiente script de PowerShell desde la raíz del proyecto. Buscará y abrirá automáticamente todos los reportes de cobertura disponibles en tu navegador por defecto:

```powershell
# Abrir reportes de Java (JaCoCo)
@('auth-service','user-service','inventory-service','order-service','shipping-service','api-gateway','eureka-server') | ForEach-Object {
    $path = "$_\target\site\jacoco\index.html"
    if (Test-Path $path) {
        Invoke-Item $path
    } else {
        Write-Host "No encontrado (Java): $path"
    }
}

# Abrir reporte de Node (Jest)
$nodePath = "notification-service\coverage\index.html"
if (Test-Path $nodePath) {
    Invoke-Item $nodePath
} else {
    Write-Host "No encontrado (Node): $nodePath"
}
```

> `eureka-server` no incluye sección de pruebas con cobertura JaCoCo; el script las omite silenciosamente si no encuentra el reporte.

## Uso de la API (Ejemplo Multi-Tenant)

Al realizar pruebas externas (por ejemplo, desde Postman), todas las peticiones deben apuntar al puerto 8080 (API Gateway) e incluir el header `pyme_id` para resolver la tenencia de los datos correctamente.

Ejemplo de petición al catálogo de productos:

```http
GET http://localhost:8080/products
Authorization: Bearer <TU_TOKEN_JWT>
pyme_id: 50
```

## Documentación por Microservicio

| Microservicio | Documento |
|---|---|
| API Gateway | `README-api-gateway.md` |
| Auth Service | `README-auth-service.md` |
| User Service | `README-user-service.md` |
| Inventory Service | `README-inventory-service.md` |
| Order Service | `README-order-service.md` |
| Shipping Service | `README-shipping-service.md` |
| Notification Service | `README-notification-service.md` |
| Eureka Server | `README-eureka-server.md` |
