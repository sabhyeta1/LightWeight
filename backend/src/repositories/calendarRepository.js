const db = require("../database");

async function getSessions(userId, from, to) {
  const sql = `
    SELECT
      cs.id,
      cs.user_id,
      cs.workout_plan_id,
      wp.name AS workout_plan_name,
      cs.recurrence_rule_id,
      cs.session_date,
      cs.session_time,
      cs.color_id,
      cs.status,
      cs.created_at
    FROM calendar_sessions cs
    JOIN workout_plan wp
      ON wp.id = cs.workout_plan_id
    WHERE cs.user_id = $1
      AND cs.session_date >= $2
      AND cs.session_date <= $3
    ORDER BY cs.session_date ASC, cs.session_time ASC
  `;

  const { rows } = await db.query(sql, [userId, from, to]);
  return rows;
}

async function createSession(userId, data) {
  const sql = `
    INSERT INTO calendar_sessions (
      user_id,
      workout_plan_id,
      session_date,
      session_time,
      color_id
    )
    SELECT
      $1,
      wp.id,
      $3,
      $4,
      $5
    FROM workout_plan wp
    WHERE wp.id = $2
      AND wp.owner_id = $1
    RETURNING
      id,
      user_id,
      workout_plan_id,
      recurrence_rule_id,
      session_date,
      session_time,
      color_id,
      status,
      created_at
  `;

  const { rows } = await db.query(sql, [
    userId,
    data.workout_plan_id,
    data.session_date,
    data.session_time,
    data.color_id,
  ]);

  if (rows.length === 0) {
    throw new Error("Workout plan not found");
  }

  return rows[0];
}

async function deleteSession(userId, sessionId) {
  const sql = `
    DELETE FROM calendar_sessions
    WHERE id = $1
      AND user_id = $2
    RETURNING id
  `;

  const { rows } = await db.query(sql, [sessionId, userId]);

  return rows.length > 0;
}

async function createRecurrence(userId, recurrenceData, sessions) {
  const client = await db.connect();

  try {
    await client.query("BEGIN");

    const recurrenceSql = `
      INSERT INTO recurrence_rules (
        user_id,
        workout_plan_id,
        "type",
        weekdays,
        interval_days,
        start_date,
        end_date,
        session_time,
        color_id
      )
      SELECT
        $1,
        wp.id,
        $3,
        $4,
        $5,
        $6,
        $7,
        $8,
        $9
      FROM workout_plan wp
      WHERE wp.id = $2
        AND wp.owner_id = $1
      RETURNING
        id,
        user_id,
        workout_plan_id,
        "type",
        weekdays,
        interval_days,
        start_date,
        end_date,
        session_time,
        color_id,
        is_active,
        created_at
    `;

    const recurrenceResult = await client.query(recurrenceSql, [
      userId,
      recurrenceData.workout_plan_id,
      recurrenceData.type,
      recurrenceData.weekdays,
      recurrenceData.interval_days,
      recurrenceData.start_date,
      recurrenceData.end_date,
      recurrenceData.session_time,
      recurrenceData.color_id,
    ]);

    if (recurrenceResult.rows.length === 0) {
      await client.query("ROLLBACK");
      throw new Error("Workout plan not found");
    }

    const recurrence = recurrenceResult.rows[0];

    const createdSessions = [];

    for (const session of sessions) {
      const sessionSql = `
        INSERT INTO calendar_sessions (
          user_id,
          workout_plan_id,
          recurrence_rule_id,
          session_date,
          session_time,
          color_id
        )
        VALUES ($1, $2, $3, $4, $5, $6)
        RETURNING
          id,
          user_id,
          workout_plan_id,
          recurrence_rule_id,
          session_date,
          session_time,
          color_id,
          status,
          created_at
      `;

      const sessionResult = await client.query(sessionSql, [
        userId,
        session.workout_plan_id,
        recurrence.id,
        session.session_date,
        session.session_time,
        session.color_id,
      ]);

      createdSessions.push(sessionResult.rows[0]);
    }

    await client.query("COMMIT");

    return {
      recurrence,
      sessions: createdSessions,
    };
  } catch (err) {
    await client.query("ROLLBACK");
    throw err;
  } finally {
    client.release();
  }
}

async function deleteRecurrence(userId, recurrenceId) {
  const client = await db.connect();

  try {
    await client.query("BEGIN");

    const recurrenceResult = await client.query(
      `
      SELECT id
      FROM recurrence_rules
      WHERE id = $1
        AND user_id = $2
        AND is_active = true
      `,
      [recurrenceId, userId]
    );

    if (recurrenceResult.rows.length === 0) {
      await client.query("ROLLBACK");
      return false;
    }

    await client.query(
      `
      DELETE FROM calendar_sessions
      WHERE recurrence_rule_id = $1
        AND user_id = $2
        AND session_date >= CURRENT_DATE
      `,
      [recurrenceId, userId]
    );

    await client.query(
      `
      UPDATE recurrence_rules
      SET is_active = false
      WHERE id = $1
        AND user_id = $2
      `,
      [recurrenceId, userId]
    );

    await client.query("COMMIT");

    return true;
  } catch (err) {
    await client.query("ROLLBACK");
    throw err;
  } finally {
    client.release();
  }
}

module.exports = {
  getSessions,
  createSession,
  deleteSession,
  createRecurrence,
  deleteRecurrence,
};