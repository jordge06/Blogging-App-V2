package com.example.bloggappapi.models;

import com.example.bloggappapi.models.Comment;
import com.example.bloggappapi.models.Image;
import com.example.bloggappapi.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "post_table")
public class Post {

    @SerializedName("postImage")
    @Expose
    private List<Image> postImage = null;

    @PrimaryKey
    @SerializedName("_id")
    @Expose
    @NonNull
    private String id;

    @SerializedName("postedBy")
    @Expose
    private User postedBy;

    @SerializedName("comment")
    @Expose
    private List<Comment> comment;

    @SerializedName("postText")
    @Expose
    private String postText;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    @SerializedName("__v")
    @Expose
    private Integer v;

    public List<Image> getPostImage() {
        return postImage;
    }

    public void setPostImage(List<Image> postImage) {
        this.postImage = postImage;
    }

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(User postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return "PostsResponse{" +
                "postImage=" + postImage +
                ", id='" + id + '\'' +
                ", postedBy=" + postedBy +
                ", postText='" + postText + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", v=" + v +
                '}';
    }
}
