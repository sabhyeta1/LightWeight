const db = require("../database");

async function upsertGoal(userId, targetMl) {
  const { rows } = await db.query(
    `INSERT INTO water_goals (user_id, target_ml, updated_at)
     VALUES ($1, $2, now())
     ON CONFLICT (user_id)
     DO UPDATE SET target_ml = $2, updated_at = now()
     RETURNING user_id, target_ml, updated_at`,
    [userId, targetMl]
  );
  return rows[0];
}

async function findGoal(userId) {
  const { rows } = await db.query(
    `SELECT user_id, target_ml, updated_at FROM water_goals WHERE user_id = $1`,
    [userId]
  );
  return rows[0] || null;
}

async function addIntake(userId, amountMl) {
  const { rows } = await db.query(
    `INSERT INTO water_intake_logs (user_id, amount_ml)
     VALUES ($1, $2)
     RETURNING id, user_id, amount_ml, logged_at`,
    [userId, amountMl]
  );
  return rows[0];
}

async function getTodayLogs(userId) {
  const { rows } = await db.query(
    `SELECT id, user_id, amount_ml, logged_at
     FROM water_intake_logs
     WHERE user_id = $1 AND logged_at::date = now()::date
     ORDER BY logged_at ASC`,
    [userId]
  );
  return rows;
}

async function deleteIntake(userId, logId) {
  await db.query(
    `DELETE FROM water_intake_logs WHERE id = $1 AND user_id = $2`,
    [logId, userId]
  );
}

module.exports = { upsertGoal, findGoal, addIntake, getTodayLogs, deleteIntake };