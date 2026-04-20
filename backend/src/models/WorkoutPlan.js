class WorkoutPlan{
    constructor({id, owner_id, name, description, is_published}) {
        this.id = id;
        this.owner_id = owner_id;
        this.name = name;
        this.description = description; 
        this.is_published = is_published;
    }
}

module.exports = WorkoutPlan;