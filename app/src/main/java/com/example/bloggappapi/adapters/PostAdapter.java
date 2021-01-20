package com.example.bloggappapi.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bloggappapi.models.Post;
import com.example.bloggappapi.R;
import com.example.bloggappapi.models.Image;
import com.example.bloggappapi.models.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter {

    private List<Post> postList;
    private LayoutInflater layoutInflater;
    private ClickListener clickListener;
    private String currentUserProfile;
    private static final int NORMAL_VIEW = 0;
    private static final int MULTIPLE_VIEW = 1;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setCurrentUserProfile(String currentUserProfile) {
        this.currentUserProfile = currentUserProfile;
    }

    public PostAdapter(List<Post> postList, String currentUserProfile) {
        this.postList = postList;
        this.currentUserProfile = currentUserProfile;
    }

    @Override
    public int getItemViewType(int position) {
        int imageSize = postList.get(position).getPostImage().size();

        if (imageSize > 1) {
            return MULTIPLE_VIEW;
        }
        return NORMAL_VIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == NORMAL_VIEW) {
            View view = layoutInflater.inflate(R.layout.item_container_posts, parent, false);
            return new PostViewHolder(view);
        }
        return new MultipleImageViewHolder(
                layoutInflater.inflate(R.layout.item_container_post_multiple_images,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int imageSize = postList.get(position).getPostImage().size();
        User user = postList.get(position).getPostedBy();
        if (imageSize > 1) {
            MultipleImageViewHolder mHolder = (MultipleImageViewHolder) holder;
            if (user != null) {
                try {
                    mHolder.setPostImage(postList.get(position).getPostImage());
                    // Set Profile Image of the User who posted the post
                    String profileUrl = postList.get(position).getPostedBy().getAvatar().getUrl();
                    mHolder.setProfileImage(profileUrl);
                    // Set the username of the User who posted the post
                    mHolder.txtUsername.setText(postList.get(position).getPostedBy().getUsername());
                    // Set the post caption
                    String text = postList.get(position).getPostText() + "";
                    mHolder.txtPostDescription.setText(text);
                } catch (Exception e) {
                    Log.d("postAdapter", "onBindViewHolder: " + e.getMessage());
                }
            } //else //mHolder.layout.setVisibility(View.GONE);
        } else {
            PostViewHolder mHolder = (PostViewHolder) holder;
            if (user != null) {
                mHolder.layout.setVisibility(View.VISIBLE);
                try {
                    // Check first if posts has image url/s
                    if (imageSize > 0) {
                        mHolder.imgPost.setVisibility(View.VISIBLE);
                        mHolder.setPostImage(postList.get(position).getPostImage().get(0).getUrl());
                    } else {
                        mHolder.imgPost.setVisibility(View.GONE);
                    }
                    // Set Profile Image of the User who posted the post
                    String profileUrl = postList.get(position).getPostedBy().getAvatar().getUrl();
                    mHolder.setProfileImage(profileUrl);
                    // Set the username of the User who posted the post
                    mHolder.txtUsername.setText(postList.get(position).getPostedBy().getUsername());
                    // Set the post caption
                    String text = postList.get(position).getPostText() + "";
                    mHolder.txtPostDescription.setText(text);

                } catch (Exception e) {
                    Log.d("postAdapter", "onBindViewHolder: " + e.getMessage());
                }
            } else {
                clickListener.removeItemWithNoUser(position);
            }
        }
    }

    public Post getPostByPosition(int pos) {
        return postList.get(pos);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgProfile;
        private final ImageView imgPost, btnOptions, btnSendComment;
        private final TextView txtUsername, txtPostDescription;
        private final EditText txtComment;
        private final CardView layout;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPost = itemView.findViewById(R.id.imgPost);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            ImageView imgActiveProfile = itemView.findViewById(R.id.imgActiveProfile);
            txtComment = itemView.findViewById(R.id.txtComment);
            btnOptions = itemView.findViewById(R.id.btnOptions);
            btnSendComment = itemView.findViewById(R.id.btnSendComment);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtPostDescription = itemView.findViewById(R.id.txtPostDescription);
            layout = itemView.findViewById(R.id.layout);

            btnOptions.setOnClickListener(view -> clickListener.onOptionClick(getAdapterPosition()));
            btnSendComment.setOnClickListener(view -> {
                String comment = txtComment.getText().toString();
                clickListener.onCommentSend(comment, txtComment, getAdapterPosition());
            });

            imgPost.setOnClickListener(view -> clickListener.openCommentSection(getAdapterPosition()));
            txtPostDescription.setOnClickListener(view -> clickListener.openCommentSection(getAdapterPosition()));
            imgProfile.setOnClickListener(view -> clickListener.onProfileClick(getAdapterPosition()));
            txtUsername.setOnClickListener(view -> clickListener.onProfileClick(getAdapterPosition()));

            Glide.with(imgActiveProfile.getContext())
                    .load(currentUserProfile)
                    .error(R.drawable.ic_empty_profile)
                    .circleCrop()
                    .into(imgActiveProfile);
        }

        protected void setPostImage(String url) {
            if (url != null) {
                if (!url.isEmpty()) {
                    Glide.with(imgPost.getContext()).load(url).into(imgPost);
                }
            } else imgPost.setVisibility(View.GONE);
        }

        protected void setProfileImage(String url) {
            if (url != null) {
                if (!url.isEmpty()) {
                    Glide.with(imgProfile.getContext())
                            .load(url)
                            .circleCrop()
                            .error(R.drawable.ic_empty_profile)
                            .into(imgProfile);

                } else imgProfile.setImageResource(R.drawable.ic_empty_profile);
            } else imgProfile.setImageResource(R.drawable.ic_empty_profile);

        }
    }

    class MultipleImageViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgProfile;
        private final ImageView imgPost, imgPost2, imgPost3;
        private final TextView txtUsername, txtPostDescription, txtNumberOfImages;
        private final EditText txtComment;
        private final LinearLayout horizontalSeparator;

        public MultipleImageViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPost = itemView.findViewById(R.id.imgPost);
            imgPost2 = itemView.findViewById(R.id.imgPost2);
            imgPost3 = itemView.findViewById(R.id.imgPost3);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            ImageView imgActiveProfile = itemView.findViewById(R.id.imgActiveProfile);
            txtComment = itemView.findViewById(R.id.txtComment);
            txtNumberOfImages = itemView.findViewById(R.id.txtNumberOfImages);
            ImageView btnOptions = itemView.findViewById(R.id.btnOptions);
            ImageView btnSendComment = itemView.findViewById(R.id.btnSendComment);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtPostDescription = itemView.findViewById(R.id.txtPostDescription);
            horizontalSeparator = itemView.findViewById(R.id.horizontalSeparator);

            btnOptions.setOnClickListener(view -> clickListener.onOptionClick(getAdapterPosition()));
            btnSendComment.setOnClickListener(view -> {
                String comment = txtComment.getText().toString();
                clickListener.onCommentSend(comment, txtComment, getAdapterPosition());
            });

            imgPost.setOnClickListener(view -> clickListener.openCommentSection(getAdapterPosition()));
            txtPostDescription.setOnClickListener(view -> clickListener.openCommentSection(getAdapterPosition()));
            imgProfile.setOnClickListener(view -> clickListener.onProfileClick(getAdapterPosition()));
            txtUsername.setOnClickListener(view -> clickListener.onProfileClick(getAdapterPosition()));

            Glide.with(imgActiveProfile.getContext())
                    .load(currentUserProfile)
                    .error(R.drawable.ic_empty_profile)
                    .circleCrop()
                    .into(imgActiveProfile);
        }

        protected void setPostImage(List<Image> urlList) {
            if (urlList != null) {
                if (urlList.size() < 3) {
                    Glide.with(imgPost.getContext()).load(urlList.get(0).getUrl()).into(imgPost);
                    Glide.with(imgPost.getContext()).load(urlList.get(1).getUrl()).into(imgPost2);
                    imgPost3.setVisibility(View.GONE);
                    horizontalSeparator.setVisibility(View.GONE);
                } else if (urlList.size() == 3) {
                    imgPost3.setVisibility(View.VISIBLE);
                    horizontalSeparator.setVisibility(View.VISIBLE);
                    Glide.with(imgPost.getContext()).load(urlList.get(0).getUrl()).into(imgPost);
                    Glide.with(imgPost.getContext()).load(urlList.get(1).getUrl()).into(imgPost2);
                    Glide.with(imgPost.getContext()).load(urlList.get(2).getUrl()).into(imgPost3);
                } else {
                    imgPost3.setVisibility(View.VISIBLE);
                    horizontalSeparator.setVisibility(View.VISIBLE);
                    Glide.with(imgPost.getContext()).load(urlList.get(0).getUrl()).into(imgPost);
                    Glide.with(imgPost.getContext()).load(urlList.get(1).getUrl()).into(imgPost2);
                    Glide.with(imgPost.getContext()).load(urlList.get(2).getUrl()).into(imgPost3);

                    txtNumberOfImages.setVisibility(View.VISIBLE);
                    String text = "+" + (urlList.size() - 3);
                    txtNumberOfImages.setText(text);
                }
            }
        }

        protected void setProfileImage(String url) {
            if (url != null) {
                if (!url.isEmpty()) {
                    Glide.with(imgProfile.getContext())
                            .load(url)
                            .circleCrop()
                            .error(R.drawable.ic_empty_profile)
                            .into(imgProfile);

                } else imgProfile.setImageResource(R.drawable.ic_empty_profile);
            } else imgProfile.setImageResource(R.drawable.ic_empty_profile);

        }
    }

    public interface ClickListener {
        void onOptionClick(final int pos);

        void onCommentSend(final String comment, final EditText editText, final int pos);

        void openCommentSection(final int pos);

        void onProfileClick(int pos);

        void removeItemWithNoUser(int pos);
    }
}
