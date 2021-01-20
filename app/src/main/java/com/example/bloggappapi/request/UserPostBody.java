package com.example.bloggappapi.request;

import com.google.gson.annotations.SerializedName;

public class UserPostBody {

    @SerializedName("postedBy")
    private String userId;

    @SerializedName("page")
    private int page;

    public UserPostBody(String userId, int page) {
        this.userId = userId;
        this.page = page;
    }
}
