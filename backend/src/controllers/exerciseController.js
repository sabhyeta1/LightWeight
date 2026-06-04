const exerciseService = require("../services/exerciseService");

async function getExercises(req, res) {
  try {
    const muscleGroupId = req.query.muscleGroupId ? Number(req.query.muscleGroupId) : null;
    const exercises = await exerciseService.getExercises(muscleGroupId);
    res.status(200).json(exercises);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
}

module.exports = { getExercises };