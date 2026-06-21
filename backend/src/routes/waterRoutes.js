const express = require("express");
const router = express.Router();

const waterController = require("../controllers/waterController");
const { requireAuth } = require("../middlewares/authMiddleware");

router.get("/status", requireAuth, waterController.getStatus);
router.put("/goal", requireAuth, waterController.setGoal);
router.post("/intake", requireAuth, waterController.addIntake);
router.delete("/intake/:id", requireAuth, waterController.deleteIntake);

module.exports = router;