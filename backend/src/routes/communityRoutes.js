const express = require("express");
const router = express.Router();

const communityController = require("../controllers/communityController");
const { requireAuth } = require("../middlewares/authMiddleware");

router.get("/workout-plans", requireAuth, communityController.getCommunityPlans);
router.get("/workout-plans/:id", requireAuth, communityController.getCommunityPlanById);
router.post("/workout-plans/:id/copy", requireAuth, communityController.copyCommunityPlan);

module.exports = router;