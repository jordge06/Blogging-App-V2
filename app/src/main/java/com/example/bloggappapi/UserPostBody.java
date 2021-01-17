package com.example.bloggappapi;

import com.google.gson.annotations.SerializedName;

public class UserPostBody {

    @SerializedName("postedBy")
    private String userId;

    public UserPostBody(String userId) {
        this.userId = userId;
    }
}
