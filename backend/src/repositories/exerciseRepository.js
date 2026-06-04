const db = require("../database");

async function findAll(muscleGroupId = null) {
  if (muscleGroupId) {
    const sql = `
      SELECT e.id, e.name, e.description, e.photo_url,
             array_agg(mg.name) AS muscle_groups
      FROM exercises e
      JOIN exercises_muscle_groups emg ON emg.exercise_id = e.id
      JOIN muscle_groups mg ON mg.id = emg.muscle_group_id
      WHERE emg.muscle_group_id = $1
      GROUP BY e.id
      ORDER BY e.name
    `;
    const { rows } = await db.query(sql, [muscleGroupId]);
    return rows;
  }

  const sql = `
    SELECT e.id, e.name, e.description, e.photo_url,
           array_agg(mg.name) AS muscle_groups
    FROM exercises e
    LEFT JOIN exercises_muscle_groups emg ON emg.exercise_id = e.id
    LEFT JOIN muscle_groups mg ON mg.id = emg.muscle_group_id
    GROUP BY e.id
    ORDER BY e.name
  `;
  const { rows } = await db.query(sql);
  return rows;
}

module.exports = { findAll };