const express = require("express");
const router = express.Router();
const workoutPlanRoutes = require('./workoutPlanRoutes');
const authRoutes = require('./authRoutes');

router.use("/workout-plan", workoutPlanRoutes);
router.use("/auth", authRoutes);

module.exports = router