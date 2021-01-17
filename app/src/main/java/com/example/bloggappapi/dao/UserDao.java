package com.example.bloggappapi.dao;

import com.example.bloggappapi.models.Image;
import com.example.bloggappapi.models.User;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertUser(User user);

    @Delete
    Completable deleteUser(User user);

    @Query("SELECT avatar from user_table LIMIT 1")
    Flowable<Image> getUserAvatar();

    @Query("SELECT * From user_table ORDER BY userId LIMIT 1")
    Flowable<User> getUser();

    @Query("SELECT * From user_table ORDER BY userId LIMIT 1")
    Flowable<User> getAllUser();

    @Query("SELECT COUNT(userId) FROM user_table")
    Flowable<Integer> getRowCount();

}
