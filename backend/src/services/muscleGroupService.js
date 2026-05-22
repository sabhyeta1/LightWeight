const muscleGroupRepository = require("../repositories/muscleGroupRepository");

async function getMuscleGroups() {
  return await muscleGroupRepository.findAllMuscleGroups();
}

module.exports = { getMuscleGroups };