DROP TABLE IF EXISTS public.users;
DROP TABLE IF EXISTS public.exercises;
DROP TABLE IF EXISTS public.muscle_groups;
DROP TABLE IF EXISTS public.exercises_muscle_groups;
DROP TABLE IF EXISTS public.workout_plan;
DROP TABLE IF EXISTS public.exercises_workout_plan;
DROP TABLE IF EXISTS public.exercise_sets;

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