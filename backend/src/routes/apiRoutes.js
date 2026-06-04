const express = require("express");
const router = express.Router();
const workoutPlanRoutes = require('./workoutPlanRoutes');
const authRoutes = require('./authRoutes');
const muscleGroupRoutes = require('./muscleGroupRoutes');
const exerciseRoutes = require('./exerciseRoutes');

router.use("/workout-plan", workoutPlanRoutes);
router.use("/auth", authRoutes);
router.use("/muscle-group", muscleGroupRoutes);
router.use("/exercise", exerciseRoutes);

module.exports = router;