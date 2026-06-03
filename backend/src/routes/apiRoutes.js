const express = require("express");
const router = express.Router();
const workoutPlanRoutes = require('./workoutPlanRoutes');
const authRoutes = require('./authRoutes');
const exerciseRoutes = require('./exerciseRoutes');

router.use("/workout-plan", workoutPlanRoutes);
router.use("/auth", authRoutes);
router.use("/exercise", exerciseRoutes);

module.exports = router;