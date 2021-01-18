package com.example.bloggappapi.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bloggappapi.R;
import com.example.bloggappapi.request.UserPostBody;
import com.example.bloggappapi.adapters.PostImageAdapter;
import com.example.bloggappapi.models.User;
import com.example.bloggappapi.viewmodels.ProfileActivityViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private User user;
    private PostImageAdapter postImageAdapter;
    private List<String> postImages = new ArrayList<>();
    private ProfileActivityViewModel profileActivityViewModel;
    private ImageView imgProfile;
    private TextView txtName, txtUsername;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imgProfile = findViewById(R.id.imgProfile);
        txtName = findViewById(R.id.txtName);
        txtUsername = findViewById(R.id.txtUsername);

        profileActivityViewModel = new ViewModelProvider(this).get(ProfileActivityViewModel.class);

        // if the User is not login get their data via intent
        if (this.getIntent() != null) {
            user = getIntent().getParcelableExtra("user");
        }

        if (user != null) {
            fetchUserFromCache();
            setRecycler();
            getUserPost();
            setUserData();
        } else Toast.makeText(this, "Empty User", Toast.LENGTH_SHORT).show();

    }

    // Setting data to views
    private void setUserData() {
        // set User Profile Picture
        Glide.with(this)
                .load(user.getAvatar().getUrl())
                .error(R.drawable.ic_empty_profile)
                .circleCrop()
                .into(imgProfile);

        String fullName = user.getFirstName() + " " + user.getLastName();
        txtName.setText(fullName);
        txtUsername.setText(user.getUsername());
    }

    private void setRecycler() {
        RecyclerView rvPosts = findViewById(R.id.rvPosts);
        rvPosts.setHasFixedSize(true);
        postImageAdapter = new PostImageAdapter(postImages, this);
        rvPosts.setAdapter(postImageAdapter);

    }

    // if the user is login get user data from database cache
    private void fetchUserFromCache() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(profileActivityViewModel.getUser()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userData -> {
                    if (userData != null) user = userData;
                    compositeDisposable.dispose();
                })
        );
    }

    // get users post by their user id
    private void getUserPost() {
        try {
            UserPostBody userPostBody = new UserPostBody(user.getUserId());
            profileActivityViewModel.getPostByUser(userPostBody).observe(this, postsResponseList -> {
                if (postsResponseList != null) {
                    for (int i = 0; i < postsResponseList.size(); i++) {
                        if (postsResponseList.get(i).getPostImage().size() > 0) {
                            postImages.add(postsResponseList.get(i).getPostImage().get(0).getUrl());
                            postImageAdapter.notifyDataSetChanged();
                        }
                    }
                } else Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            Log.d(TAG, "getUserPost: " + e.getMessage());
        }

    }


}
