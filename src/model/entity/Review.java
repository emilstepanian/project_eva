package model.entity;

/**
 * Created by emilstepanian on 19/11/2016.
 */
public class Review {

    private int id;
    private int userId;
    private int lectureId;
    private int rating;
    private String comment;
    private boolean isDeleted;

    public Review() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLectureId() {
        return lectureId;
    }

    public void setLectureId(int lectureId) {
        this.lectureId = lectureId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", userId=" + userId +
                ", lectureId=" + lectureId +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}