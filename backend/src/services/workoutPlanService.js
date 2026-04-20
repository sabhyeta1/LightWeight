const workoutPlanRepository = require('../repositories/workoutPlanRepository');

const createWorkoutPlan = async (owner, name, description) => {
    // Validierung
    if (!name) {
        throw new Error('Name is required');
    }
    return await workoutPlanRepository.createPlan(owner, name, description);
};

// pläne eines bestimmten users
const getWorkoutPlansByUser = async (userId) => {
    return await workoutPlanRepository.findPlansByUser(userId);
};

module.exports = { createWorkoutPlan, getWorkoutPlansByUser};