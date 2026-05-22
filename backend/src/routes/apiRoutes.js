const express = require("express");
const router = express.Router();
const workoutPlanRoutes = require('./workoutPlanRoutes');
const authRoutes = require('./authRoutes');
const muscleGroupRoutes = require('./muscleGroupRoutes');

router.use("/workout-plan", workoutPlanRoutes);
router.use("/auth", authRoutes);
router.use("/muscle-group", muscleGroupRoutes);

module.exports = router