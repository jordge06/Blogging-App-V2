package com.example.bloggappapi.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bloggappapi.R;
import com.example.bloggappapi.models.Response;
import com.example.bloggappapi.utilities.FileHelper;
import com.example.bloggappapi.viewmodels.UserViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class RegisterActivity extends AppCompatActivity {

    // widgets
    private EditText txtUsername, txtEmail, txtPassword, txtFirstName, txtLastName, txtBirthday, txtAge;
    private ImageView imgProfile, imgAdd;

    // instance
    private UserViewModel userViewModel;
    private FileHelper fileHelper;

    // vars
    private Uri profileImg = null;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtBirthday = findViewById(R.id.txtBirthday);
        txtEmail = findViewById(R.id.txtEmail);
        txtAge = findViewById(R.id.txtAge);
        imgProfile = findViewById(R.id.imgProfile);
        imgAdd = findViewById(R.id.imgAdd);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        fileHelper = new FileHelper(this);

        imgProfile.setOnClickListener(view -> permission());
        imgAdd.setOnClickListener(view -> permission());
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view -> {
            String username = txtUsername.getText().toString();
            String fName = txtFirstName.getText().toString();
            String password = txtPassword.getText().toString();
            String lName = txtLastName.getText().toString();
            String email = txtEmail.getText().toString();
            String birthday = txtBirthday.getText().toString();
            String age = txtAge.getText().toString();
            if (username.isEmpty() || fName.isEmpty() || password.isEmpty() || lName.isEmpty() ||
                    email.isEmpty() || birthday.isEmpty() || age.isEmpty()) {
                Snackbar.make(btnRegister, "Empty Field", Snackbar.LENGTH_SHORT)
                        .show();
                return;
            }
            Response response = new Response(age, birthday, email, fName, lName, password, username);
            registerUser(response);
        });
    }

    private void permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(RegisterActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            } else {
                selectProfileImage();
            }
        } else {
            selectProfileImage();
        }
    }

    private MultipartBody.Part getAvatar() {
        if (profileImg != null) {
            File file = new File(fileHelper.getRealPathFromURI(profileImg));
            //RequestBody requestBody = RequestBody.create(file, MediaType.parse("multipart/form-data"));
            try {
                RequestBody requestBody = RequestBody.create(file, MediaType.parse(getContentResolver().getType(profileImg)));
                return MultipartBody.Part.createFormData("avatar", file.getName(), requestBody);
            } catch (Exception e) {
                Log.d(TAG, "getAvatar: " + e.getMessage());
            }

        }
        return null;
    }

    private void selectProfileImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }

    private void registerUser(Response response) {
        RequestBody partAge = RequestBody.create(response.getAge(), MediaType.parse("multipart/form-data"));
        RequestBody partBirthday = RequestBody.create(response.getBirthday(), MediaType.parse("multipart/form-data"));
        RequestBody partEmail = RequestBody.create(response.getEmail(), MediaType.parse("multipart/form-data"));
        RequestBody partFirstName = RequestBody.create(response.getFirstName(), MediaType.parse("multipart/form-data"));
        RequestBody partLastName = RequestBody.create(response.getLastName(), MediaType.parse("multipart/form-data"));
        RequestBody partPassword = RequestBody.create(response.getPassword(), MediaType.parse("multipart/form-data"));
        RequestBody partUsername = RequestBody.create(response.getUsername(), MediaType.parse("multipart/form-data"));

        userViewModel.registerUser(partAge,
                getAvatar(),
                partBirthday,
                partEmail,
                partFirstName,
                partLastName,
                partPassword,
                partUsername).observe(this, user -> {
            if (user != null) {
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                Log.d(TAG, "registerUser: Shit Happens, Registration Failed");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            profileImg = data.getData();
            //imgProfile.setImageURI(Uri.parse(fileHelper.getRealPathFromURI(data.getData())));
            Glide.with(RegisterActivity.this)
                    .load(fileHelper.getRealPathFromURI(data.getData()))
                    .error(R.drawable.ic_empty_profile)
                    .circleCrop()
                    .into(imgProfile);
            imgAdd.setVisibility(View.INVISIBLE);
        }
    }
}