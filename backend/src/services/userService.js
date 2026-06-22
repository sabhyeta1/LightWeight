const userRepository = require("../repositories/userRepository");

async function getProfile(userId) {
  return await userRepository.findUserById(userId);
}

async function updateProfile(userId, displayName) {
  if (!displayName || typeof displayName !== "string" || displayName.trim() === "") {
    throw new Error("Display name is required");
  }
  return await userRepository.updateUser(userId, displayName.trim());
}

const updateProfilePicture = async (userId, profilePictureUrl) => {
  return await userRepository.updateProfilePicture(userId, profilePictureUrl);
};

const deleteProfilePicture = async (userId) => {
  return await userRepository.deleteProfilePicture(userId);
};

module.exports = { getProfile, updateProfile, updateProfilePicture, deleteProfilePicture };