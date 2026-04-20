class WorkoutPlan{
    constructor(id, owner, name, description, is_published) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description; 
        this.is_published = is_published;
    }
}

module.exports = WorkoutPlan;