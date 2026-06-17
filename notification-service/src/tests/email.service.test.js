
jest.mock('nodemailer');
const nodemailer = require('nodemailer');

const sendMailMock = jest.fn();
nodemailer.createTransport.mockReturnValue({
    sendMail: sendMailMock
});


const emailService = require('../services/email.service');

describe('Email Service', () => {
    
    beforeEach(() => {
        sendMailMock.mockClear();
    });

    it('debería enviar un correo exitosamente y retornar la información', async () => {
        sendMailMock.mockResolvedValue({ messageId: 'simulated-id-999' });

        const result = await emailService.sendEmail('test@empresa.com', 'Alerta', 'Mensaje de prueba');

        expect(result.messageId).toBe('simulated-id-999');
        expect(sendMailMock).toHaveBeenCalledTimes(1);
    });

    it('debería capturar el error de nodemailer y lanzar una excepción personalizada', async () => {
        sendMailMock.mockRejectedValue(new Error('Conexión rechazada por el servidor SMTP'));

        await expect(emailService.sendEmail('test@empresa.com', 'Alerta', 'Mensaje de prueba'))
            .rejects
            .toThrow('Fallo al intentar enviar el correo electrónico externo');
        
        expect(sendMailMock).toHaveBeenCalledTimes(1);
    });
});