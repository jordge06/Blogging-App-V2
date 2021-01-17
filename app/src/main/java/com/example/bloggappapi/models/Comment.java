package com.example.bloggappapi.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Comment implements Parcelable {

    @SerializedName("commentImage")
    @Expose
    private List<Image> commentImage;

//    @SerializedName("replies")
//    @Expose
//    private List<Object> replies = null;

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("postId")
    @Expose
    private String postId;

    @SerializedName("commentedBy")
    @Expose
    private User commentedBy;

    @SerializedName("commentText")
    @Expose
    private String commentText;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    @SerializedName("__v")
    @Expose
    private Integer v;

    protected Comment(Parcel in) {
        commentImage = in.createTypedArrayList(Image.CREATOR);
        id = in.readString();
        postId = in.readString();
        commentedBy = in.readParcelable(User.class.getClassLoader());
        commentText = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        if (in.readByte() == 0) {
            v = null;
        } else {
            v = in.readInt();
        }
    }

    public static final Creator<Comment> CREATOR = new Creator<Comment>() {
        @Override
        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        @Override
        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };

    public List<Image> getCommentImage() {
        return commentImage;
    }

    public void setCommentImage(List<Image> commentImage) {
        this.commentImage = commentImage;
    }

//    public List<Object> getReplies() {
//        return replies;
//    }
//
//    public void setReplies(List<Object> replies) {
//        this.replies = replies;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public User getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(User commentedBy) {
        this.commentedBy = commentedBy;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(commentImage);
        parcel.writeString(id);
        parcel.writeString(postId);
        parcel.writeParcelable(commentedBy, i);
        parcel.writeString(commentText);
        parcel.writeString(createdAt);
        parcel.writeString(updatedAt);
        if (v == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(v);
        }
    }
}
