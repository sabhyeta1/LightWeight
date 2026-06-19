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
    const { display_name, profile_picture_url } = req.body;
    const updated = await userService.updateProfile(req.user.id, display_name, profile_picture_url);
    res.status(200).json(updated);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

module.exports = { getProfile, updateProfile };