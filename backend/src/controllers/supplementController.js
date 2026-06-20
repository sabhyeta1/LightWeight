const supplementService = require("../services/supplementService");

const createSupplement = async (req, res) => {
  try {
    const userId = req.user.id;
    const { name, dosage } = req.body;
    const supplement = await supplementService.createSupplement(userId, name, dosage);
    res.status(201).json(supplement);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

const getSupplements = async (req, res) => {
  try {
    const userId = req.user.id;
    const supplements = await supplementService.getSupplements(userId);
    res.status(200).json(supplements);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

const deleteSupplement = async (req, res) => {
  try {
    const userId = req.user.id;
    const supplementId = Number(req.params.id);
    await supplementService.deleteSupplement(userId, supplementId);
    res.status(204).send();
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

module.exports = { createSupplement, getSupplements, deleteSupplement };