package co.edu.unipiloto.myappcash.models;

public class Comment {
    private String userId;
    private float rating;
    private String comment;

    public Comment() {

    }

    public Comment(String userId, float rating, String comment) {
        this.userId = userId;
        this.rating = rating;
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}
