const db = require("../database");
const WorkoutPlan = require("../models/WorkoutPlan");

async function findPublishedPlans() {
  const sql = `
    SELECT
      wp.id,
      wp.owner_id,
      wp.name,
      wp.description,
      wp.is_published,
      COALESCE(
        ARRAY_AGG(DISTINCT mg.name) FILTER (WHERE mg.name IS NOT NULL),
        '{}'
      ) AS muscle_groups
    FROM workout_plan wp
    LEFT JOIN exercises_workout_plan ewp
      ON ewp.workout_plan_id = wp.id
    LEFT JOIN exercises_muscle_groups emg
      ON emg.exercise_id = ewp.exercise_id
    LEFT JOIN muscle_groups mg
      ON mg.id = emg.muscle_group_id
    WHERE wp.is_published = true
    GROUP BY wp.id, wp.owner_id, wp.name, wp.description, wp.is_published
    ORDER BY wp.id DESC
  `;

  const { rows } = await db.query(sql);
  return rows;
}

async function findPublishedPlanById(planId) {
  const planSql = `
    SELECT 
      id,
      owner_id,
      name,
      description,
      is_published
    FROM workout_plan
    WHERE id = $1
      AND is_published = true
  `;

  const planResult = await db.query(planSql, [planId]);

  if (planResult.rows.length === 0) {
    return null;
  }

  const plan = planResult.rows[0];

  const exercisesSql = `
    SELECT
      ewp.id AS plan_exercise_id,
      ewp.exercise_id,
      ewp."order",
      e.name,
      e.description,
      e.photo_url,
      COALESCE(
        ARRAY_AGG(DISTINCT mg.name) FILTER (WHERE mg.name IS NOT NULL),
        '{}'
      ) AS muscle_groups
    FROM exercises_workout_plan ewp
    JOIN exercises e
      ON e.id = ewp.exercise_id
    LEFT JOIN exercises_muscle_groups emg
      ON emg.exercise_id = e.id
    LEFT JOIN muscle_groups mg
      ON mg.id = emg.muscle_group_id
    WHERE ewp.workout_plan_id = $1
    GROUP BY
      ewp.id,
      ewp.exercise_id,
      ewp."order",
      e.name,
      e.description,
      e.photo_url
    ORDER BY ewp."order" ASC
  `;

  const exercisesResult = await db.query(exercisesSql, [planId]);
  const exercises = exercisesResult.rows;

  if (exercises.length === 0) {
    return {
      ...plan,
      exercises: []
    };
  }

  const planExerciseIds = exercises.map((exercise) => exercise.plan_exercise_id);

  const setsSql = `
    SELECT
      id,
      ewp_id,
      set_number,
      reps,
      weight,
      machine_settings,
      is_drop_set
    FROM exercise_sets
    WHERE ewp_id = ANY($1::int[])
    ORDER BY ewp_id ASC, set_number ASC
  `;

  const setsResult = await db.query(setsSql, [planExerciseIds]);

  const setsByExercise = new Map();

  for (const exercise of exercises) {
    setsByExercise.set(exercise.plan_exercise_id, []);
  }

  for (const set of setsResult.rows) {
    setsByExercise.get(set.ewp_id).push(set);
  }

  return {
    ...plan,
    exercises: exercises.map((exercise) => ({
      ...exercise,
      sets: setsByExercise.get(exercise.plan_exercise_id) || []
    }))
  };
}

async function copyPublishedPlan(planId, userId) {
  const client = await db.connect();

  try {
    await client.query("BEGIN");

    const originalPlanResult = await client.query(
      `
      SELECT id, name, description
      FROM workout_plan
      WHERE id = $1 AND is_published = true
      `,
      [planId]
    );

    if (originalPlanResult.rows.length === 0) {
      await client.query("ROLLBACK");
      return null;
    }

    const originalPlan = originalPlanResult.rows[0];

    const newPlanResult = await client.query(
      `
      INSERT INTO workout_plan (owner_id, name, description, is_published)
      VALUES ($1, $2, $3, false)
      RETURNING id, owner_id, name, description, is_published
      `,
      [userId, originalPlan.name, originalPlan.description]
    );

    const newPlan = newPlanResult.rows[0];

    const originalExercisesResult = await client.query(
      `
      SELECT id, exercise_id, "order"
      FROM exercises_workout_plan
      WHERE workout_plan_id = $1
      ORDER BY "order" ASC
      `,
      [planId]
    );

    for (const originalExercise of originalExercisesResult.rows) {
      const newExerciseResult = await client.query(
        `
        INSERT INTO exercises_workout_plan (workout_plan_id, exercise_id, "order")
        VALUES ($1, $2, $3)
        RETURNING id
        `,
        [newPlan.id, originalExercise.exercise_id, originalExercise.order]
      );

      const newEwpId = newExerciseResult.rows[0].id;

      const originalSetsResult = await client.query(
        `
        SELECT set_number, reps, weight, machine_settings, is_drop_set
        FROM exercise_sets
        WHERE ewp_id = $1
        ORDER BY set_number ASC
        `,
        [originalExercise.id]
      );

      for (const set of originalSetsResult.rows) {
        await client.query(
          `
          INSERT INTO exercise_sets
            (ewp_id, set_number, reps, weight, machine_settings, is_drop_set)
          VALUES ($1, $2, $3, $4, $5, $6)
          `,
          [
            newEwpId,
            set.set_number,
            set.reps,
            set.weight,
            set.machine_settings,
            set.is_drop_set,
          ]
        );
      }
    }

    await client.query("COMMIT");
    return new WorkoutPlan(newPlan);
  } catch (err) {
    await client.query("ROLLBACK");
    throw err;
  } finally {
    client.release();
  }
}

module.exports = {
  findPublishedPlans,
  findPublishedPlanById,
  copyPublishedPlan,
};