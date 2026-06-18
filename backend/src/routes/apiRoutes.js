const express = require("express");
const router = express.Router();
const workoutPlanRoutes = require('./workoutPlanRoutes');
const authRoutes = require('./authRoutes');
const muscleGroupRoutes = require('./muscleGroupRoutes');
const exerciseRoutes = require('./exerciseRoutes');
const communityRoutes = require('./communityRoutes')
const calendarRoutes = require('./calendarRoutes')

router.use("/workout-plan", workoutPlanRoutes);
router.use("/auth", authRoutes);
router.use("/muscle-group", muscleGroupRoutes);
router.use("/exercise", exerciseRoutes);
router.use("/community", communityRoutes);
router.use("/calendar", calendarRoutes)

module.exports = router;