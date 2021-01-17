package com.example.bloggappapi.utilities;

import com.example.bloggappapi.models.Comment;
import com.example.bloggappapi.models.Image;
import com.example.bloggappapi.models.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;

public class EntityConverter {

    @TypeConverter
    public static Image fromString(String data) {
        Type type = new TypeToken<Image>(){}.getType();
        return new Gson().fromJson(data, type);
    }

    @TypeConverter
    public static String fromImage(Image data) {
        return new Gson().toJson(data);
    }

    @TypeConverter
    public static List<Image> fromStringValue(String data) {
        Type type = new TypeToken< List<Image>>(){}.getType();
        return new Gson().fromJson(data, type);
    }

    @TypeConverter
    public static String fromListImageValue(List<Image> data) {
        return new Gson().toJson(data);
    }

    @TypeConverter
    public static User fromStringToUser(String data) {
        Type type = new TypeToken<User>(){}.getType();
        return new Gson().fromJson(data, type);
    }

    @TypeConverter
    public static String fromUserValue(User data) {
        return new Gson().toJson(data);
    }

    @TypeConverter
    public static List<Comment> fromStringToComment(String data) {
        Type type = new TypeToken<List<Comment>>(){}.getType();
        return new Gson().fromJson(data, type);
    }

    @TypeConverter
    public static String fromListCommentValue(List<Comment> data) {
        return new Gson().toJson(data);
    }
}
