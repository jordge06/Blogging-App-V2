package com.example.bloggappapi.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloggappapi.R;
import com.example.bloggappapi.adapters.CommentAdapter;
import com.example.bloggappapi.models.Comment;
import com.example.bloggappapi.models.Image;
import com.example.bloggappapi.models.Post;
import com.example.bloggappapi.request.DeleteCommentBody;
import com.example.bloggappapi.viewmodels.CommentActivityViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements CommentAdapter.OnCommentItemClick {

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
        commentAdapter.setOnCommentItemClick(this);
        rvComments.setHasFixedSize(true);
        rvComments.setAdapter(commentAdapter);

    }

    @Override
    public void onOptionClick(int pos, Button btnSave, Button btnCancel, EditText txtEditComment, TextView txtComment1) {
        Comment comment = commentAdapter.getCommentByPos(pos);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetTheme);
        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.bottom_sheet_layout,
                findViewById(R.id.bottomSheet));

        TextView actionUpdate = sheetView.findViewById(R.id.actionUpdate);
        actionUpdate.setText(R.string.update_comment);
        TextView actionDelete = sheetView.findViewById(R.id.actionDelete);
        actionDelete.setText(R.string.delete_comment);

        actionDelete.setOnClickListener(view -> {
            DeleteCommentBody deleteCommentBody = new DeleteCommentBody();
            deleteCommentBody.setId(comment.getId());
            commentActivityViewModel.isCommentDeleted(deleteCommentBody).observe(this, isDeleted -> {
                if (isDeleted) {
                    Toast.makeText(this, "Comment Deleted", Toast.LENGTH_SHORT).show();
                    commentList.remove(pos);
                    commentAdapter.notifyItemRemoved(pos);
                    commentAdapter.notifyItemRangeChanged(pos, commentAdapter.getItemCount());
                } else {
                    Log.d(TAG, "onOptionClick: Failed to Delete Comment");
                }
            });
            bottomSheetDialog.dismiss();
        });

        actionUpdate.setOnClickListener(view -> {
            txtComment1.setVisibility(View.GONE);
            txtEditComment.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            txtEditComment.setText(txtComment1.getText());
            txtEditComment.requestFocus();

            btnSave.setOnClickListener(view1 -> {
                String commentText = txtEditComment.getText().toString();
                if (comment.getId() == null) {
                    Log.d(TAG, "onOptionClick: Empty Id");
                    Toast.makeText(this, "Empty Id", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (commentText.isEmpty()) {
                    Toast.makeText(this, "Add A Comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                RequestBody text = RequestBody.create(commentText, MediaType.parse("multipart/form-data"));
                RequestBody id = RequestBody.create(comment.getId(), MediaType.parse("multipart/form-data"));
                commentActivityViewModel.isCommentUpdated(text, id).observe(this, isUpdated -> {
                    if (isUpdated) {
                        txtComment1.setVisibility(View.VISIBLE);
                        txtComment1.setText(commentText);
                        txtEditComment.setVisibility(View.GONE);
                        btnSave.setVisibility(View.GONE);
                        btnCancel.setVisibility(View.GONE);
                        commentAdapter.notifyDataSetChanged();
                        Toast.makeText(this, "Comment Updated!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Comment Not Updated", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            btnCancel.setOnClickListener(view2 -> {
                txtComment1.setVisibility(View.VISIBLE);
                txtEditComment.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
            });

            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();

    }
}