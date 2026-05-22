const express = require("express");
const router = express.Router();

const muscleGroupController = require("../controllers/muscleGroupController");

router.get("/", muscleGroupController.getMuscleGroups);

module.exports = router;