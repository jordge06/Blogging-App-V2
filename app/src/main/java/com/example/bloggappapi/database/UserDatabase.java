package com.example.bloggappapi.database;

import android.content.Context;

import com.example.bloggappapi.models.Post;
import com.example.bloggappapi.dao.PostDao;
import com.example.bloggappapi.dao.UserDao;
import com.example.bloggappapi.models.User;
import com.example.bloggappapi.utilities.EntityConverter;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {User.class, Post.class}, version = 4, exportSchema = false)
@TypeConverters(EntityConverter.class)
public abstract class UserDatabase extends RoomDatabase {

    private static volatile UserDatabase instance;

    public static UserDatabase getInstance(Context context) {
        synchronized (UserDatabase.class) {
            if (instance == null) {
                instance = Room.databaseBuilder(
                        context,
                        UserDatabase.class, "user_database")
                        .fallbackToDestructiveMigration()
                        .build();
            }
        } return instance;
    }

    public abstract UserDao userDao();

    public abstract PostDao postDao();

}
