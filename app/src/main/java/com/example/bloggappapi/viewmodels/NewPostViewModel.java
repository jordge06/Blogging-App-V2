package com.example.bloggappapi.viewmodels;

import android.app.Application;

import com.example.bloggappapi.models.Response;
import com.example.bloggappapi.repositories.PostRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class NewPostViewModel extends AndroidViewModel {

    private PostRepository postRepository;

    public NewPostViewModel(@NonNull Application application) {
        super(application);
        postRepository = new PostRepository(application);
    }

    public LiveData<Boolean> addPost(RequestBody id, RequestBody postDescription, List<MultipartBody.Part> imageList) {
        if (postRepository.addPost(id, postDescription, imageList) != null)
            return postRepository.addPost(id, postDescription, imageList);
        else {
            return new MutableLiveData<>(false);
        }
    }

    public LiveData<Boolean> postUpdated(RequestBody id, RequestBody postDescription, List<MultipartBody.Part> imageList) {
        if (postRepository.isUpdated(id, postDescription, imageList) != null)
            return postRepository.isUpdated(id, postDescription, imageList);
        else {
            return new MutableLiveData<>(false);
        }
    }

}
