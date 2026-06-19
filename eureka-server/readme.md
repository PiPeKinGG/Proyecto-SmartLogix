<div align="center">

# SmartLogix — Eureka Server

**Servidor de descubrimiento y registro dinámico para el ecosistema de microservicios de SmartLogix.**

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Eureka Server](https://img.shields.io/badge/Eureka_Server-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

</div>

---

## Descripción General

El **Eureka Server** actúa como el directorio central de la red interna. Su función principal es permitir que todos los microservicios (Auth, User, Inventory, Order, Shipping) y el API Gateway se registren automáticamente al iniciar. Esto elimina la necesidad de configurar direcciones IP estáticas o puertos fijos en el código, permitiendo que el API Gateway enrute las peticiones dinámicamente mediante balanceo de carga.

---

## Integración en la Arquitectura

Este servicio no interactúa directamente con el cliente (React) ni maneja bases de datos. Su flujo de trabajo es interno:

1. Al levantarse un microservicio (ej. `auth-service`), este se anuncia automáticamente a Eureka.
2. Cuando el API Gateway recibe una petición externa, consulta a Eureka para saber en qué IP y puerto está corriendo el servicio de destino.

---

## Tecnologías Utilizadas

| Categoría | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework Core | Spring Boot 3 |
| Service Discovery | Spring Cloud Netflix Eureka Server |

---

## Requisitos Previos

Para levantar este microservicio de forma local (fuera de Docker), asegúrate de tener instalado:

- **JDK 21** 
- **Maven 3.8** o superior

> **Nota:** Este componente no requiere ninguna base de datos ni broker de mensajería.

---

## Configuración

El servicio corre en el puerto **`8761`** y se configura mediante el archivo `application.yml`. A diferencia de los demás microservicios, Eureka actúa como un servidor autónomo y no debe intentar registrarse a sí mismo:

```yaml
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

---

## Instrucciones de Ejecución

### 1. Ejecución Local (Desarrollo)

> **Importante:** Dado que todos los demás servicios dependen de Eureka para encontrarse, este debe ser siempre el **primer servicio en levantarse**.

**Paso 1 — Compilar el proyecto:**
```bash
mvn clean install -DskipTests
```

**Paso 2 — Levantar la aplicación:**
```bash
mvn spring-boot:run
```

### 2. Ejecución vía Docker (Producción / Integración)

En el entorno de Docker Compose, este contenedor está configurado con validaciones de salud (`healthcheck`) para asegurar que esté listo antes de arrancar el API Gateway o el resto de los servicios. Desde la raíz del repositorio principal:

```bash
docker-compose up -d eureka-server
```

---

## Panel de Administración (Dashboard)

Eureka expone una interfaz gráfica donde se pueden monitorear en tiempo real todos los microservicios registrados en el ecosistema.

**URL de acceso:** `http://localhost:8761`

Al ingresar a esta dirección desde el navegador, se mostrará el panel de Eureka con los detalles del sistema y la sección **"Instances currently registered with Eureka"**, donde aparecerán listados los servicios activos como `API-GATEWAY`, `AUTH-SERVICE`, `ORDER-SERVICE`, entre otros.
