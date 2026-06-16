const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const userRepository = require("../repositories/userRepository");
const User = require("../models/User");

const JWT_SECRET = process.env.JWT_SECRET;
const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || "7d";

if (!JWT_SECRET) {
  throw new Error("JWT_SECRET is not set");
}

async function register(username, password, displayName = null, profile_picture_url = null) {
  const cleanUsername = typeof username === "string" ? username.trim() : "";
  
  if (!cleanUsername) {
    throw new Error("Username is required");
  }

  if (typeof password !== "string" || password.length < 6) {
    throw new Error("Password must be at least 6 characters");
  }

  const passwordHash = await bcrypt.hash(password, 10);

  const user = await userRepository.createUser(cleanUsername, passwordHash, displayName, profile_picture_url);

  const token = jwt.sign({ userId: user.id }, JWT_SECRET, { expiresIn: JWT_EXPIRES_IN });
  return { user, token };
}

async function login(username, password) {
  const cleanUsername = typeof username === "string" ? username.trim() : "";
  
  if (!cleanUsername) {
    throw new Error("Username is required");
  }

  if (typeof password !== "string") {
    throw new Error("Password is required");
  }

  const row = await userRepository.findUserWithPasswordByUsername(cleanUsername);

  if (!row) {
    throw new Error("Invalid credentials")
  }

  const ok = await bcrypt.compare(password, row.password);
  if (!ok) {
    throw new Error("Invalid credentials")
  }

  const user = new User({
    id: row.id, 
    username: row.username, 
    display_name: row.display_name, 
    profile_picture_url: row.profile_picture_url
  });

  const token = jwt.sign({ userId: user.id }, JWT_SECRET, { expiresIn: JWT_EXPIRES_IN });
  return { user, token };
}

async function getMe(userId) {
  return await userRepository.findUserById(userId);
}

module.exports = { register, login, getMe };