const communityService = require("../services/communityService");

/*const getCommunityPlans = async (req, res) => {
  try {
    const plans = await communityService.getCommunityPlans();
    res.status(200).json(plans);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};*/

const getCommunityPlans = async (req, res) => {
  try {
    const { search = "", filterType = "name" } = req.query;
    const plans = await communityService.getCommunityPlans(search, filterType);
    res.status(200).json(plans);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
};

const getCommunityPlanById = async (req, res) => {
  try {
    const planId = Number(req.params.id);
    const plan = await communityService.getCommunityPlanById(planId);

    if (!plan) {
      return res.status(404).json({ error: "Community plan not found" });
    }

    res.status(200).json(plan);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

const copyCommunityPlan = async (req, res) => {
  try {
    const userId = req.user.id;
    const planId = Number(req.params.id);

    const copiedPlan = await communityService.copyCommunityPlan(planId, userId);

    if (!copiedPlan) {
      return res.status(404).json({ error: "Community plan not found" });
    }

    res.status(201).json(copiedPlan);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

module.exports = { getCommunityPlans, getCommunityPlanById, copyCommunityPlan };