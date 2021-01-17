package com.example.bloggappapi.dao;

import com.example.bloggappapi.models.Post;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable savePost(List<Post> postList);

    @Query("SELECT * FROM post_table")
    Flowable<List<Post>> getAllPost();

    @Query("DELETE FROM post_table")
    Completable deleteAllRecords();

}
