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

module.exports = {createWorkoutPlan, getWorkoutPlans, getWorkoutPlanById, updateWorkoutPlan, deleteWorkoutPlan}