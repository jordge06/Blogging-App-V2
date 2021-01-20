package com.example.bloggappapi.repositories;

import android.content.Context;
import android.util.Log;

import com.example.bloggappapi.request.DeleteBody;
import com.example.bloggappapi.request.DeleteCommentBody;
import com.example.bloggappapi.request.PostBody;
import com.example.bloggappapi.models.Post;
import com.example.bloggappapi.request.UserPostBody;
import com.example.bloggappapi.network.ApiService;
import com.example.bloggappapi.network.NoConnectivityException;
import com.example.bloggappapi.network.RetrofitInstance;
import com.example.bloggappapi.response.PostResponse;

import java.net.SocketTimeoutException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRepository {

    private static final String TAG = "PostRepository";
    private ApiService apiService;
    private MutableLiveData<Boolean> networkStatus;
    private MutableLiveData<Boolean> isDeleted;
    private MutableLiveData<Boolean> timeout;

    public PostRepository(Context context) {
        apiService = RetrofitInstance.getRetrofit(context).create(ApiService.class);
        networkStatus = new MutableLiveData<>();
        isDeleted = new MutableLiveData<>();
        timeout = new MutableLiveData<>();
    }

    public LiveData<Boolean> getNetworkStatus() {
        return networkStatus;
    }

    public LiveData<PostResponse> getPostList(PostBody postBody) {
        MutableLiveData<PostResponse> data = new MutableLiveData<>();
        apiService.getPosts(postBody).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                timeout.setValue(false);
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: " + response.errorBody());
                    data.setValue(null);
                }
            }
            @Override
            public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                data.setValue(null);
                if (t instanceof NoConnectivityException) {
                    networkStatus.setValue(true);
                }

                if (t instanceof SocketTimeoutException) {
                    Log.d(TAG, "onResponse: " );
                    timeout.setValue(true);
                }
            }
        });
        return data;
    }

    public LiveData<Boolean> isTimeout() {
        return timeout;
    }

    public LiveData<Post> sendComment(RequestBody id, RequestBody postId, RequestBody commentText) {
        MutableLiveData<Post> data = new MutableLiveData<>();
        apiService.submitComment(id, postId, commentText).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(@NonNull Call<Post> call, @NonNull Response<Post> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                    Log.d(TAG, "onResponse: Comment Added");
                } else {
                    data.setValue(null);
                    Log.d(TAG, "onResponse: " + response.errorBody());
                }
            }
            @Override
            public void onFailure(@NonNull Call<Post> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    public LiveData<PostResponse> getPostByUser(UserPostBody userId) {
        MutableLiveData<PostResponse> data = new MutableLiveData<>();
        apiService.getUserPosts(userId).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostResponse> call, @NonNull Response<PostResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Success");
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                    Log.d(TAG, "onResponse: " + response.errorBody() + " Error Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PostResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
        return data;
    }

    public LiveData<Boolean> addPost(RequestBody id, RequestBody postDescription, List<MultipartBody.Part> imageList) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        apiService.submitPost(id, postDescription, imageList).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Success");
                    data.setValue(true);
                } else {
                    Log.d(TAG, "onResponse: " + response.errorBody());
                    Log.d(TAG, "onResponse: " + " Error Code: " + response.code());
                    data.setValue(false);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: Request Failed");
                Log.d(TAG, "onFailure: " + t.getMessage());
                data.setValue(false);
            }
        });
        return data;
    }

    public void deletePost(DeleteBody deleteBody) {
        apiService.deletePost(deleteBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Post Deleted");
                    isDeleted.setValue(true);
                } else {
                    Log.d(TAG, "onDelete: " + response.errorBody() + " ErrorCode: " + response.code());
                    isDeleted.setValue(false);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                isDeleted.setValue(false);
            }
        });
    }

    public LiveData<Boolean> isPostDeleted() {
        return isDeleted;
    }

    public LiveData<Boolean> deleteComment(DeleteCommentBody deleteCommentBody) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        apiService.deleteComment(deleteCommentBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Comment Deleted");
                    data.setValue(true);
                } else {
                    Log.d(TAG, "onResponse: Delete Comment Failed" + response.errorBody());
                    data.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(false);
                Log.d(TAG, "onFailure: Delete Comment Failed" + t.getMessage());
            }
        });
        return data;
    }

    public LiveData<Boolean> updateComment(RequestBody text, RequestBody id) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        apiService.updateComment(text, id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Comment Update");
                    data.setValue(true);
                } else {
                    Log.d(TAG, "onResponse: Update Comment Failed" + response.errorBody());
                    data.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(false);
                Log.d(TAG, "onFailure: Update Comment Failed" + t.getMessage());
            }
        });
        return data;
    }

    public LiveData<Boolean> isPostUpdated(RequestBody id, RequestBody postDescription, List<MultipartBody.Part> imageList) {
        MutableLiveData<Boolean> data = new MutableLiveData<>();
        apiService.updatePost(id, postDescription, imageList).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    data.setValue(true);
                } else {
                    data.setValue(false);
                    Log.d(TAG, "onResponse: Update Response Not Successful" + response.errorBody());
                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                data.setValue(false);
                Log.d(TAG, "onFailure: Update Failure" + t.getMessage());
            }
        });
        return data;
    }
}
