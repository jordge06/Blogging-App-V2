package com.example.bloggappapi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.bloggappapi.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserPostsAdapter extends RecyclerView.Adapter<UserPostsAdapter.MyViewHolder> {

    List<String> urlList;
    LayoutInflater layoutInflater;

    public UserPostsAdapter(List<String> urlList) {
        this.urlList = urlList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) layoutInflater = LayoutInflater.from(parent.getContext());
        return new MyViewHolder(layoutInflater.inflate(
                R.layout.item_container_post_thumbnail,
                parent,
                false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setPostImage(urlList.get(position));
    }

    @Override
    public int getItemCount() {
        return urlList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgPost;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgPost = itemView.findViewById(R.id.imgPost);
            ImageView btnRemove = itemView.findViewById(R.id.btnRemove);
            btnRemove.setVisibility(View.GONE);
        }

        protected void setPostImage(String url) {
            if (url.equals("")) {
                imgPost.setImageResource(R.drawable.ic_text);
            } else {
                Glide.with(imgPost.getContext())
                        .load(url)
                        .into(imgPost);
            }
        }
    }
}