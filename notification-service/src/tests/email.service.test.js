// 1. Hacemos el mock genérico de la librería completa primero
jest.mock('nodemailer');

// 2. Importamos la librería (ahora interceptada por Jest)
const nodemailer = require('nodemailer');

// 3. Preparamos nuestra función simulada y el comportamiento del transporte
// ESTO DEBE OCURRIR ANTES DE IMPORTAR EL SERVICIO
const sendMailMock = jest.fn();
nodemailer.createTransport.mockReturnValue({
    sendMail: sendMailMock
});

// 4. Ahora sí, importamos nuestro servicio. Al cargarse, usará el mock preparado arriba.
const emailService = require('../services/email.service');

describe('Email Service', () => {
    
    beforeEach(() => {
        // Limpiamos los contadores antes de cada prueba
        sendMailMock.mockClear();
    });

    it('debería enviar un correo exitosamente y retornar la información', async () => {
        // Simulamos respuesta de éxito
        sendMailMock.mockResolvedValue({ messageId: 'simulated-id-999' });

        const result = await emailService.sendEmail('test@empresa.com', 'Alerta', 'Mensaje de prueba');

        expect(result.messageId).toBe('simulated-id-999');
        expect(sendMailMock).toHaveBeenCalledTimes(1);
    });

    it('debería capturar el error de nodemailer y lanzar una excepción personalizada', async () => {
        // Simulamos una falla en el servidor SMTP
        sendMailMock.mockRejectedValue(new Error('Conexión rechazada por el servidor SMTP'));

        await expect(emailService.sendEmail('test@empresa.com', 'Alerta', 'Mensaje de prueba'))
            .rejects
            .toThrow('Fallo al intentar enviar el correo electrónico externo');
        
        expect(sendMailMock).toHaveBeenCalledTimes(1);
    });
});