package com.example.bloggappapi.viewmodels;

import android.app.Application;

import com.example.bloggappapi.models.Post;
import com.example.bloggappapi.repositories.PostRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import okhttp3.RequestBody;

public class CommentActivityViewModel extends AndroidViewModel {

    private PostRepository postRepository;

    public CommentActivityViewModel(@NonNull Application application) {
        super(application);
        postRepository = new PostRepository(application);
    }

    public LiveData<Post> sendComment(RequestBody id, RequestBody postId, RequestBody commentText) {
        return postRepository.sendComment(id, postId, commentText);
    }
}
