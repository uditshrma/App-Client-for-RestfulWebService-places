package tk.uditsharma.clientapp.model;

public class CommentResponse {

    private int id;
    private String username;
    private String usermail;
    private String commentText;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsermail() {
        return usermail;
    }

    public void setUsermail(String usermail) {
        this.usermail = usermail;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public CommentResponse(int id, String username, String usermail, String commentText) {
        this.id = id;
        this.username = username;
        this.usermail = usermail;
        this.commentText = commentText;
    }
}
