const workoutPlanService = require('../services/workoutPlanService');


const createWorkoutPlan = async (req, res) => {
    try {
        const {name, description} = req.body;
        const owner = 1; //dummy user - später req.user.id
        
        const plan = await workoutPlanService.createWorkoutPlan(owner, name, description);
        res.status(201).json(plan);
    } catch(err) {
        res.status(400).json({error: err.message});
    }
};

const getWorkoutPlans = async (req, res) => {
    try {
        const userId = 1 // dummy req.user.id
        // ruft alle trainingspläne über service ab
        const plans = await workoutPlanService.getWorkoutPlansByUser(userId);
        res.status(200).json(plans);
    } catch (err) {
        res.status(500).json({error: err.message});
    }
};

module.exports = {createWorkoutPlan, getWorkoutPlans}