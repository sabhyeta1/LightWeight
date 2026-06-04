const db = require("../database");
const MuscleGroup = require("../models/MuscleGroup");

async function findAllMuscleGroups() {
  const sql = `
    SELECT id, name
    FROM muscle_groups
    ORDER BY name ASC
  `;

  const { rows } = await db.query(sql);
  return rows.map((row) => new MuscleGroup(row));
}

module.exports = { findAllMuscleGroups };