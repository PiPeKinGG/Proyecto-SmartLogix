const request = require('supertest');
const app = require('../app'); 
const emailService = require('../services/email.service');

jest.mock('../services/email.service');

describe('Notification API Endpoints', () => {

    beforeEach(() => {
        jest.clearAllMocks();
    });

    describe('GET /health', () => {
        it('debería retornar 200 y el estado UP', async () => {
            const res = await request(app).get('/health');
            expect(res.statusCode).toBe(200);
            expect(res.body.status).toBe('UP');
            expect(res.body.service).toBe('notification-service-node');
        });
    });

    describe('POST /api/notifications/send', () => {
        it('debería retornar 200 y el messageId cuando los datos son válidos', async () => {
            emailService.sendEmail.mockResolvedValue({ messageId: 'mock-id-9876' });

            const res = await request(app)
                .post('/api/notifications/send')
                .send({
                    recipient: 'cliente@test.com',
                    subject: 'Tu pedido #123 ha sido enviado',
                    message: 'El pedido está en camino a tu dirección.'
                });

            expect(res.statusCode).toBe(200);
            expect(res.body.success).toBe(true);
            expect(res.body.data.recipient).toBe('cliente@test.com');
            expect(res.body.data.messageId).toBe('mock-id-9876'); 
            expect(emailService.sendEmail).toHaveBeenCalledTimes(1);
            expect(emailService.sendEmail).toHaveBeenCalledWith(
                'cliente@test.com', 
                'Tu pedido #123 ha sido enviado', 
                'El pedido está en camino a tu dirección.'
            );
        });

        it('debería retornar 400 y ser capturado por el GlobalExceptionHandler si faltan datos', async () => {
            const res = await request(app)
                .post('/api/notifications/send')
                .send({
                    recipient: 'cliente@test.com'
                });

            expect(res.statusCode).toBe(400);
            expect(res.body.error).toBe('Faltan campos obligatorios: recipient, subject, message');
            
            expect(emailService.sendEmail).not.toHaveBeenCalled();
        });
    });
});