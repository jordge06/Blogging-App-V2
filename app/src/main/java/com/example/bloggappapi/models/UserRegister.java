package com.example.bloggappapi.models;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UserRegister {

    private RequestBody age;
    private MultipartBody.Part avatar;
    private RequestBody birthDay;
    private RequestBody email;
    private RequestBody firstname;
    private RequestBody lastname;
    private RequestBody password;
    private RequestBody username;

    public UserRegister(RequestBody age, MultipartBody.Part avatar, RequestBody birthDay, RequestBody email,
                        RequestBody firstname, RequestBody lastname, RequestBody password, RequestBody username) {
        this.age = age;
        this.avatar = avatar;
        this.birthDay = birthDay;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.username = username;
    }

    public RequestBody getAge() {
        return age;
    }

    public void setAge(RequestBody age) {
        this.age = age;
    }

    public MultipartBody.Part getAvatar() {
        return avatar;
    }

    public void setAvatar(MultipartBody.Part avatar) {
        this.avatar = avatar;
    }

    public RequestBody getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(RequestBody birthDay) {
        this.birthDay = birthDay;
    }

    public RequestBody getEmail() {
        return email;
    }

    public void setEmail(RequestBody email) {
        this.email = email;
    }

    public RequestBody getFirstname() {
        return firstname;
    }

    public void setFirstname(RequestBody firstname) {
        this.firstname = firstname;
    }

    public RequestBody getLastname() {
        return lastname;
    }

    public void setLastname(RequestBody lastname) {
        this.lastname = lastname;
    }

    public RequestBody getPassword() {
        return password;
    }

    public void setPassword(RequestBody password) {
        this.password = password;
    }

    public RequestBody getUsername() {
        return username;
    }

    public void setUsername(RequestBody username) {
        this.username = username;
    }
}
