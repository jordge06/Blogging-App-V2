package com.example.bloggappapi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloggappapi.R;
import com.example.bloggappapi.adapters.CommentAdapter;
import com.example.bloggappapi.models.Comment;
import com.example.bloggappapi.viewmodels.CommentActivityViewModel;

import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG = "CommentActivity";
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    private CommentActivityViewModel commentActivityViewModel;
    private TextView txtComment;
    private String userId;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        commentList = getIntent().getParcelableArrayListExtra("commentList");
        userId = getIntent().getStringExtra("userId");
        postId = getIntent().getStringExtra("postId");
        txtComment = findViewById(R.id.txtComment);
        commentActivityViewModel = new ViewModelProvider(this).get(CommentActivityViewModel.class);
        setRecycler();

        findViewById(R.id.btnSendComment).setOnClickListener(view -> sendComment());
    }

    private void sendComment() {
        String comment = txtComment.getText().toString().trim();

        if (userId == null) return;

        if (comment.isEmpty()) {
            Toast.makeText(this, "Add a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        if (postId == null) return;

        final RequestBody id = RequestBody.create(userId, MediaType.parse("multipart/form-data"));
        final RequestBody postIdReq = RequestBody.create(postId, MediaType.parse("multipart/form-data"));
        final RequestBody commentText = RequestBody.create(comment, MediaType.parse("multipart/form-data"));

        try {
            commentActivityViewModel.sendComment(id, postIdReq, commentText).observe(this, postsResponse -> {
                if (postsResponse != null) {
                    Toast.makeText(this, "Comment Added", Toast.LENGTH_SHORT).show();
//                    Comment commentModel = postsResponse.getComment().get( postsResponse.getComment().size() - 1);
//                    commentList.add(commentModel);
//                    commentAdapter.notifyDataSetChanged();
                    txtComment.setText("");
//                    commentAdapter.notifyItemInserted(commentList.size() - 1);
//                    commentAdapter.notifyItemRangeChanged(commentList.size() - 1, commentAdapter.getItemCount());
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "sendComment: " + e.getMessage());
        }

    }

    private void setRecycler() {
        RecyclerView rvComments = findViewById(R.id.rvComments);
        commentAdapter = new CommentAdapter(commentList);
        rvComments.setHasFixedSize(true);
        rvComments.setAdapter(commentAdapter);
    }
}