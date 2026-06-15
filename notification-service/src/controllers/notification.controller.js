const emailService = require('../services/email.service');

const sendNotification = async (req, res, next) => {
    try {
        const { recipient, subject, message } = req.body;

        // Validación de negocio
        if (!recipient || !subject || !message) {
            const error = new Error('Faltan campos obligatorios: recipient, subject, message');
            error.status = 400; // Bad Request
            throw error;
        }

        // Delegamos la lógica asíncrona al Service
        const info = await emailService.sendEmail(recipient, subject, message);
        
        res.status(200).json({
            success: true,
            message: 'Notificación procesada y enviada correctamente',
            data: {
                messageId: info.messageId,
                recipient,
                subject,
                timestamp: new Date().toISOString()
            }
        });
    } catch (error) {
        next(error); // Deriva el error al Middleware Global de app.js
    }
};

module.exports = {
    sendNotification
};