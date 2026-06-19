<div align="center">

# SmartLogix — Notification Service

**Microservicio de mensajería y alertas asíncronas para el ecosistema SmartLogix.**

![Node.js](https://img.shields.io/badge/Node.js_16+-339933?style=for-the-badge&logo=nodedotjs&logoColor=white)
![Express](https://img.shields.io/badge/Express.js-000000?style=for-the-badge&logo=express&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![Jest](https://img.shields.io/badge/Jest-C21325?style=for-the-badge&logo=jest&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

</div>

---

## Descripción General

El **Notification Service** es el componente encargado de gestionar y despachar todas las comunicaciones salientes hacia el cliente final (como correos electrónicos de confirmación de compra o actualizaciones de estado de envío). Está desarrollado íntegramente en **Node.js**.
Este servicio funciona principalmente escuchando eventos en segundo plano a través de **Apache Kafka**, lo que evita ralentizar los procesos de compra principales.

---

## Integración en la Arquitectura

Este servicio opera en dos modalidades de integración:

**Basada en Eventos (Asíncrona):**
```
Order Service / Shipping Service  →  Apache Kafka (Broker)  →  Notification Service  →  Envío de Email
```

**Peticiones Directas (Síncrona):**
```
React (Frontend)  →  API Gateway (:8080)  →  Notification Service (:8086)
```

---

## Tecnologías Utilizadas

| Categoría | Tecnología |
|---|---|
| Lenguaje | JavaScript (Node.js) |
| Framework Web | Express.js |
| Mensajería Asíncrona | Apache Kafka *(Cliente para Node)* |
| Testing y Cobertura | Jest |

---

## Requisitos Previos

Para levantar este microservicio de forma local (fuera de Docker), asegúrate de tener instalado:

- **Node.js** versión 16 o superior
- **NPM** (incluido con Node.js)
- **Apache Kafka** corriendo en el puerto `9092`

> **Nota:** Este componente no utiliza una base de datos propia, ya que su función es netamente reactiva y de tránsito.

---

## Variables de Entorno

El servicio corre en el puerto **`8086`** y se configura mediante un archivo `.env` o a través del entorno. Las siguientes variables son requeridas:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `PORT` | Puerto de ejecución del servidor Express | `8086` |
| `NODE_ENV` | Entorno de ejecución | `development` o `production` |
| `KAFKA_BROKERS` | Lista de brokers de Kafka para conectarse | `localhost:9092` |

---

## Instrucciones de Ejecución

### 1. Ejecución Local (Desarrollo)

**Paso 1 — Instalar las dependencias del proyecto:**
```bash
npm install
```

**Paso 2 — Levantar la aplicación:**
```bash
npm start
```

### 2. Ejecución vía Docker (Producción / Integración)

Este microservicio está configurado para construirse y desplegarse junto a la arquitectura completa a través de **Docker Compose**. Desde la raíz del repositorio principal:

```bash
docker-compose up -d notification-service
```

---

## Pruebas Unitarias y Cobertura (Jest)

A diferencia de los microservicios en Java, este servicio utiliza **Jest** para ejecutar las pruebas y medir la cobertura. Se valida la resiliencia de:

- **`notification.controller.js`** — Recepción de peticiones HTTP.
- **`email.service.js`** — Lógica de formateo y simulación de envío de correos.

Para ejecutar la suite de pruebas y generar el reporte de cobertura:

```bash
npm run test -- --coverage
```

**Visualización de los resultados:**

- **Consola:** Al finalizar, Jest imprimirá una tabla con los porcentajes de cobertura por línea, función y rama, evidenciando el cumplimiento superior al 60%.
- **Reporte HTML:** Jest generará una carpeta `coverage/`. Abre el archivo `coverage/lcov-report/index.html` en tu navegador para ver un reporte gráfico interactivo detallado.

---

## Endpoints Principales (Síncronos)

> El API Gateway intercepta la ruta `/notifications/` y realiza un `RewritePath` dinámico hacia este servicio en el puerto `8086`.

### `POST /notifications/send` — Envío Manual de Alerta

**URL vía Gateway:** `http://localhost:8080/notifications/send`

**Request Body:**
```json
{
  "to": "cliente@email.com",
  "subject": "Actualización de su pedido",
  "message": "Su pedido ha sido despachado exitosamente."
}
```

**Respuesta Exitosa — `200 OK`:**
```json
{
  "status": "success",
  "message": "Notificación procesada correctamente"
}
```
