const calendarRepository = require("../repositories/calendarRepository");

const getSessions = async (userId, from, to) => {
  return await calendarRepository.getSessions(userId, from, to);
};

const createSession = async (userId, data) => {
  const sessionData = {
    workout_plan_id: data.workout_plan_id,
    session_date: data.session_date,
    session_time: data.session_time,
    color_id: data.color_id ?? 1,
  };

  return await calendarRepository.createSession(userId, sessionData);
};

const deleteSession = async (userId, sessionId) => {
  return await calendarRepository.deleteSession(userId, sessionId);
};

const createRecurrence = async (userId, data) => {
  const recurrenceData = {
    workout_plan_id: data.workout_plan_id,
    type: data.type,
    weekdays: data.weekdays ?? null,
    interval_days: data.interval_days ?? null,
    start_date: data.start_date,
    end_date: data.end_date,
    session_time: data.session_time,
    color_id: data.color_id ?? 1,
  };

  const sessions = generateSessionsForRecurrence(recurrenceData);

  return await calendarRepository.createRecurrence(
    userId,
    recurrenceData,
    sessions
  );
};

const deleteRecurrence = async (userId, recurrenceId) => {
  return await calendarRepository.deleteRecurrence(userId, recurrenceId);
};

function generateSessionsForRecurrence(recurrenceData) {
  if (recurrenceData.type === "weekdays") {
    return generateWeekdaySessions(recurrenceData);
  }

  if (recurrenceData.type === "interval") {
    return generateIntervalSessions(recurrenceData);
  }

  throw new Error("Invalid recurrence type");
}

function generateWeekdaySessions(recurrenceData) {
  const sessions = [];

  const startDate = parseDate(recurrenceData.start_date);
  const endDate = parseDate(recurrenceData.end_date);

  const weekdays = recurrenceData.weekdays;

  let currentDate = new Date(startDate);

  while (currentDate <= endDate) {
    const currentWeekday = currentDate.getUTCDay();

    if (weekdays.includes(currentWeekday)) {
      sessions.push({
        workout_plan_id: recurrenceData.workout_plan_id,
        session_date: formatDate(currentDate),
        session_time: recurrenceData.session_time,
        color_id: recurrenceData.color_id,
      });
    }

    currentDate.setUTCDate(currentDate.getUTCDate() + 1);
  }

  return sessions;
}

function generateIntervalSessions(recurrenceData) {
  const sessions = [];

  const startDate = parseDate(recurrenceData.start_date);
  const endDate = parseDate(recurrenceData.end_date);

  let currentDate = new Date(startDate);

  while (currentDate <= endDate) {
    sessions.push({
      workout_plan_id: recurrenceData.workout_plan_id,
      session_date: formatDate(currentDate),
      session_time: recurrenceData.session_time,
      color_id: recurrenceData.color_id,
    });

    currentDate.setUTCDate(
      currentDate.getUTCDate() + recurrenceData.interval_days
    );
  }

  return sessions;
}

function parseDate(dateString) {
  return new Date(`${dateString}T00:00:00.000Z`);
}

function formatDate(date) {
  return date.toISOString().split("T")[0];
}

module.exports = {
  getSessions,
  createSession,
  deleteSession,
  createRecurrence,
  deleteRecurrence,
};