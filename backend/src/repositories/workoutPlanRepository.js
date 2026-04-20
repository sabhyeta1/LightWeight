const db = require("../database");
const WorkoutPlan = require("../models/WorkoutPlan");

async function createPlan(ownerId, name, description, is_published) {
  const sql = `
    INSERT INTO workout_plan (owner_id, name, description, is_published)
    VALUES ($1, $2, $3, $4)
    RETURNING id, owner_id, name, description, is_published
  `;

  const { rows } = await db.query(sql, [ownerId, name, description, is_published]);
  return new WorkoutPlan(rows[0]);
}

async function findPlansByUser(userId) {
  const sql = `
    SELECT id, owner_id, name, description, is_published
    FROM workout_plan
    WHERE owner_id = $1
    ORDER BY id DESC
  `;

  const { rows } = await db.query(sql, [userId]);
  return rows.map((row) => new WorkoutPlan(row));
}

module.exports = { createPlan, findPlansByUser };