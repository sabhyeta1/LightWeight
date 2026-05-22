const workoutPlanRepository = require('../repositories/workoutPlanRepository');

const createWorkoutPlan = async (ownerId, name, description, isPublished) => {
    // Validierung
    if (!name) {
        throw new Error('Name is required');
    }
    return await workoutPlanRepository.createPlan(ownerId, name, description, isPublished);
};

// pläne eines bestimmten users
const getWorkoutPlansByUser = async (userId) => {
    return await workoutPlanRepository.findPlansByUser(userId);
};

const getWorkoutPlanById = async (planId, userId) => {
  if (!Number.isInteger(planId)) {
    throw new Error("Invalid plan id");
  }

  return await workoutPlanRepository.findPlanByIdAndUser(planId, userId);
};

const updateWorkoutPlan = async (planId, userId, name, description, isPublished) => {
  if (!Number.isInteger(planId)) {
    throw new Error("Invalid plan id");
  }

  if (name !== undefined && !name) {
    throw new Error("Name cannot be empty");
  }

  return await workoutPlanRepository.updatePlan(planId, userId, name, description, isPublished);
};

const deleteWorkoutPlan = async (planId, userId) => {
  if (!Number.isInteger(planId)){ 
    throw new Error("Invalid plan id");
  }

  return await workoutPlanRepository.deletePlan(planId, userId);
};

module.exports = { createWorkoutPlan, getWorkoutPlansByUser, getWorkoutPlanById, updateWorkoutPlan, deleteWorkoutPlan};