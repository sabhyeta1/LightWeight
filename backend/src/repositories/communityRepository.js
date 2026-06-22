const db = require("../database");
const WorkoutPlan = require("../models/WorkoutPlan");

async function findPublishedPlans(search = "", filterType = "name") {
  const params = [];
  let filterClause = "";

  if (search && search.trim() !== "") {
    params.push(`%${search.trim()}%`);

    if (filterType === "muscle group") {
      // nur Plans, die mind. eine Exercise mit passender Muscle Group haben
      filterClause = `
        AND EXISTS (
          SELECT 1
          FROM exercises_workout_plan ewp2
          JOIN exercises_muscle_groups emg2
            ON emg2.exercise_id = ewp2.exercise_id
          JOIN muscle_groups mg2
            ON mg2.id = emg2.muscle_group_id
          WHERE ewp2.workout_plan_id = wp.id
            AND mg2.name ILIKE $1
        )
      `;
    } else {
      // default: nach name suchen
      filterClause = "AND wp.name ILIKE $1";
    }
  }

  const sql = `
    SELECT
      wp.id,
      wp.owner_id,
      wp.name,
      wp.description,
      wp.is_published,
      u.display_name AS owner_name,
      COALESCE(
        ARRAY_AGG(DISTINCT mg.name) FILTER (WHERE mg.name IS NOT NULL),
        '{}'
      ) AS muscle_groups
    FROM workout_plan wp
    JOIN users u
      ON u.id = wp.owner_id
    LEFT JOIN exercises_workout_plan ewp
      ON ewp.workout_plan_id = wp.id
    LEFT JOIN exercises_muscle_groups emg
      ON emg.exercise_id = ewp.exercise_id
    LEFT JOIN muscle_groups mg
      ON mg.id = emg.muscle_group_id
    WHERE wp.is_published = true
    ${filterClause}
    GROUP BY wp.id, wp.owner_id, wp.name, wp.description, wp.is_published, u.display_name
    ORDER BY wp.id DESC
  `;

  const { rows } = await db.query(sql, params);
  return rows;
}

/*async function findPublishedPlans() {
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
}*/

async function findPublishedPlanById(planId) {
  const planSql = `
    SELECT 
      wp.id,
      wp.owner_id,
      wp.name,
      wp.description,
      wp.is_published,
      u.display_name AS owner_name
    FROM workout_plan wp
    JOIN users u ON u.id = wp.owner_id
    WHERE wp.id = $1
      AND wp.is_published = true
  `;

  const planResult = await db.query(planSql, [planId]);

  if (planResult.rows.length === 0) {
    return null;
  }

  const plan = planResult.rows[0];

  const exercisesSql = `
    SELECT
      ewp.id AS ewp_id,
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

  const planExerciseIds = exercises.map((exercise) => exercise.ewp_id);
  
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
    setsByExercise.set(exercise.ewp_id, []);
  }

  for (const set of setsResult.rows) {
    setsByExercise.get(set.ewp_id).push(set);
  }

  return {
    ...plan,
    exercises: exercises.map((exercise) => ({
      ...exercise,
      sets: setsByExercise.get(exercise.ewp_id) || []
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

async function savePlan(userId, planId) {
  await db.query(
    `INSERT INTO saved_plans (user_id, workout_plan_id)
     VALUES ($1, $2)
     ON CONFLICT (user_id, workout_plan_id) DO NOTHING`,
    [userId, planId]
  );
}

async function unsavePlan(userId, planId) {
  await db.query(
    `DELETE FROM saved_plans WHERE user_id = $1 AND workout_plan_id = $2`,
    [userId, planId]
  );
}

async function findSavedPlans(userId) {
  const sql = `
    SELECT
      wp.id,
      wp.owner_id,
      wp.name,
      wp.description,
      wp.is_published,
      u.display_name AS owner_name,
      COALESCE(
        ARRAY_AGG(DISTINCT mg.name) FILTER (WHERE mg.name IS NOT NULL),
        '{}'
      ) AS muscle_groups
    FROM saved_plans sp
    JOIN workout_plan wp ON wp.id = sp.workout_plan_id
    JOIN users u ON u.id = wp.owner_id
    LEFT JOIN exercises_workout_plan ewp ON ewp.workout_plan_id = wp.id
    LEFT JOIN exercises_muscle_groups emg ON emg.exercise_id = ewp.exercise_id
    LEFT JOIN muscle_groups mg ON mg.id = emg.muscle_group_id
    WHERE sp.user_id = $1
    GROUP BY wp.id, wp.owner_id, wp.name, wp.description, wp.is_published, u.display_name, sp.saved_at
    ORDER BY sp.saved_at DESC
  `;
  const { rows } = await db.query(sql, [userId]);
  return rows;
}

module.exports = {
  findPublishedPlans,
  findPublishedPlanById,
  copyPublishedPlan,
  savePlan,
  unsavePlan,
  findSavedPlans,
};
