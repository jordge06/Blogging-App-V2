package com.example.bloggappapi.utilities;

public class Account {

    private String userId;
    private static volatile Account instance;

    private Account(){}
    public static Account getInstance() {
        if (instance == null) {
            instance = new Account();
        }
        return instance;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
