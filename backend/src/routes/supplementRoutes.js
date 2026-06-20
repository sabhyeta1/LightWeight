const express = require("express");
const router = express.Router();

const supplementController = require("../controllers/supplementController");
const { requireAuth } = require("../middlewares/authMiddleware");

router.get("/", requireAuth, supplementController.getSupplements);
router.post("/", requireAuth, supplementController.createSupplement);
router.delete("/:id", requireAuth, supplementController.deleteSupplement);

module.exports = router;