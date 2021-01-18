package com.example.bloggappapi.viewmodels;

import android.app.Application;

import com.example.bloggappapi.models.Post;
import com.example.bloggappapi.repositories.PostRepository;
import com.example.bloggappapi.request.DeleteCommentBody;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import okhttp3.MultipartBody;
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

    public LiveData<Boolean> isCommentDeleted(DeleteCommentBody deleteCommentBody) {
        if (postRepository.deleteComment(deleteCommentBody) != null) {
            return postRepository.deleteComment(deleteCommentBody);
        } return new MutableLiveData<>(false);
    }

    public LiveData<Boolean> isCommentUpdated(RequestBody text, RequestBody id) {
        if (postRepository.updateComment(text, id) != null) {
            return postRepository.updateComment(text, id);
        } return new MutableLiveData<>(false);
    }
}
