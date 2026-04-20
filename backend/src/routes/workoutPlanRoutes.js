const express = require('express');
const router = express.Router();
const workoutPlanController = require('../controllers/workoutPlanController');

// beim erstellen aufgerufen
router.post('/', workoutPlanController.createWorkoutPlan);
// gibt alle pläne eines Users zurück
router.get('/', workoutPlanController.getWorkoutPlans);

module.exports = router;