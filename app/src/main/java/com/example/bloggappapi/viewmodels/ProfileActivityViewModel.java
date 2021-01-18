package com.example.bloggappapi.viewmodels;

import android.app.Application;
import android.util.Log;

import com.example.bloggappapi.models.Post;
import com.example.bloggappapi.request.UserPostBody;
import com.example.bloggappapi.dao.UserDao;
import com.example.bloggappapi.database.UserDatabase;
import com.example.bloggappapi.models.User;
import com.example.bloggappapi.repositories.PostRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import io.reactivex.Flowable;

public class ProfileActivityViewModel extends AndroidViewModel {

    private static final String TAG = "ProfileActivityViewMode";
    private UserDao userDao;
    private PostRepository postRepository;


    public ProfileActivityViewModel(@NonNull Application application) {
        super(application);

        userDao = UserDatabase.getInstance(application).userDao();
        postRepository = new PostRepository(application);

    }

    public Flowable<User> getUser() {
        return userDao.getUser();
    }

    public LiveData<List<Post>> getPostByUser(UserPostBody userPostBody) {
        try {
            Log.d(TAG, "getPostByUser: Successful");
            return postRepository.getPostByUser(userPostBody);
        } catch (Exception e) {
            Log.d(TAG, "getPostByUser: " + e.getMessage());
            return null;
        }
    }


}
