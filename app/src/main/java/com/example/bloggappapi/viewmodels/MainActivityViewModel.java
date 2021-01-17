package com.example.bloggappapi.viewmodels;

import android.app.Application;
import android.util.Log;

import com.example.bloggappapi.DeleteBody;
import com.example.bloggappapi.PostBody;
import com.example.bloggappapi.UserPostBody;
import com.example.bloggappapi.models.Post;
import com.example.bloggappapi.dao.PostDao;
import com.example.bloggappapi.dao.UserDao;
import com.example.bloggappapi.database.UserDatabase;
import com.example.bloggappapi.models.User;
import com.example.bloggappapi.repositories.PostRepository;
import com.example.bloggappapi.response.PostResponse;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import okhttp3.RequestBody;

public class MainActivityViewModel extends AndroidViewModel {

    private static final String TAG = "MainActivityViewModel";
    private PostDao postDao;
    private UserDao userDao;
    private PostRepository postRepository;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        postDao = UserDatabase.getInstance(application).postDao();
        userDao = UserDatabase.getInstance(application).userDao();
        postRepository = new PostRepository(application);
    }

    public LiveData<PostResponse> getPosts(PostBody postBody) {
        try {
            return postRepository.getPostList(postBody);
        } catch (Exception e) {
            Log.d(TAG, "getPosts: " + e.getMessage());
            return null;
        }
    }

    public LiveData<Boolean> getNetworkStatus() {
        return postRepository.getNetworkStatus();
    }

    public LiveData<Boolean> timeout() {
        return postRepository.isTimeout();
    }

    public LiveData<Post> sendComment(RequestBody id, RequestBody postId, RequestBody commentText) {
        return postRepository.sendComment(id, postId, commentText);
    }

    public Completable savePost(List<Post> postList) {
        return postDao.savePost(postList);
    }

    public Completable deleteUser(User user) {
        return userDao.deleteUser(user);
    }

    public Completable deleteAllRecords() {
        return postDao.deleteAllRecords();
    }

    public Flowable<User> getUser() {
        return userDao.getUser();
    }

    public Flowable<Integer> getRowCount() {
        return userDao.getRowCount();
    }

    public Flowable<List<Post>> getAllPost() {
        return postDao.getAllPost();
    }

    public void deletePost(DeleteBody deleteBody) {
        postRepository.deletePost(deleteBody);
    }

    public LiveData<Boolean> isPostDeleted() {
        return postRepository.isPostDeleted();
    }
}
