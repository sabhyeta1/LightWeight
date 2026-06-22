const waterRepository = require("../repositories/waterRepository");

const GLASS_SIZE_ML = 250;

const setGoal = async (userId, targetMl) => {
  if (!Number.isInteger(targetMl) || targetMl <= 0) {
    throw new Error("Target must be a positive number of millilitres");
  }
  return await waterRepository.upsertGoal(userId, targetMl);
};

const getStatus = async (userId) => {
  const goal = await waterRepository.findGoal(userId);
  const logs = await waterRepository.getTodayLogs(userId);
  const totalMl = logs.reduce((sum, log) => sum + log.amount_ml, 0);
  return {
    target_ml: goal ? goal.target_ml : null,
    total_ml: totalMl,
    logs,
  };
};

const addIntake = async (userId, amountMl) => {
  const amount = Number.isInteger(amountMl) ? amountMl : GLASS_SIZE_ML;
  if (amount <= 0) {
    throw new Error("Amount must be a positive number of millilitres");
  }
  return await waterRepository.addIntake(userId, amount);
};

const deleteIntake = async (userId, logId) => {
  if (!Number.isInteger(logId)) throw new Error("Invalid log id");
  return await waterRepository.deleteIntake(userId, logId);
};

module.exports = { setGoal, getStatus, addIntake, deleteIntake, GLASS_SIZE_ML };