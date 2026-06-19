const express = require("express");
const router = express.Router();
const workoutPlanRoutes = require('./workoutPlanRoutes');
const authRoutes = require('./authRoutes');
const muscleGroupRoutes = require('./muscleGroupRoutes');
const exerciseRoutes = require('./exerciseRoutes');
const communityRoutes = require('./communityRoutes');
const calendarRoutes = require('./calendarRoutes');
const userRoutes = require('./userRoutes'); // FR-17

router.use("/workout-plan", workoutPlanRoutes);
router.use("/auth", authRoutes);
router.use("/muscle-group", muscleGroupRoutes);
router.use("/exercise", exerciseRoutes);
router.use("/community", communityRoutes);
router.use("/calendar", calendarRoutes);
router.use("/user", userRoutes); // FR-17

module.exports = router;