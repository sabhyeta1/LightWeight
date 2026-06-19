const express = require("express");
const userController = require("../controllers/userController");
const { requireAuth } = require("../middlewares/authMiddleware");

const router = express.Router();

// FR-17: GET and PATCH /api/user/profile
router.get("/profile", requireAuth, userController.getProfile);
router.patch("/profile", requireAuth, userController.updateProfile);

module.exports = router;