package com.example.bloggappapi;

import android.app.Application;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class Server extends Application {

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("https://dcsocmed.herokuapp.com/");
        } catch (URISyntaxException e) {
            Log.d("Server", "instance initializer: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
}
