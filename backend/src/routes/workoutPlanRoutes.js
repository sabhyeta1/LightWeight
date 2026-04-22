const express = require('express');
const router = express.Router();
const workoutPlanController = require('../controllers/workoutPlanController');
const { requireAuth } = require('../middlewares/authMiddleware')

// beim erstellen aufgerufen
router.post('/', requireAuth, workoutPlanController.createWorkoutPlan);
// gibt alle pläne eines Users zurück
router.get('/', requireAuth, workoutPlanController.getWorkoutPlans);

module.exports = router;