const muscleGroupService = require("../services/muscleGroupService");

async function getMuscleGroups(req, res) {
  try {
    const muscleGroups = await muscleGroupService.getMuscleGroups();
    res.status(200).json(muscleGroups);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
}

module.exports = { getMuscleGroups };