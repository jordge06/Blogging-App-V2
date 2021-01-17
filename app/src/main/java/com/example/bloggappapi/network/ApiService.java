package com.example.bloggappapi.network;

import com.example.bloggappapi.DeleteBody;
import com.example.bloggappapi.PostBody;
import com.example.bloggappapi.models.Post;
import com.example.bloggappapi.UserPostBody;
import com.example.bloggappapi.models.SingleUser;
import com.example.bloggappapi.models.User;
import com.example.bloggappapi.response.PostResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    // Register User with Profile Image
    @Multipart
    @POST("users/register")
    Call<User> registerAccount(
            @Part("age") RequestBody age,
            @Part MultipartBody.Part avatar,
            @Part("birthday") RequestBody birthday,
            @Part("email") RequestBody email,
            @Part("firstname") RequestBody firstname,
            @Part("lastname") RequestBody lastname,
            @Part("password") RequestBody password,
            @Part("username") RequestBody username);

    // Login User
    @POST("users/login")
    Call<User> loginUser(@Body SingleUser user);

    // Get all posts
    @POST("posts")
    Call<PostResponse> getPosts(@Body PostBody postBody);

    // Get post by User
    @POST("posts/userpost")
    Call<List<Post>> getUserPosts(@Body UserPostBody userPostBody);

    // Submit Post with Image/s
    @Multipart
    @POST("posts/submitpost")
    Call<ResponseBody> submitPost(@Part("postedBy") RequestBody userId,
                           @Part("postText") RequestBody postText,
                           @Part List<MultipartBody.Part> postImage);

    // Delete Post
    @POST("posts/deletepost")
    Call<ResponseBody> deletePost(@Body DeleteBody deleteBody);

    // Post Comment with Image/s
    @POST("comments/submitcomment")
    @Multipart
    Call<Post> submitComment(@Part("commentedBy") RequestBody userId,
                             @Part("postId") RequestBody postId,
                             @Part("commentText") RequestBody comment,
                             @Part List<MultipartBody.Part> commentImage);

    // Post Comment
    @POST("comments/submitcomment")
    @Multipart
    Call<Post> submitComment(@Part("commentedBy") RequestBody userId,
                             @Part("postId") RequestBody postId,
                             @Part("commentText") RequestBody comment);
}
