package com.example.bloggappapi.repositories;

import android.util.Log;

import com.example.bloggappapi.models.SingleUser;
import com.example.bloggappapi.models.User;
import com.example.bloggappapi.models.UserRegister;
import com.example.bloggappapi.network.ApiService;
import com.example.bloggappapi.network.RetrofitInstance;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Part;

public class UserRepository {

    private static final String TAG = "UserRepository";
    private ApiService apiService;

    public UserRepository() {
        apiService = RetrofitInstance.getRetrofit().create(ApiService.class);
    }

    public LiveData<User> loginUser(SingleUser singleUser) {
        MutableLiveData<User> data = new MutableLiveData<>();
        apiService.loginUser(singleUser).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: " + response.errorBody() + "Error Code: " + response.code());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
        return data;
    }

    public LiveData<User> registerUser(RequestBody age, MultipartBody.Part avatar, RequestBody birthday, RequestBody email,
                                       RequestBody firstname, RequestBody lastname, RequestBody password, RequestBody username) {
        MutableLiveData<User> data = new MutableLiveData<>();
        apiService.registerAccount(age, avatar, birthday, email, firstname, lastname,
                password, username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    Log.d(TAG, "onResponse: " + response.errorBody());
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                data.setValue(null);
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });
        return data;
    }
}
