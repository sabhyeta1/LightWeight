const workoutPlanRepository = require('../repositories/workoutPlanRepository');

const createWorkoutPlan = async (ownerId, name, description, is_published) => {
    // Validierung
    if (!name) {
        throw new Error('Name is required');
    }
    return await workoutPlanRepository.createPlan(ownerId, name, description, is_published);
};

// pläne eines bestimmten users
const getWorkoutPlansByUser = async (userId) => {
    return await workoutPlanRepository.findPlansByUser(userId);
};

module.exports = { createWorkoutPlan, getWorkoutPlansByUser};