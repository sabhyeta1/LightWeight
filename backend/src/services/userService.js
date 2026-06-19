const userRepository = require("../repositories/userRepository");

async function getProfile(userId) {
  return await userRepository.findUserById(userId);
}

async function updateProfile(userId, displayName, profilePictureUrl) {
  if (!displayName || typeof displayName !== "string" || displayName.trim() === "") {
    throw new Error("Display name is required");
  }
  return await userRepository.updateUser(userId, displayName.trim(), profilePictureUrl ?? null);
}

module.exports = { getProfile, updateProfile };