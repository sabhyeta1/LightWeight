const authService = require("../services/authService");

const register = async (req, res) => {
  try {
    const { username, password, display_name, profile_picture_url } = req.body;
    const result = await authService.register(username, password, display_name, profile_picture_url);
    res.status(201).json(result);
  } catch (err) {
    // Unique in Postgres = 23505
    if (err.code === "23505") {
      return res.status(409).json({ error: "Username already exists" });
    }
    res.status(400).json({ error: err.message });
  }
};

const login = async (req, res) => {
  try {
    const { username, password } = req.body;
    const result = await authService.login(username, password);
    res.status(200).json(result);
  } catch (err) {
    res.status(401).json({ error: err.message });
  }
};

module.exports = { register, login };