const workoutPlanService = require('../services/workoutPlanService');


const createWorkoutPlan = async (req, res) => {
    try {
        const {name, description, is_published} = req.body;
        const ownerId = req.user.id;
        
        const plan = await workoutPlanService.createWorkoutPlan(ownerId, name, description, is_published);
        res.status(201).json(plan);
    } catch(err) {
        res.status(400).json({error: err.message});
    }
};

const getWorkoutPlans = async (req, res) => {
    try {
        const userId = req.user.id;
        // ruft alle trainingspläne über service ab
        const plans = await workoutPlanService.getWorkoutPlansByUser(userId);
        res.status(200).json(plans);
    } catch (err) {
        res.status(500).json({error: err.message});
    }
};

const getWorkoutPlanById = async (req, res) => {
  try {
    const userId = req.user.id;
    const planId = Number(req.params.id);

    const plan = await workoutPlanService.getWorkoutPlanById(planId, userId);

    if (!plan) {
      return res.status(404).json({ error: "Workout plan not found" });
    }

    res.status(200).json(plan);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

const updateWorkoutPlan = async (req, res) => {
  try {
    const userId = req.user.id;
    const planId = Number(req.params.id);
    const { name, description, is_published } = req.body;

    const plan = await workoutPlanService.updateWorkoutPlan(planId, userId, name, description, is_published);

    if (!plan) {
      return res.status(404).json({ error: "Workout plan not found" });
    }

    res.status(200).json(plan);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

const deleteWorkoutPlan = async (req, res) => {
  try {
    const userId = req.user.id;
    const planId = Number(req.params.id);

    const deleted = await workoutPlanService.deleteWorkoutPlan(planId, userId);

    if (!deleted) {
      return res.status(404).json({ error: "Workout plan not found" });
    }

    res.status(204).send();
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

// FR-07: add exercise to plan
const addExerciseToPlan = async (req, res) => {
  try {
    const userId = req.user.id;
    const planId = Number(req.params.id);
    const { exercise_id, order } = req.body;
    const result = await workoutPlanService.addExerciseToPlan(planId, userId, exercise_id, order);
    res.status(201).json(result);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

// FR-08: update sets for an exercise in a plan
const updateExerciseSets = async (req, res) => {
  try {
    const userId = req.user.id;
    const planId = Number(req.params.id);
    const ewpId = Number(req.params.ewpId);
    const { sets } = req.body; // array of { set_number, reps, weight, machine_settings }
    const result = await workoutPlanService.updateExerciseSets(planId, userId, ewpId, sets);
    res.status(200).json(result);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

// remove exercise from plan
const removeExerciseFromPlan = async (req, res) => {
  try {
    const userId = req.user.id;
    const planId = Number(req.params.id);
    const ewpId = Number(req.params.ewpId);
    await workoutPlanService.removeExerciseFromPlan(planId, userId, ewpId);
    res.status(204).send();
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

const getExerciseSets = async (req, res) => {
  try {
    const userId = req.user.id;
    const planId = Number(req.params.id);
    const ewpId = Number(req.params.ewpId);

    const sets = await workoutPlanService.getExerciseSets(planId, ewpId, userId);

    res.status(200).json(sets);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

const publishWorkoutPlan = async (req, res) => {
  try {
    const userId = req.user.id;
    const planId = Number(req.params.id);

    const plan = await workoutPlanService.publishWorkoutPlan(planId, userId);

    if (!plan) {
      return res.status(404).json({ error: "Workout plan not found" });
    }

    res.status(200).json(plan);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

const unpublishWorkoutPlan = async (req, res) => {
  try {
    const userId = req.user.id;
    const planId = Number(req.params.id);

    const plan = await workoutPlanService.unpublishWorkoutPlan(planId, userId);

    if (!plan) {
      return res.status(404).json({ error: "Workout plan not found" });
    }

    res.status(200).json(plan);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

module.exports = {createWorkoutPlan, getWorkoutPlans, getWorkoutPlanById, updateWorkoutPlan, deleteWorkoutPlan, addExerciseToPlan, updateExerciseSets, removeExerciseFromPlan, getExerciseSets, publishWorkoutPlan, unpublishWorkoutPlan}