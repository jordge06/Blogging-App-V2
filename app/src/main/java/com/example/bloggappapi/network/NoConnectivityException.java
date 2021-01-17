package com.example.bloggappapi.network;

import java.io.IOException;

import androidx.annotation.Nullable;

public class NoConnectivityException extends IOException {

    @Nullable
    @Override
    public String getMessage() {
        return "No Internet Connection";
    }
}
