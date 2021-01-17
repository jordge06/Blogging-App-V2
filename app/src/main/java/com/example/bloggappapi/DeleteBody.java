package com.example.bloggappapi;

import com.google.gson.annotations.SerializedName;

public class DeleteBody {

    @SerializedName("postId")
    private String postId;

    public DeleteBody(String postId) {
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
