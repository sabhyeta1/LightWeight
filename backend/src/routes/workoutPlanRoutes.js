const express = require('express');
const router = express.Router();
const workoutPlanController = require('../controllers/workoutPlanController');
const { requireAuth } = require('../middlewares/authMiddleware')

// beim erstellen aufgerufen
router.post('/', requireAuth, workoutPlanController.createWorkoutPlan);
// gibt alle pläne eines Users zurück
router.get('/', requireAuth, workoutPlanController.getWorkoutPlans);

router.get("/:id", requireAuth, workoutPlanController.getWorkoutPlanById);
router.put("/:id", requireAuth, workoutPlanController.updateWorkoutPlan);
router.delete("/:id", requireAuth, workoutPlanController.deleteWorkoutPlan);

module.exports = router;