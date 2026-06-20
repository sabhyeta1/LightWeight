const waterService = require("../services/waterService");

const setGoal = async (req, res) => {
  try {
    const userId = req.user.id;
    const { target_ml } = req.body || {};
    const goal = await waterService.setGoal(userId, target_ml);
    res.status(200).json(goal);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

const getStatus = async (req, res) => {
  try {
    const userId = req.user.id;
    const status = await waterService.getStatus(userId);
    res.status(200).json(status);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

const addIntake = async (req, res) => {
  try {
    const userId = req.user.id;
    const { amount_ml } = req.body || {};
    const log = await waterService.addIntake(userId, amount_ml);
    res.status(201).json(log);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

const deleteIntake = async (req, res) => {
  try {
    const userId = req.user.id;
    const logId = Number(req.params.id);
    await waterService.deleteIntake(userId, logId);
    res.status(204).send();
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

module.exports = { setGoal, getStatus, addIntake, deleteIntake };