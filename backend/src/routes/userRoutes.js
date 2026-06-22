const express = require("express");
const userController = require("../controllers/userController");
const { requireAuth } = require("../middlewares/authMiddleware");
const upload = require("../middlewares/profilePictureUpload");

const router = express.Router();

// FR-17: GET and PUT /api/user/profile
router.get("/profile", requireAuth, userController.getProfile);
router.put("/profile", requireAuth, userController.updateProfile);
router.post("/profile-picture", requireAuth, upload.single("profile_picture"), userController.uploadProfilePicture);
router.delete("/profile-picture", requireAuth, userController.deleteProfilePicture);

module.exports = router;