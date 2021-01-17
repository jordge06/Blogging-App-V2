package com.example.bloggappapi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.bloggappapi.R;
import com.example.bloggappapi.models.SingleUser;
import com.example.bloggappapi.models.User;
import com.example.bloggappapi.viewmodels.UserViewModel;
import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText txtEmail, txtPassword;
    private UserViewModel userViewModel;
    private ConstraintLayout parentLayout;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        parentLayout = findViewById(R.id.parentLayout);
        btnLogin = findViewById(R.id.btnLogin);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        btnLogin.setOnClickListener(view -> loginUser());

        findViewById(R.id.btnOpenRegister).setOnClickListener(view -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void loginUser() {
        String email = txtEmail.getText().toString();
        String pass = txtPassword.getText().toString();
        if (email.isEmpty() || pass.isEmpty()) {
            Log.d(TAG, "loginUser: Empty Fields");
            Snackbar.make(parentLayout, "Empty Field", Snackbar.LENGTH_SHORT)
                    .setAnchorView(btnLogin)
                    .show();
            return;
        }

        userViewModel.loginUser(new SingleUser(email, pass)).observe(this, user -> {
            if (user != null) {
                Log.d(TAG, "loginUser: Login Successful");
                saveUserToDatabase(user);
                startActivity(new Intent(this, MainActivity.class));
            } else Snackbar.make(btnLogin, "No User Found", Snackbar.LENGTH_SHORT).show();
        });
    }

    private void saveUserToDatabase(User user) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(userViewModel.saveUser(user)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Log.d(TAG, "saveUserToDatabase: User Save");
                    compositeDisposable.dispose();
                })
        );
    }
}