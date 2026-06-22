const userService = require("../services/userService");

const getProfile = async (req, res) => {
  try {
    const user = await userService.getProfile(req.user.id);
    if (!user) {
      return res.status(404).json({ error: "User not found" });
    }
    res.status(200).json(user);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

const updateProfile = async (req, res) => {
  try {
    const { display_name } = req.body;
    const updated = await userService.updateProfile(req.user.id, display_name);
    res.status(200).json(updated);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

const uploadProfilePicture = async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ error: "No file uploaded" });
    }

    const profilePictureUrl = `/static/uploads/profile-pictures/${req.file.filename}`;

    const updated = await userService.updateProfilePicture(
      req.user.id,
      profilePictureUrl
    );

    res.status(200).json(updated);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

const deleteProfilePicture = async (req, res) => {
  try {
    const updated = await userService.deleteProfilePicture(req.user.id);
    res.status(200).json(updated);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

module.exports = { getProfile, updateProfile, uploadProfilePicture, deleteProfilePicture };