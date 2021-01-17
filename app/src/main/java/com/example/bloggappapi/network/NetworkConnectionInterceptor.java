package com.example.bloggappapi.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkConnectionInterceptor implements Interceptor {

    private Context context;

    public NetworkConnectionInterceptor(Context context) {
        this.context = context;
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        if (!isConnected()) {
            throw new NoConnectivityException();
        }

        Request.Builder builder = chain.request().newBuilder();

        return chain.proceed(builder.build());
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

//    private Boolean hasInternet() {
//        try {
//            int timeOutMs = 1500;
//            Socket socket = new Socket();
//            SocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);
//            socket.connect(socketAddress, timeOutMs);
//            socket.close();
//            return true;
//        } catch (IOException e) {
//            return false;
//        }
//    }
}
