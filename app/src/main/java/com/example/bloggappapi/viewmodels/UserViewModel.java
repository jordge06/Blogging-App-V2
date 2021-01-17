package com.example.bloggappapi.viewmodels;

import android.app.Application;
import android.util.Log;

import com.example.bloggappapi.dao.UserDao;
import com.example.bloggappapi.database.UserDatabase;
import com.example.bloggappapi.models.SingleUser;
import com.example.bloggappapi.models.User;
import com.example.bloggappapi.models.UserRegister;
import com.example.bloggappapi.repositories.UserRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserViewModel extends AndroidViewModel {

    private static final String TAG = "UserViewModel";
    private UserRepository userRepository;
    private UserDao userDao;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository();
        UserDatabase userDatabase = UserDatabase.getInstance(application);
        userDao = userDatabase.userDao();
    }

    public LiveData<User> loginUser(SingleUser singleUser) {
        return userRepository.loginUser(singleUser);
    }

    public LiveData<User> registerUser(RequestBody age, MultipartBody.Part avatar, RequestBody birthday, RequestBody email,
                                       RequestBody firstname, RequestBody lastname, RequestBody password, RequestBody username) {
        try {
            return userRepository.registerUser(age, avatar, birthday, email, firstname, lastname, password, username);
        } catch (Exception e) {
            Log.d(TAG, "registerUser: " + e.getMessage());
            return null;
        }

    }

    public Completable saveUser(User user) {
        return userDao.insertUser(user);
    }

    public Completable deleteUser(User user) {
        return userDao.deleteUser(user);
    }

    public Flowable<User> getUser() {
        return userDao.getUser();
    }

    public Flowable<Integer> getRowCount() {
        return userDao.getRowCount();
    }

}
