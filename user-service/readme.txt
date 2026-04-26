Endpoints expuestos por user-service
Verificación interna de usuario (usado por auth-service):

POST /internal/users/verify
Body: { "email": "...", "password": "...", "pymeId": ... }
Respuesta: { "valid": true/false, "userId": ..., "pymeId": ..., "role": "..." }
Uso: Solo accesible internamente. Permite a auth-service validar credenciales y obtener datos clave para el JWT.
CRUD de usuarios (accesible para administradores de la PYME):

GET users
Header: pyme_id
Descripción: Lista todos los usuarios de la pyme.
POST users
Header: pyme_id
Body: { "email": "...", "nombre": "...", "role": "..." }
Descripción: Crea un usuario para la pyme.
Conexión entre auth-service y user-service
auth-service usa un Feign Client (UserClient) apuntando a /internal/users/verify de user-service.
Cuando se recibe un login, auth-service envía el email, password y pymeId a user-service.
user-service valida el hash de la contraseña y el pymeId, y responde con los datos necesarios.
auth-service genera el JWT con los claims: userId, pymeId, role.
Recomendaciones para integración perfecta
Ambos servicios deben estar registrados en Eureka y tener la misma configuración de discovery.
El Feign Client de auth-service debe usar el nombre lógico del servicio (user-service) para la URL.
El endpoint /internal/users/verify debe estar protegido para uso interno (permitAll en SecurityConfig).
El campo pymeId debe ser obligatorio en todas las operaciones de usuario para mantener el aislamiento multi-tenant.
El password debe ser correctamente hasheado con BCrypt en user-service y comparado igual en la verificación.