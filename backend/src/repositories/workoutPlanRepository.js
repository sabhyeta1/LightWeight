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

/*async function findPlanByIdAndUser(planId, userId) {
  const sql = `
    SELECT id, owner_id, name, description, is_published
    FROM workout_plan
    WHERE id = $1 AND owner_id = $2
  `;

  const { rows } = await db.query(sql, [planId, userId]);
  return rows[0] ? new WorkoutPlan(rows[0]) : null;
}*/

async function findPlanByIdAndUser(planId, userId) {
  // Query 1: Plan + Exercises
  const planSql = `
    SELECT wp.id, wp.name, wp.description, wp.is_published,
           ewp.id AS ewp_id, ewp.order,
           e.id AS exercise_id, e.name AS exercise_name, e.photo_url
    FROM workout_plan wp
    LEFT JOIN exercises_workout_plan ewp ON ewp.workout_plan_id = wp.id
    LEFT JOIN exercises e ON e.id = ewp.exercise_id
    WHERE wp.id = $1 AND wp.owner_id = $2
    ORDER BY ewp.order
  `;
  const { rows: planRows } = await db.query(planSql, [planId, userId]);
  if (planRows.length === 0) return null;

  // Query 2: alle Sets für diesen Plan
  const setsSql = `
    SELECT es.id, es.ewp_id, es.set_number, es.reps,
           es.weight, es.machine_settings, es.is_drop_set
    FROM exercise_sets es
    INNER JOIN exercises_workout_plan ewp ON ewp.id = es.ewp_id
    WHERE ewp.workout_plan_id = $1
    ORDER BY es.set_number
  `;
  const { rows: setRows } = await db.query(setsSql, [planId]);

  // Zusammenbauen
  const plan = {
    id: planRows[0].id,
    name: planRows[0].name,
    description: planRows[0].description,
    is_published: planRows[0].is_published,
    exercises: planRows
      .filter(row => row.ewp_id !== null)
      .map(row => ({
        ewp_id: row.ewp_id,
        order: row.order,
        exercise_id: row.exercise_id,
        name: row.exercise_name,
        photo_url: row.photo_url,
        sets: setRows.filter(s => s.ewp_id === row.ewp_id)
      }))
  };

  return plan;
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