class User {
    constructor({ id, username, display_name, profile_picture_url}){
        this.id = id;
        this.username = username;
        this.display_name = display_name;
        this.profile_picture_url = profile_picture_url;
    }
}

module.exports = User;