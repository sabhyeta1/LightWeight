const db = require("../database");
const User = require("../models/User");

// Create user
async function createUser(username, passwordHash, displayName = null, profile_picture_url = null) {
  const sql = `
    INSERT INTO users (username, "password", display_name, profile_picture_url)
    VALUES ($1, $2, $3, $4)
    RETURNING id, username, display_name, profile_picture_url
  `;
  const { rows } = await db.query(sql, [username, passwordHash, displayName, profile_picture_url]);
  return new User(rows[0]);
}

// Get user by password
async function findUserWithPasswordByUsername(username) {
  const sql = `
    SELECT id, username, "password", display_name, profile_picture_url
    FROM users
    WHERE username = $1
  `;
  const { rows } = await db.query(sql, [username]);
  return rows[0] || null;
}

// Find user by id
async function findUserById(id) {
  const sql = `
    SELECT id, username, display_name, profile_picture_url
    FROM users
    WHERE id = $1
  `;
  const { rows } = await db.query(sql, [id]);
  return rows[0] ? new User(rows[0]) : null;
}

// FR-17: Update display_name
async function updateUser(userId, displayName) {
  const sql = `
    UPDATE users
    SET display_name = $2
    WHERE id = $1
    RETURNING id, username, display_name, profile_picture_url
  `;
  const { rows } = await db.query(sql, [userId, displayName]);
  return rows[0] ? new User(rows[0]) : null;
}

async function updateProfilePicture(userId, profilePictureUrl) {
  const sql = `
    UPDATE users
    SET profile_picture_url = $2
    WHERE id = $1
    RETURNING id, username, display_name, profile_picture_url
  `;

  const { rows } = await db.query(sql, [userId, profilePictureUrl]);

  return rows[0] ? new User(rows[0]) : null;
}

async function deleteProfilePicture(userId) {
  const sql = `
    UPDATE users
    SET profile_picture_url = NULL
    WHERE id = $1
    RETURNING id, username, display_name, profile_picture_url
  `;

  const { rows } = await db.query(sql, [userId]);
  return rows[0] ? new User(rows[0]) : null;
}

module.exports = { createUser, findUserWithPasswordByUsername, findUserById, updateUser, updateProfilePicture, deleteProfilePicture };