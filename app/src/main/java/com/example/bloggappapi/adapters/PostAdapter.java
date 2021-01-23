package com.example.bloggappapi.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.bloggappapi.databinding.ItemContainerPostMultipleImagesBinding;
import com.example.bloggappapi.databinding.ItemContainerPostsBinding;
import com.example.bloggappapi.models.Post;
import com.example.bloggappapi.R;
import com.example.bloggappapi.models.Image;
import com.example.bloggappapi.models.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter {

    private final List<Post> postList;
    private LayoutInflater layoutInflater;
    private ClickListener clickListener;
    private String currentUserProfile;
    private static final int NORMAL_VIEW = 0;
    private static final int MULTIPLE_VIEW = 1;

    private boolean isLoading;

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setCurrentUserProfile(String currentUserProfile) {
        this.currentUserProfile = currentUserProfile;
    }

    public PostAdapter(List<Post> postList) {
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
            ItemContainerPostsBinding itemContainerPostsBinding = DataBindingUtil
                    .inflate(layoutInflater,
                            R.layout.item_container_posts,
                            parent,
                            false);
            return new PostViewHolder(itemContainerPostsBinding);
        }
        return new MultipleImageViewHolder(DataBindingUtil.inflate(
                layoutInflater,
                R.layout.item_container_post_multiple_images,
                parent,
                false
        ));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int imageSize = postList.get(position).getPostImage().size();
        User user = postList.get(position).getPostedBy();
        if (imageSize > 1) {
            if (user != null) {
                try {
                    ((MultipleImageViewHolder) holder).bindData(postList.get(position), currentUserProfile);
                    ((MultipleImageViewHolder) holder).setPostImage(postList.get(position).getPostImage());
                } catch (Exception e) {
                    Log.d("postAdapter", "onBindViewHolder: " + e.getMessage());
                }
            }
        } else {
            if (user != null) {
                try {
                    ((PostViewHolder) holder).binData(postList.get(position), currentUserProfile, isLoading);
                } catch (Exception e) {
                    Log.d("postAdapter", "onBindViewHolder: " + e.getMessage());
                }
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

        private final ItemContainerPostsBinding itemContainerPostsBinding;

        public PostViewHolder(@NonNull ItemContainerPostsBinding itemContainerPostsBinding) {
            super(itemContainerPostsBinding.getRoot());
            this.itemContainerPostsBinding = itemContainerPostsBinding;

            itemContainerPostsBinding.btnOptions.setOnClickListener(view -> clickListener.onOptionClick(getAdapterPosition()));
            itemContainerPostsBinding.btnSendComment.setOnClickListener(view -> {
                String comment = itemContainerPostsBinding.txtComment.getText().toString();
                clickListener.onCommentSend(comment, itemContainerPostsBinding.txtComment, getAdapterPosition());
            });

            itemContainerPostsBinding.imgPost.setOnClickListener(view -> clickListener.openCommentSection(getAdapterPosition()));
            itemContainerPostsBinding.txtPostDescription.setOnClickListener(view -> clickListener.openCommentSection(getAdapterPosition()));
            itemContainerPostsBinding.imgProfile.setOnClickListener(view -> clickListener.onProfileClick(getAdapterPosition()));
            itemContainerPostsBinding.txtUsername.setOnClickListener(view -> clickListener.onProfileClick(getAdapterPosition()));
        }

        protected void binData(Post post, String string, boolean loading) {
            itemContainerPostsBinding.setPost(post);
            itemContainerPostsBinding.setIsLoading(loading);
            itemContainerPostsBinding.setCurrentUser(string);
            itemContainerPostsBinding.executePendingBindings();
        }

    }

    class MultipleImageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerPostMultipleImagesBinding itemContainerPostMultipleImagesBinding;

        public MultipleImageViewHolder(@NonNull ItemContainerPostMultipleImagesBinding itemContainerPostMultipleImagesBinding) {
            super(itemContainerPostMultipleImagesBinding.getRoot());
            this.itemContainerPostMultipleImagesBinding = itemContainerPostMultipleImagesBinding;

            itemContainerPostMultipleImagesBinding.btnOptions.setOnClickListener(view -> clickListener.onOptionClick(getAdapterPosition()));
            itemContainerPostMultipleImagesBinding.btnSendComment.setOnClickListener(view -> {
                String comment = itemContainerPostMultipleImagesBinding.txtComment.getText().toString();
                clickListener.onCommentSend(comment, itemContainerPostMultipleImagesBinding.txtComment, getAdapterPosition());
            });

            itemContainerPostMultipleImagesBinding.imgPost.setOnClickListener(view -> clickListener.openCommentSection(getAdapterPosition()));
            itemContainerPostMultipleImagesBinding.txtPostDescription.setOnClickListener(view -> clickListener.openCommentSection(getAdapterPosition()));
            itemContainerPostMultipleImagesBinding.imgProfile.setOnClickListener(view -> clickListener.onProfileClick(getAdapterPosition()));
            itemContainerPostMultipleImagesBinding.txtUsername.setOnClickListener(view -> clickListener.onProfileClick(getAdapterPosition()));

        }

        protected void bindData(Post post, String string) {
            itemContainerPostMultipleImagesBinding.setPost(post);
            itemContainerPostMultipleImagesBinding.setCurrentUser(string);
            itemContainerPostMultipleImagesBinding.executePendingBindings();
        }

        protected void setPostImage(List<Image> urlList) {
            if (urlList != null) {
                if (urlList.size() < 3) {
                    Glide.with(itemContainerPostMultipleImagesBinding.imgPost.getContext()).load(urlList.get(0).getUrl()).into(itemContainerPostMultipleImagesBinding.imgPost);
                    Glide.with(itemContainerPostMultipleImagesBinding.imgPost.getContext()).load(urlList.get(1).getUrl()).into(itemContainerPostMultipleImagesBinding.imgPost2);
                    itemContainerPostMultipleImagesBinding.imgPost3.setVisibility(View.GONE);
                } else if (urlList.size() == 3) {
                    itemContainerPostMultipleImagesBinding.imgPost3.setVisibility(View.VISIBLE);
                    Glide.with(itemContainerPostMultipleImagesBinding.imgPost.getContext()).load(urlList.get(0).getUrl()).into(itemContainerPostMultipleImagesBinding.imgPost);
                    Glide.with(itemContainerPostMultipleImagesBinding.imgPost.getContext()).load(urlList.get(1).getUrl()).into(itemContainerPostMultipleImagesBinding.imgPost2);
                    Glide.with(itemContainerPostMultipleImagesBinding.imgPost.getContext()).load(urlList.get(2).getUrl()).into(itemContainerPostMultipleImagesBinding.imgPost3);
                } else {
                    itemContainerPostMultipleImagesBinding.imgPost3.setVisibility(View.VISIBLE);
                    Glide.with(itemContainerPostMultipleImagesBinding.imgPost.getContext()).load(urlList.get(0).getUrl()).into(itemContainerPostMultipleImagesBinding.imgPost);
                    Glide.with(itemContainerPostMultipleImagesBinding.imgPost.getContext()).load(urlList.get(1).getUrl()).into(itemContainerPostMultipleImagesBinding.imgPost2);
                    Glide.with(itemContainerPostMultipleImagesBinding.imgPost.getContext()).load(urlList.get(2).getUrl()).into(itemContainerPostMultipleImagesBinding.imgPost3);
                }
            }
        }
    }

    public interface ClickListener {
        void onOptionClick(final int pos);

        void onCommentSend(final String comment, final EditText editText, final int pos);

        void openCommentSection(final int pos);

        void onProfileClick(int pos);
    }
}
