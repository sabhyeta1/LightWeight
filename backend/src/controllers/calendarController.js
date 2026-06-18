const calendarService = require("../services/calendarService");

/*
GET /api/calendar/sessions?from=2026-06-01&to=2026-06-30
*/
const getSessions = async (req, res) => {
  try {
    const userId = req.user.id;
    const { from, to } = req.query;

    const sessions = await calendarService.getSessions(userId, from, to);

    res.status(200).json(sessions);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

/*
POST /api/calendar/sessions

Body:
{
  "workout_plan_id": 1,
  "session_date": "2026-06-20",
  "session_time": "18:00",
  "color_id": 2
}
*/
const createSession = async (req, res) => {
  try {
    const userId = req.user.id;

    const session = await calendarService.createSession(userId, req.body);

    res.status(201).json(session);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

/*
DELETE /api/calendar/sessions/:id
*/
const deleteSession = async (req, res) => {
  try {
    const userId = req.user.id;
    const sessionId = Number(req.params.id);

    const deleted = await calendarService.deleteSession(userId, sessionId);

    if (!deleted) {
      return res.status(404).json({ error: "Calendar session not found" });
    }

    res.status(204).send();
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

/*
POST /api/calendar/recurrences

Body for specific weekdays:
{
  "workout_plan_id": 1,
  "type": "weekdays",
  "weekdays": [1, 3, 5],
  "start_date": "2026-06-20",
  "end_date": "2026-09-20",
  "session_time": "18:00",
  "color_id": 2
}

Weekday numbers:
0 = Sunday
1 = Monday
2 = Tuesday
3 = Wednesday
4 = Thursday
5 = Friday
6 = Saturday

Body for every X days:
{
  "workout_plan_id": 1,
  "type": "interval",
  "interval_days": 3,
  "start_date": "2026-06-20",
  "end_date": "2026-09-20",
  "session_time": "18:00",
  "color_id": 2
}

interval_days:
1 = every day
2 = every 2 days
...
7 = every 7 days
*/
const createRecurrence = async (req, res) => {
  try {
    const userId = req.user.id;

    const result = await calendarService.createRecurrence(userId, req.body);

    res.status(201).json(result);
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

/*
DELETE /api/calendar/recurrences/:id
*/

const deleteRecurrence = async (req, res) => {
  try {
    const userId = req.user.id;
    const recurrenceId = Number(req.params.id);

    const deleted = await calendarService.deleteRecurrence(userId, recurrenceId);

    if (!deleted) {
      return res.status(404).json({ error: "Recurrence not found" });
    }

    res.status(204).send();
  } catch (err) {
    res.status(400).json({ error: err.message });
  }
};

module.exports = {
  getSessions,
  createSession,
  deleteSession,
  createRecurrence,
  deleteRecurrence,
};