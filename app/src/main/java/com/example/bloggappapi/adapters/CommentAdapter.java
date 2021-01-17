package com.example.bloggappapi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bloggappapi.R;
import com.example.bloggappapi.models.Comment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private LayoutInflater layoutInflater;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_container_comments, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        String imgUrl = commentList.get(position).getCommentedBy().getAvatar().getUrl();
        if (imgUrl != null) {
            if (!imgUrl.isEmpty()) {
                holder.setProfile(imgUrl);
            }
        }
        holder.txtUsername.setText(commentList.get(position).getCommentedBy().getUsername());
        String comment = commentList.get(position).getCommentText();
        String commentDisplay = comment.substring(0, 1).toUpperCase() + comment.substring(1);
        holder.txtComment.setText(commentDisplay);


    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProfile;
        private TextView txtUsername, txtComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtComment = itemView.findViewById(R.id.txtComment);
        }

        protected void setProfile(String url) {
            Glide.with(imgProfile.getContext()).
                    load(url)
                    .circleCrop()
                    .error(R.drawable.ic_empty_profile)
                    .into(imgProfile);
        }
    }
}
