-- MUSCLE GROUPS
INSERT INTO muscle_groups (name) VALUES
('Chest'),
('Back'),
('Shoulders'),
('Biceps'),
('Triceps'),
('Legs'),
('Abs'),
('Forearms'),
('Glutes'),
('Calves');



-- EXERISES
INSERT INTO exercises (name, description, photo_url) VALUES
('Bench Press', 'Barbell chest press on flat bench.', '/static/images/ic_launcher-playstore.png'),
('Incline Dumbbell Press', 'Incline dumbbell chest press.', '/static/images/ic_launcher-playstore.png'),
('Chest Fly', 'Machine or cable chest fly movement.', '/static/images/ic_launcher-playstore.png'),

('Pull Up', 'Vertical pulling exercise for the back.', '/static/images/ic_launcher-playstore.png'),
('Lat Pulldown', 'Cable lat pulldown exercise.', '/static/images/ic_launcher-playstore.png'),
('Barbell Row', 'Bent over rowing movement.', '/static/images/ic_launcher-playstore.png'),

('Shoulder Press', 'Overhead shoulder press.', '/static/images/ic_launcher-playstore.png'),
('Lateral Raise', 'Isolation exercise for side delts.', '/static/images/ic_launcher-playstore.png'),

('Bicep Curl', 'Classic dumbbell bicep curl.', '/static/images/ic_launcher-playstore.png'),
('Hammer Curl', 'Neutral grip dumbbell curl.', '/static/images/ic_launcher-playstore.png'),

('Tricep Pushdown', 'Cable tricep pushdown.', '/static/images/ic_launcher-playstore.png'),
('Overhead Tricep Extension', 'Overhead tricep extension.', '/static/images/ic_launcher-playstore.png'),

('Squat', 'Barbell squat for lower body.', '/static/images/ic_launcher-playstore.png'),
('Leg Press', 'Machine based leg press.', '/static/images/ic_launcher-playstore.png'),
('Romanian Deadlift', 'Hip hinge exercise for hamstrings/glutes.', '/static/images/ic_launcher-playstore.png'),
('Leg Extension', 'Quad isolation exercise.', '/static/images/ic_launcher-playstore.png'),
('Leg Curl', 'Hamstring isolation exercise.', '/static/images/ic_launcher-playstore.png'),

('Standing Calf Raise', 'Calf isolation movement.', '/static/images/ic_launcher-playstore.png'),

('Crunch', 'Abdominal crunch exercise.', '/static/images/ic_launcher-playstore.png'),
('Plank', 'Static core stabilization exercise.', '/static/images/ic_launcher-playstore.png');



-- EXERCISE <-> MUSCLE GROUP LINKS

-- Chest
INSERT INTO exercises_muscle_groups (exercise_id, muscle_group_id)
SELECT e.id, m.id
FROM exercises e, muscle_groups m
WHERE e.name IN ('Bench Press', 'Incline Dumbbell Press', 'Chest Fly')
AND m.name = 'Chest';

-- Back
INSERT INTO exercises_muscle_groups (exercise_id, muscle_group_id)
SELECT e.id, m.id
FROM exercises e, muscle_groups m
WHERE e.name IN ('Pull Up', 'Lat Pulldown', 'Barbell Row')
AND m.name = 'Back';

-- Shoulders
INSERT INTO exercises_muscle_groups (exercise_id, muscle_group_id)
SELECT e.id, m.id
FROM exercises e, muscle_groups m
WHERE e.name IN ('Shoulder Press', 'Lateral Raise')
AND m.name = 'Shoulders';

-- Biceps
INSERT INTO exercises_muscle_groups (exercise_id, muscle_group_id)
SELECT e.id, m.id
FROM exercises e, muscle_groups m
WHERE e.name IN ('Bicep Curl', 'Hammer Curl')
AND m.name = 'Biceps';

-- Triceps
INSERT INTO exercises_muscle_groups (exercise_id, muscle_group_id)
SELECT e.id, m.id
FROM exercises e, muscle_groups m
WHERE e.name IN ('Tricep Pushdown', 'Overhead Tricep Extension')
AND m.name = 'Triceps';

-- Legs
INSERT INTO exercises_muscle_groups (exercise_id, muscle_group_id)
SELECT e.id, m.id
FROM exercises e, muscle_groups m
WHERE e.name IN ('Squat', 'Leg Press', 'Romanian Deadlift', 'Leg Extension', 'Leg Curl')
AND m.name = 'Legs';

-- Abs
INSERT INTO exercises_muscle_groups (exercise_id, muscle_group_id)
SELECT e.id, m.id
FROM exercises e, muscle_groups m
WHERE e.name IN ('Crunch', 'Plank')
AND m.name = 'Abs';

-- Calves
INSERT INTO exercises_muscle_groups (exercise_id, muscle_group_id)
SELECT e.id, m.id
FROM exercises e, muscle_groups m
WHERE e.name IN ('Standing Calf Raise')
AND m.name = 'Calves';

-- Glutes
INSERT INTO exercises_muscle_groups (exercise_id, muscle_group_id)
SELECT e.id, m.id
FROM exercises e, muscle_groups m
WHERE e.name IN ('Squat', 'Romanian Deadlift')
AND m.name = 'Glutes';

-- Forearms
INSERT INTO exercises_muscle_groups (exercise_id, muscle_group_id)
SELECT e.id, m.id
FROM exercises e, muscle_groups m
WHERE e.name IN ('Hammer Curl')
AND m.name = 'Forearms';