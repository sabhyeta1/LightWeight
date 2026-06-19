const express = require("express");
const router = express.Router();

const calendarController = require("../controllers/calendarController");
const { requireAuth } = require("../middlewares/authMiddleware");

router.get("/sessions", requireAuth, calendarController.getSessions);
router.post("/sessions", requireAuth, calendarController.createSession);
router.patch("/sessions/:id", requireAuth, calendarController.updateSession);
router.delete("/sessions/:id", requireAuth, calendarController.deleteSession);

router.post("/recurrences", requireAuth, calendarController.createRecurrence);
router.patch("/recurrences/:id/sessions", requireAuth, calendarController.updateFutureSessions);
router.delete("/recurrences/:id", requireAuth, calendarController.deleteRecurrence);

module.exports = router;