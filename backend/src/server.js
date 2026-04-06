require("dotenv").config();
const express = require("express");

const app = express();
const PORT = process.env.BACKEND_PORT || 3000;

app.use(express.json());

app.get("/", (req, res) => {
  res.send("LightWeight API is running!");
});

app.listen(PORT, () => {
  console.log(`Server listening on http://localhost:${PORT}`);
});