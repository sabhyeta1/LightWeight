const supplementRepository = require("../repositories/supplementRepository");

const createSupplement = async (userId, name, dosage) => {
  if (!name || !name.trim()) throw new Error("Name is required");
  if (!dosage || !dosage.trim()) throw new Error("Dosage is required");
  return await supplementRepository.createSupplement(userId, name.trim(), dosage.trim());
};

const getSupplements = async (userId) => {
  return await supplementRepository.findSupplementsByUser(userId);
};

const deleteSupplement = async (userId, supplementId) => {
  if (!Number.isInteger(supplementId)) throw new Error("Invalid supplement id");
  return await supplementRepository.deleteSupplement(userId, supplementId);
};

module.exports = { createSupplement, getSupplements, deleteSupplement };