const db = require('../database');
const WorkoutPlan = require('../models/WorkoutPlan');

const createPlan = (owner, name, description) => {
    return new Promise((resolve, reject) => {
        const sql = 'INSERT INTO workout_plan (owner, name, description, is_published) VALUES (?,?,?,0)';
        db.run(sql, [owner, name, description], function(err){
            if (err) reject(err);
            else resolve(new WorkoutPlan({
                id: this.lastID,
                owner,
                name,
                description,
                is_published: false
            }));
        });
    });
};

const findPlansByUser = (userId) => {
    return new Promise((resolve, reject) => {
        const sql = 'SELECT * FROM workout_plan WHERE owner = ?'
        db.all(sql,[userId], (err,rows) => {
            if (err) reject (err);
            else resolve(rows.map(rows => new WorkoutPlan(row)));
        });
    });
};

module.exports = {createPlan, findPlansByUser};