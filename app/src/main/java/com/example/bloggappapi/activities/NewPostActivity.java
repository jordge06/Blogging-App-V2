package com.example.bloggappapi.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bloggappapi.adapters.PostImageAdapter;
import com.example.bloggappapi.R;
import com.example.bloggappapi.models.Image;
import com.example.bloggappapi.utilities.FileHelper;
import com.example.bloggappapi.viewmodels.NewPostViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NewPostActivity extends AppCompatActivity implements PostImageAdapter.OnImageAdapterClick {

    // vars
    private List<MultipartBody.Part> imageList = new ArrayList<>();
    private List<String> stringList = new ArrayList<>();
    private List<Image> tempList = new ArrayList<>();
    private String userId;
    private static final int IMAGE_PICKER = 473;
    private String postId;

    // widgets
    private RecyclerView rvPostImage;
    private EditText txtPostDescription;

    private PostImageAdapter postImageAdapter;
    private boolean isUpdating = false;

    // instance
    private FileHelper fileHelper;
    private NewPostViewModel newPostViewModel;

    private static final String TAG = "NewPostActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        rvPostImage = findViewById(R.id.rvPostImage);
        txtPostDescription = findViewById(R.id.txtPostDescription);
        Button btnPost = findViewById(R.id.btnPost);
        userId = getIntent().getStringExtra("userId");
        fileHelper = new FileHelper(this);
        newPostViewModel = new ViewModelProvider(this).get(NewPostViewModel.class);

        getDataFromIntentExtra();
        if (postId != null) {
            isUpdating = true;
        }

        if (isUpdating) btnPost.setText(R.string.update_button);
        setRecycler();
        stringList.add(PostImageAdapter.ADD_ICON);
        postImageAdapter.notifyDataSetChanged();

        btnPost.setOnClickListener(view -> {
            if (isUpdating) {
                updatePost();
            } else {
                newPost();
            }
        });
    }

    private void getDataFromIntentExtra() {
        if (getIntent() != null) {
            try {
                tempList.addAll(getIntent().getParcelableArrayListExtra("imageList"));
            } catch (Exception e) {
                Log.d(TAG, "getDataFromIntentExtra: " + e.getMessage());
            }
            if (tempList.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(NewPostActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(NewPostActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 121);
                    } else {
                        for (Image image : tempList) {
                            stringList.add(image.getUrl());
                            downLoadImage(image);
                        }
                    }
                } else {
                    for (Image image : tempList) {
                        downLoadImage(image);
                    }
                }
            }
            postId = getIntent().getStringExtra("postId");
            txtPostDescription.setText(getIntent().getStringExtra("description"));
        }
    }

    private MultipartBody.Part getPostImage(Uri uri) {
        FileHelper fileHelper = new FileHelper(this);
        if (uri != null) {
            File file = new File(fileHelper.getRealPathFromURI(uri));
            try {
                RequestBody requestBody = RequestBody.create(file, MediaType.parse(getContentResolver().getType(uri)));
                return MultipartBody.Part.createFormData("postImage", file.getName(), requestBody);
            } catch (Exception e) {
                Log.d(TAG, "getPostImage: " + e.getMessage());
            }
        }
        return null;
    }

    private void updatePost() {
        String desc = txtPostDescription.getText().toString();
        if (desc.isEmpty()) {
            Toast.makeText(this, "Please add a caption", Toast.LENGTH_SHORT).show();
            return;
        }
        if (postId == null) {
            Toast.makeText(this, "Empty Id", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestBody postDescription = RequestBody.create(desc, MediaType.parse("multipart/form-data"));
        RequestBody id = RequestBody.create(postId, MediaType.parse("multipart/form-data"));
        try {
            newPostViewModel.postUpdated(id, postDescription, imageList).observe(this, isUpdated -> {
                if (isUpdated) {
                    Toast.makeText(this, "Post Updated", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "newPost: Post Updated Successfully");
                } else {
                    Log.d(TAG, "updatePost: Post Update Failed");
                }
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        } catch (Exception e) {
            Log.d(TAG, "updatePost: " + e.getMessage());
        }
    }

    private void downLoadImage(Image image) {
        Uri downloadUri = Uri.parse(image.getUrl());
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        File tempFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), image.getFilename());
        if (tempFile.exists()) {
            addAFileToImageList(image);
            return;
        }
        try {
            if (downloadManager != null) {
                DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                        DownloadManager.Request.NETWORK_MOBILE)
                        .setTitle(image.getFilename())
                        .setDescription("Downloading " + image.getFilename())
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, image.getFilename())
                        .setMimeType(image.getMimeType());
                downloadManager.enqueue(request);
                addAFileToImageList(image);
            } else {
                Log.d(TAG, "downLoadImage: Download Manager Error!");
            }
        } catch (Exception e) {
            Log.d(TAG, "downLoadImage: " + e.getMessage());
        }
    }

    private void addAFileToImageList(Image image) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), image.getFilename());
        try {
            RequestBody requestBody = RequestBody.create(file, MediaType.parse("multipart/form-data"));
            imageList.add(MultipartBody.Part.createFormData("postImage", file.getName(), requestBody));
        } catch (Exception e) {
            Log.d(TAG, "downLoadImage: File Error: " + e.getMessage());
        }
    }

    private void newPost() {
        String desc = txtPostDescription.getText().toString();
        if (desc.isEmpty()) {
            Toast.makeText(this, "Please add a caption", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userId == null) {
            Toast.makeText(this, "Empty Id", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody postDescription = RequestBody.create(desc, MediaType.parse("multipart/form-data"));
        RequestBody id = RequestBody.create(userId, MediaType.parse("multipart/form-data"));
        try {
            newPostViewModel.addPost(id, postDescription, imageList).observe(this, isPostAdded -> {
                if (isPostAdded) {
                    Toast.makeText(this, "Post Added", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "newPost: New Post Added Successfully");
                } else {
                    Log.d(TAG, "newPost: Something Went Wrong!");
                }
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        } catch (Exception e) {
            Log.d(TAG, "newPost: " + e.getMessage());
        }


    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), IMAGE_PICKER);
    }

    private void setRecycler() {
        postImageAdapter = new PostImageAdapter(stringList, this);
        rvPostImage.setAdapter(postImageAdapter);
        postImageAdapter.setOnImageAdapterClick(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER && resultCode == RESULT_OK && data != null) {
            imageList.add(getPostImage(data.getData()));
            stringList.add(0, fileHelper.getRealPathFromURI(data.getData()));
            postImageAdapter.notifyItemInserted(0);
            postImageAdapter.notifyItemRangeChanged(0, postImageAdapter.getItemCount());
        }
    }

    @Override
    public void onAdd(int pos) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(NewPostActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NewPostActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                selectImage();
            }
        } else {
            selectImage();
        }
    }

    @Override
    public void onRemoveClick(int pos) {
        stringList.remove(pos);
        imageList.remove(pos);
        postImageAdapter.notifyItemRemoved(pos);
        postImageAdapter.notifyItemRangeChanged(pos, postImageAdapter.getItemCount());
    }
}