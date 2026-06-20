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

const savePlan = async (planId, userId) => {
  if (!Number.isInteger(planId)) throw new Error("Invalid plan id");
  return await communityRepository.savePlan(userId, planId);
};

const unsavePlan = async (planId, userId) => {
  if (!Number.isInteger(planId)) throw new Error("Invalid plan id");
  return await communityRepository.unsavePlan(userId, planId);
};

const getSavedPlans = async (userId) => {
  return await communityRepository.findSavedPlans(userId);
};

module.exports = {
  getCommunityPlans,
  getCommunityPlanById,
  copyCommunityPlan,
  savePlan,
  unsavePlan,
  getSavedPlans,
};
