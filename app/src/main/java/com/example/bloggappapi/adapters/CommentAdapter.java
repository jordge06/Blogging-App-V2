package com.example.bloggappapi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    private OnCommentItemClick onCommentItemClick;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public void setOnCommentItemClick(OnCommentItemClick onCommentItemClick) {
        this.onCommentItemClick = onCommentItemClick;
    }

    public Comment getCommentByPos(int pos) {
        return commentList.get(pos);
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

    class CommentViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgProfile;
        private TextView txtUsername, txtComment;
        private Button btnSave, btnCancel;
        private EditText txtEditComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtComment = itemView.findViewById(R.id.txtComment);
            btnSave = itemView.findViewById(R.id.btnSave);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            txtEditComment = itemView.findViewById(R.id.txtEditComment);
            ImageView btnOptions = itemView.findViewById(R.id.btnOptions);

            btnOptions.setOnClickListener(view -> onCommentItemClick.onOptionClick(getAdapterPosition(),
                    btnSave,
                    btnCancel,
                    txtEditComment,
                    txtComment
            ));
        }

        protected void setProfile(String url) {
            Glide.with(imgProfile.getContext()).
                    load(url)
                    .circleCrop()
                    .error(R.drawable.ic_empty_profile)
                    .into(imgProfile);
        }
    }

    public interface OnCommentItemClick {
        void onOptionClick(int pos, Button btnSave, Button btnCancel, EditText txtEditComment, TextView txtComment);
    }
}
