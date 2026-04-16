require("dotenv").config();
const express = require("express");
const workoutPlanRoutes = require('./routes/workoutPlanRoutes');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(express.json());
app.use('/api/workout-plans', workoutPlanRoutes);

app.get("/", (req, res) => {
  res.send("LightWeight API is running!");
});

app.listen(PORT, () => {
  console.log(`Server listening on http://localhost:${PORT}`);
});

