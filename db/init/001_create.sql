DROP TABLE IF EXISTS public.calendar_sessions;
DROP TABLE IF EXISTS public.recurrence_rules;
DROP TABLE IF EXISTS public.exercise_sets;
DROP TABLE IF EXISTS public.exercises_workout_plan;
DROP TABLE IF EXISTS public.exercises_muscle_groups;
DROP TABLE IF EXISTS public.workout_plan;
DROP TABLE IF EXISTS public.muscle_groups;
DROP TABLE IF EXISTS public.exercises;
DROP TABLE IF EXISTS public.users;
DROP TABLE IF EXISTS public.water_goals;
DROP TABLE IF EXISTS public.water_intake_logs;

CREATE TABLE public.users (
	id serial NOT NULL,
	username text NOT NULL,
	"password" text NOT NULL,
	display_name text NOT NULL,
	profile_picture_url text NULL,
	CONSTRAINT users_pk PRIMARY KEY (id),
	CONSTRAINT users_unique UNIQUE (username)
);

CREATE TABLE public.exercises (
	id serial NOT NULL,
	"name" text NOT NULL,
	description text NULL,
	photo_url text NULL,
	CONSTRAINT exercises_pk PRIMARY KEY (id)
);

CREATE TABLE public.muscle_groups (
	id serial4 NOT NULL,
	"name" text NOT NULL,
	CONSTRAINT muscle_groups_pk PRIMARY KEY (id)
);

CREATE TABLE public.exercises_muscle_groups (
	exercise_id int4 NOT NULL,
	muscle_group_id int4 NOT NULL,
	CONSTRAINT exercises_muscle_groups_pk PRIMARY KEY (exercise_id, muscle_group_id),
	CONSTRAINT exercises_muscle_groups_exercises_fk FOREIGN KEY (exercise_id) REFERENCES public.exercises(id),
	CONSTRAINT exercises_muscle_groups_muscle_groups_fk FOREIGN KEY (muscle_group_id) REFERENCES public.muscle_groups(id)
);

CREATE TABLE public.workout_plan (
	id serial4 NOT NULL,
	owner_id int4 NOT NULL,
	"name" text NOT NULL,
	description text NULL,
	is_published bool NOT NULL,
	CONSTRAINT workout_plan_pk PRIMARY KEY (id),
	CONSTRAINT workout_plan_users_fk FOREIGN KEY (owner_id) REFERENCES public.users(id)
);

CREATE TABLE public.exercises_workout_plan (
	id serial4 NOT NULL,
	workout_plan_id int4 NOT NULL,
	exercise_id int4 NOT NULL,
	"order" int4 NOT NULL,
	CONSTRAINT exercises_workout_plan_pk PRIMARY KEY (id),
	CONSTRAINT exercises_workout_plan_unique UNIQUE (workout_plan_id, exercise_id),
	CONSTRAINT exercises_workout_plan_exercises_fk FOREIGN KEY (exercise_id) REFERENCES public.exercises(id),
	CONSTRAINT exercises_workout_plan_workout_plan_fk FOREIGN KEY (workout_plan_id) REFERENCES public.workout_plan(id)
);

CREATE TABLE public.exercise_sets (
	id serial4 NOT NULL,
	ewp_id int4 NOT NULL,
	set_number int4 NULL,
	reps int4 NULL,
	weight numeric NULL,
	machine_settings text NULL,
	is_drop_set bool NULL,
	CONSTRAINT exercise_sets_pk PRIMARY KEY (id),
	CONSTRAINT exercise_sets_exercises_workout_plan_fk FOREIGN KEY (ewp_id) REFERENCES public.exercises_workout_plan(id)
);

CREATE TABLE public.recurrence_rules (
	id serial4 NOT NULL,
	user_id int4 NOT NULL,
	workout_plan_id int4 NOT NULL,
	"type" text NOT NULL,
	weekdays int4[] NULL,
	interval_days int4 NULL,
	start_date date NOT NULL,
	end_date date NOT NULL,
	session_time time NOT NULL,
	color_id int4 NOT NULL DEFAULT 1,
	is_active bool NOT NULL DEFAULT true,
	created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT recurrence_rules_pk PRIMARY KEY (id),
	CONSTRAINT recurrence_rules_type_check CHECK ("type" IN ('weekdays', 'interval')),
	CONSTRAINT recurrence_rules_interval_days_check CHECK (interval_days IS NULL OR interval_days BETWEEN 1 AND 7),
	CONSTRAINT recurrence_rules_users_fk FOREIGN KEY (user_id) REFERENCES public.users(id),
	CONSTRAINT recurrence_rules_workout_plan_fk FOREIGN KEY (workout_plan_id) REFERENCES public.workout_plan(id)
);

CREATE TABLE public.calendar_sessions (
	id serial4 NOT NULL,
	user_id int4 NOT NULL,
	workout_plan_id int4 NOT NULL,
	recurrence_rule_id int4 NULL,
	session_date date NOT NULL,
	session_time time NOT NULL,
	color_id int4 NOT NULL DEFAULT 1,
	status text NOT NULL DEFAULT 'scheduled',
	created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT calendar_sessions_pk PRIMARY KEY (id),
	CONSTRAINT calendar_sessions_status_check CHECK (status IN ('scheduled', 'completed')),
	CONSTRAINT calendar_sessions_users_fk FOREIGN KEY (user_id) REFERENCES public.users(id),
	CONSTRAINT calendar_sessions_workout_plan_fk FOREIGN KEY (workout_plan_id) REFERENCES public.workout_plan(id),
	CONSTRAINT calendar_sessions_recurrence_rules_fk FOREIGN KEY (recurrence_rule_id) REFERENCES public.recurrence_rules(id) ON DELETE SET NULL
);

CREATE TABLE public.water_goals (
    user_id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    target_ml INTEGER NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE public.water_intake_logs (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    amount_ml INTEGER NOT NULL,
    logged_at TIMESTAMP NOT NULL DEFAULT now()
);