package com.example.bloggappapi.request;

import com.google.gson.annotations.SerializedName;

public class DeleteCommentBody {

    @SerializedName("commentId")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
