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

// FR-07: add an exercise to a plan
router.post('/:id/exercise', requireAuth, workoutPlanController.addExerciseToPlan);
// FR-08: replace all sets for an exercise entry in a plan
router.put('/:id/exercise/:ewpId/sets', requireAuth, workoutPlanController.updateExerciseSets);
// remove an exercise from a plan
router.delete('/:id/exercise/:ewpId', requireAuth, workoutPlanController.removeExerciseFromPlan);

module.exports = router;