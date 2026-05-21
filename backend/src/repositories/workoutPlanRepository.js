const db = require("../database");
const WorkoutPlan = require("../models/WorkoutPlan");

async function createPlan(ownerId, name, description, isPublished) {
  const sql = `
    INSERT INTO workout_plan (owner_id, name, description, is_published)
    VALUES ($1, $2, $3, $4)
    RETURNING id, owner_id, name, description, is_published
  `;

  const { rows } = await db.query(sql, [ownerId, name, description, isPublished]);
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

async function findPlanByIdAndUser(planId, userId) {
  const sql = `
    SELECT id, owner_id, name, description, is_published
    FROM workout_plan
    WHERE id = $1 AND owner_id = $2
  `;

  const { rows } = await db.query(sql, [planId, userId]);
  return rows[0] ? new WorkoutPlan(rows[0]) : null;
}

async function updatePlan(planId, userId, name, description, isPublished) {
  const sql = `
    UPDATE workout_plan
    SET name = $3, description = $4, is_published = $5
    WHERE id = $1 AND owner_id = $2
    RETURNING id, owner_id, name, description, is_published
  `;

  const { rows } = await db.query(sql, [planId, userId, name, description, isPublished]);

  return rows[0] ? new WorkoutPlan(rows[0]) : null;
}

async function deletePlan(planId, userId) {
  const sql = `
    DELETE FROM workout_plan
    WHERE id = $1 AND owner_id = $2
    RETURNING id
  `;

  const { rows } = await db.query(sql, [planId, userId]);
  return rows.length > 0;
}

module.exports = { createPlan, findPlansByUser, findPlanByIdAndUser, updatePlan, deletePlan };