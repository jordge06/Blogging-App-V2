package com.example.bloggappapi.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bloggappapi.DeleteBody;
import com.example.bloggappapi.PostBody;
import com.example.bloggappapi.models.Post;
import com.example.bloggappapi.R;
import com.example.bloggappapi.adapters.PostAdapter;
import com.example.bloggappapi.models.Comment;
import com.example.bloggappapi.models.User;
import com.example.bloggappapi.viewmodels.MainActivityViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PostAdapter.ClickListener {

    // vars
    private static final String TAG = "MainActivity";
    private String userId;
    private String profileUrl;
    private User user;
    private List<Post> postList = new ArrayList<>();
    private ConstraintLayout layout;
    private int pageNumber = 0;

    // instance
    private PostAdapter postAdapter;
    private MainActivityViewModel mainActivityViewModel;

    private RecyclerView rvPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mainActivityViewModel = new ViewModelProvider(MainActivity.this).get(MainActivityViewModel.class);
        layout = findViewById(R.id.layout);

        // Get User from Local Database
        getUser();
        setRecycler();

        // Fetch data from Network through API
        fetchDataFromApi();

        // Check if Local Database has no rows/data
        // NOTE Not really good implementation
        // Will be replace soon
        getRowCount();

        // Display a SnackBar Message once a post was deleted
        // This uses an observable
        deleteMessage();

        try {
            // Check from ViewModel if network is available
            networkStatus();
        } catch (Exception e) {
            Log.d(TAG, "onCreate: " + e.getMessage());
        }

        // Check for timeout exception, then fetch data again
        checkTimeout();

        rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!rvPosts.canScrollVertically(1)) {
                    if (pageNumber <= 1) {
                        pageNumber++;
                        fetchDataFromApi();
                    }
                }
            }
        });

    }

    private void checkTimeout() {
        mainActivityViewModel.timeout().observe(this, isTimeout -> {
            if (isTimeout != null) {
                if (isTimeout) fetchDataFromApi();
            }
        });
    }


    private void getRowCount() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mainActivityViewModel.getRowCount()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rowCount -> {
                    if (rowCount == 0) {
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                        Log.d(TAG, "getRowCount: No User Login, Local Database is Empty");
                    }
                    compositeDisposable.dispose();
                })
        );
    }

    private void networkStatus() {
        mainActivityViewModel.getNetworkStatus().observe(this, noConnection -> {
            if (noConnection != null) {
                // If the're is no connection
                // We want to fetch data from our Local Database
                if (noConnection) {
                    fetchDataFromCache();
                    snackBarMaker("Can't Update Data, Please Connect to a Network",
                            Snackbar.LENGTH_INDEFINITE,
                            true);
                }
            }
        });
    }

    private void snackBarMaker(String message, int length, boolean withAction) {
        Snackbar snackbar = Snackbar.make(layout, message, length);
        if (withAction) {
            snackbar.setAction("Retry", view -> {
                fetchDataFromApi();
                Toast.makeText(this, "Retrying", Toast.LENGTH_SHORT).show();
            });
        }
        snackbar.show();
    }

    private void getUser() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mainActivityViewModel.getUser()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userData -> {
                    if (userData != null) {
                        user = userData;
                        userId = userData.getUserId();
                        profileUrl = userData.getAvatar().getUrl();
                        postAdapter.setCurrentUserProfile(profileUrl);
                    } else {
                        Log.d(TAG, "getUser: Empty Userdata");
                    }
                    compositeDisposable.dispose();
                })
        );
    }

    private void setRecycler() {
        rvPosts = findViewById(R.id.rvPosts);
        postAdapter = new PostAdapter(postList, profileUrl);
        rvPosts.setHasFixedSize(true);
        rvPosts.setAdapter(postAdapter);
        postAdapter.setClickListener(this);
    }

    private void fetchDataFromApi() {
        PostBody postBody = new PostBody();
        postBody.setPage(pageNumber);
        mainActivityViewModel.getPosts(postBody).observe(this, postResponses -> {
            if (postResponses != null) {
                if (postResponses.getPostList().size() > 0) {
                    int oldSize = postList.size();
                    postList.addAll(postResponses.getPostList());
                    postAdapter.notifyItemRangeInserted(oldSize, postAdapter.getItemCount());

                }
                deleteAllRecords();
                cachePosts(postList);
            }
        });
    }

    private void fetchDataFromCache() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                mainActivityViewModel.getAllPost()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(responseList -> {
                            if (postList.size() > 0) {
                                postList.clear();
                                postAdapter.notifyDataSetChanged();
                            }
                            postList.addAll(responseList);
                            postAdapter.notifyDataSetChanged();
                            compositeDisposable.dispose();
                        })
        );
    }

    private void deleteAllRecords() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(
                mainActivityViewModel.deleteAllRecords()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(compositeDisposable::dispose)
        );
    }

    private void cachePosts(List<Post> postResponses) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mainActivityViewModel.savePost(postResponses)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d(TAG, "cachePosts: Post Save to Database");
                    compositeDisposable.dispose();
                })
        );
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem profileItem = menu.findItem(R.id.action_profile);
        if (profileUrl != null) {
            if (!profileUrl.isEmpty()) {
                Glide.with(this)
                        .asBitmap()
                        .load(profileUrl)
                        .circleCrop()
                        .error(R.drawable.ic_empty_profile)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                profileItem.setIcon(new BitmapDrawable(getResources(), resource));
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            logout();
            return true;
        } else if (item.getItemId() == R.id.action_new_post) {
            Intent intent = new Intent(this, NewPostActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public void openCommentSection(int pos) {
        Post post = postAdapter.getPostByPosition(pos);
        try {
            ArrayList<Comment> commentList = new ArrayList<>(post.getComment());
            Intent intent = new Intent(this, CommentActivity.class);
            intent.putParcelableArrayListExtra("commentList", commentList);
            intent.putExtra("userId", post.getPostedBy().getUserId());
            intent.putExtra("postId", post.getId());
            startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "openCommentSection: " + e.getMessage());
        }

    }

    @Override
    public void onProfileClick(int pos) {
        User user = postAdapter.getPostByPosition(pos).getPostedBy();
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    @Override
    public void onCommentSend(final String comment, final EditText editText, final int pos) {
        final Post post = postAdapter.getPostByPosition(pos);
        final RequestBody id = RequestBody.create(userId, MediaType.parse("multipart/form-data"));
        final RequestBody postId = RequestBody.create(post.getId(), MediaType.parse("multipart/form-data"));
        final RequestBody commentText = RequestBody.create(comment, MediaType.parse("multipart/form-data"));

        if (comment.isEmpty()) {
            snackBarMaker("Add a Comment", Snackbar.LENGTH_SHORT, false);
            return;
        }
        try {
            mainActivityViewModel.sendComment(id, postId, commentText).observe(this, postResponse -> {
                if (postResponse != null) {
                    snackBarMaker("Commend Added", Snackbar.LENGTH_SHORT, false);
                    editText.setText("");
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "onCommentSend: " + e.getMessage());
        }

    }

    private void logout() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(mainActivityViewModel.deleteUser(user)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d(TAG, "logout: User Deleted");
                    compositeDisposable.dispose();
                })
        );

        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void deleteMessage() {
        mainActivityViewModel.isPostDeleted().observe(this, isDeleted -> {
            if (isDeleted != null) {
                if (isDeleted) {
                    Snackbar.make(layout, "Post Deleted", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onOptionClick(int pos) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetTheme);
        Post post = postAdapter.getPostByPosition(pos);
        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.bottom_sheet_layout,
                findViewById(R.id.bottomSheet));
        sheetView.findViewById(R.id.actionUpdate).setOnClickListener(view -> {
            ArrayList<String> imageList = new ArrayList<>();
            for (int i = 0; i < post.getPostImage().size(); i++) {
                imageList.add(post.getPostImage().get(i).getUrl());
            }
            Intent intent = new Intent(this, NewPostActivity.class);
            intent.putStringArrayListExtra("imageList", imageList);
            intent.putExtra("description", post.getPostText());
            startActivity(intent);
            bottomSheetDialog.dismiss();
        });

        sheetView.findViewById(R.id.actionDelete).setOnClickListener(view -> {
            DeleteBody deleteBody = new DeleteBody(post.getId());
            mainActivityViewModel.deletePost(deleteBody);
            postList.remove(pos);
            postAdapter.notifyItemRemoved(pos);
            postAdapter.notifyItemRangeChanged(pos, postAdapter.getItemCount());
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    @Override
    public void removeItemWithNoUser(int pos) {
        //postList.remove(pos);
//        postAdapter.notifyItemRemoved(pos);
//        postAdapter.notifyItemRangeChanged(pos, postAdapter.getItemCount());
    }
}