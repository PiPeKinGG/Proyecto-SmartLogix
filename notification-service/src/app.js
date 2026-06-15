const express = require('express');
const cors = require('cors');
const dotenv = require('dotenv');

// Cargar variables de entorno
dotenv.config();

const app = express();

// Middlewares base
app.use(cors());
app.use(express.json());

// Health check endpoint (útil para Eureka o el API Gateway después)
app.get('/health', (req, res) => {
    res.status(200).json({ status: 'UP', service: 'notification-service-node' });
});

// Aquí inyectaremos nuestras rutas en el próximo paso
app.use('/api/notifications', require('./routes/notification.routes'));

// Middleware de manejo de errores global (Equivalente a tu GlobalExceptionHandler en Spring)
app.use((err, req, res, next) => {
    console.error(`[Error] ${err.message}`);
    const status = err.status || 500;
    res.status(status).json({
        timestamp: new Date().toISOString(),
        status: status,
        error: err.message || 'Error interno del servidor',
        path: req.originalUrl
    });
});

// Exportamos la app para que Jest/Supertest pueda probarla sin levantar el puerto
module.exports = app;

// Solo iniciamos el servidor si este archivo se ejecuta directamente (no en tests)
if (require.main === module) {
    const PORT = process.env.PORT || 8086;
    app.listen(PORT, () => {
        console.log(`Notification Service (Node.js) corriendo en el puerto ${PORT}`);
    });
}