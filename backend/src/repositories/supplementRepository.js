const db = require("../database");

async function createSupplement(userId, name, dosage) {
  const { rows } = await db.query(
    `INSERT INTO supplements (user_id, name, dosage)
     VALUES ($1, $2, $3)
     RETURNING id, user_id, name, dosage, created_at`,
    [userId, name, dosage]
  );
  return rows[0];
}

async function findSupplementsByUser(userId) {
  const { rows } = await db.query(
    `SELECT id, user_id, name, dosage, created_at
     FROM supplements
     WHERE user_id = $1
     ORDER BY created_at DESC`,
    [userId]
  );
  return rows;
}

async function deleteSupplement(userId, supplementId) {
  await db.query(
    `DELETE FROM supplements WHERE id = $1 AND user_id = $2`,
    [supplementId, userId]
  );
}

module.exports = {
  createSupplement,
  findSupplementsByUser,
  deleteSupplement,
};