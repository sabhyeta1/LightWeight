const communityRepository = require("../repositories/communityRepository");

/*const getCommunityPlans = async () => {
  return await communityRepository.findPublishedPlans();
};*/

const getCommunityPlans = async (search = "", filterType = "name") => {
  return await communityRepository.findPublishedPlans(search, filterType);
};

const getCommunityPlanById = async (planId) => {
  if (!Number.isInteger(planId)) throw new Error("Invalid plan id");
  return await communityRepository.findPublishedPlanById(planId);
};

const copyCommunityPlan = async (planId, userId) => {
  if (!Number.isInteger(planId)) throw new Error("Invalid plan id");
  return await communityRepository.copyPublishedPlan(planId, userId);
};

module.exports = { getCommunityPlans, getCommunityPlanById, copyCommunityPlan };