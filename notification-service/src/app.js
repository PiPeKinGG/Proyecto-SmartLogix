const express = require("express");
const cors = require("cors");
const dotenv = require("dotenv");

dotenv.config();

const app = express();
app.use(cors());
app.use(express.json());
app.get("/health", (req, res) => {
  res.status(200).json({ status: "UP", service: "notification-service-node" });
});
app.use("/api/notifications", require("./routes/notification.routes"));

app.use((err, req, res, next) => {
  console.error(`[Error] ${err.message}`);
  const status = err.status || 500;
  res.status(status).json({
    timestamp: new Date().toISOString(),
    status: status,
    error: err.message || "Error interno del servidor",
    path: req.originalUrl,
  });
});

module.exports = app;

if (require.main === module) {
  const PORT = process.env.PORT || 8086;
  app.listen(PORT, () => {
    console.log(
      `Notification Service (Node.js) corriendo en el puerto ${PORT}`,
    );
  });
}
