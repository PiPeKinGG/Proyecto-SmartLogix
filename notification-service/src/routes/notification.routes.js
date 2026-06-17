const express = require('express');
const router = express.Router();
const notificationController = require('../controllers/notification.controller');

// Endpoint: POST /api/notifications/send
router.post('/send', notificationController.sendNotification);

module.exports = router;