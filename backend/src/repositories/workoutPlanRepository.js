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
  const client = await db.connect();

  try {
    await client.query("BEGIN");

    const planResult = await client.query(
      `
      SELECT id
      FROM workout_plan
      WHERE id = $1 AND owner_id = $2
      `,
      [planId, userId]
    );

    if (planResult.rows.length === 0) {
      await client.query("ROLLBACK");
      return false;
    }

    await client.query(
      `
      DELETE FROM exercise_sets
      WHERE ewp_id IN (
        SELECT id
        FROM exercises_workout_plan
        WHERE workout_plan_id = $1
      )
      `,
      [planId]
    );

    await client.query(
      `
      DELETE FROM exercises_workout_plan
      WHERE workout_plan_id = $1
      `,
      [planId]
    );

    await client.query(
      `
      DELETE FROM calendar_sessions
      WHERE workout_plan_id = $1
        AND user_id = $2
      `,
      [planId, userId]
    );

    await client.query(
      `
      DELETE FROM recurrence_rules
      WHERE workout_plan_id = $1
        AND user_id = $2
      `,
      [planId, userId]
    );

    await client.query(
      `
      DELETE FROM workout_plan
      WHERE id = $1 AND owner_id = $2
      `,
      [planId, userId]
    );

    await client.query("COMMIT");
    return true;
  } catch (err) {
    await client.query("ROLLBACK");
    throw err;
  } finally {
    client.release();
  }
}

// FR-07: add exercise to plan
async function addExercise(planId, exerciseId, order) {
  const sql = `
    INSERT INTO exercises_workout_plan (workout_plan_id, exercise_id, "order")
    VALUES ($1, $2, $3)
    RETURNING id, workout_plan_id, exercise_id, "order"
  `;
  const { rows } = await db.query(sql, [planId, exerciseId, order]);
  return rows[0];
}

// FR-08: replace all sets for an exercise entry
async function replaceSets(ewpId, sets) {
  await db.query("DELETE FROM exercise_sets WHERE ewp_id = $1", [ewpId]);
  if (!sets || sets.length === 0) return [];

  const inserted = [];
  for (const s of sets) {
    const sql = `
      INSERT INTO exercise_sets (ewp_id, set_number, reps, weight, machine_settings, is_drop_set)
      VALUES ($1, $2, $3, $4, $5, $6)
      RETURNING *
    `;
    const { rows } = await db.query(sql, [
      ewpId,
      s.set_number,
      s.reps,
      s.weight,
      s.machine_settings ?? null,
      s.is_drop_set ?? false,
    ]);
    inserted.push(rows[0]);
  }
  return inserted;
}

// remove exercise (and its sets) from plan
async function removeExercise(ewpId) {
  await db.query("DELETE FROM exercise_sets WHERE ewp_id = $1", [ewpId]);
  await db.query("DELETE FROM exercises_workout_plan WHERE id = $1", [ewpId]);
}

async function getExerciseSets(planId, ewpId, userId) {
  const sql = `
    SELECT 
      es.id,
      es.ewp_id,
      es.set_number,
      es.reps,
      es.weight,
      es.machine_settings,
      es.is_drop_set
    FROM exercise_sets es
    JOIN exercises_workout_plan ewp ON ewp.id = es.ewp_id
    JOIN workout_plan wp ON wp.id = ewp.workout_plan_id
    WHERE wp.id = $1
      AND ewp.id = $2
      AND wp.owner_id = $3
    ORDER BY es.set_number ASC
  `;

  const { rows } = await db.query(sql, [planId, ewpId, userId]);
  return rows;
}

async function setPublished(planId, userId, isPublished) {
  const sql = `
    UPDATE workout_plan
    SET is_published = $3
    WHERE id = $1 AND owner_id = $2
    RETURNING id, owner_id, name, description, is_published
  `;

  const { rows } = await db.query(sql, [planId, userId, isPublished]);
  return rows[0] ? new WorkoutPlan(rows[0]) : null;
}

module.exports = { createPlan, findPlansByUser, findPlanByIdAndUser, updatePlan, deletePlan, addExercise, replaceSets, removeExercise, getExerciseSets, setPublished };