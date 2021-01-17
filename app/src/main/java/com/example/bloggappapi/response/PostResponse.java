package com.example.bloggappapi.response;

import com.example.bloggappapi.models.Post;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostResponse {

    @SerializedName("post_page")
    private int page;

    @SerializedName("data")
    private List<Post> postList;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }
}
