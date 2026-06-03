const express = require("express");
const router = express.Router();
const exerciseController = require("../controllers/exerciseController");
const { requireAuth } = require("../middlewares/authMiddleware");

// FR-16: browse full exercise library (optional ?muscleGroupId= filter)
router.get("/", requireAuth, exerciseController.getExercises);

module.exports = router;