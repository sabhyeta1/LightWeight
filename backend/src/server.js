require("dotenv").config();
const express = require("express");
const apiRoutes = require('./routes/apiRoutes');
const path = require("path");

const app = express();
const PORT = process.env.BACKEND_PORT || 3000;

app.use(
  "/static",
  express.static(path.join(__dirname, "../public"))
);

app.use(express.json());

//debug
app.use((req, res, next) => {
    res.on('finish', () => {
        console.log(`${req.method} ${req.url} → ${res.statusCode}`);
    });
    next();
});

app.use('/api', apiRoutes);

app.get("/", (req, res) => {
  res.send("LightWeight API is running!");
});

app.listen(PORT, () => {
  console.log(`Server listening on http://localhost:${PORT}`);
});

