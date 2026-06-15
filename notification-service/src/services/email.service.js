const nodemailer = require('nodemailer');

// Configuramos el "transporte" leyendo las variables de entorno de tu .env
const transporter = nodemailer.createTransport({
    host: process.env.SMTP_HOST,
    port: process.env.SMTP_PORT,
    auth: {
        user: process.env.SMTP_USER,
        pass: process.env.SMTP_PASS
    }
});

/**
 * Función asíncrona para enviar correos.
 * Demuestra el poder de Node.js para tareas de entrada/salida sin bloquear el hilo principal.
 */
const sendEmail = async (to, subject, text) => {
    try {
        const info = await transporter.sendMail({
            from: '"SmartLogix Notifications" <noreply@smartlogix.com>',
            to: to,
            subject: subject,
            text: text
        });
        
        console.log(`[Email Service] Correo enviado con ID: ${info.messageId}`);
        return info;
    } catch (error) {
        console.error('[Email Service] Error enviando correo:', error);
        throw new Error('Fallo al intentar enviar el correo electrónico externo');
    }
};

module.exports = {
    sendEmail
};