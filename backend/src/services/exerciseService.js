const exerciseRepository = require("../repositories/exerciseRepository");

async function getExercises(muscleGroupId = null) {
  return await exerciseRepository.findAll(muscleGroupId);
}

module.exports = { getExercises };