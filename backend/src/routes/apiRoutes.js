const express = require("express");
const router = express.Router();
const workoutPlanRoutes = require('./workoutPlanRoutes');

router.use("/workout-plans", workoutPlanRoutes);

module.exports = router