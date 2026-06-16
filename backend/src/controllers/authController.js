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

const me = async (req, res) => {
  try {
    const user = await authService.getMe(req.user.id);

    if (!user) {
      return res.status(404).json({ error: "User not found" });
    }

    res.status(200).json(user);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

module.exports = { register, login, me };