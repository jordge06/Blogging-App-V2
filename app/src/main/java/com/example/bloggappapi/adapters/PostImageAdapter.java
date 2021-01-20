package com.example.bloggappapi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.bloggappapi.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostImageAdapter extends RecyclerView.Adapter {

    public static final String ADD_ICON = "add";
    private List<String> stringList;
    private LayoutInflater layoutInflater;
    private Context context;
    private static final int ADD = 0;
    private static final int NORMAL = 1;
    private OnImageAdapterClick onImageAdapterClick;

    public PostImageAdapter(List<String> stringList, Context context) {
        this.context = context;
        this.stringList = stringList;
    }

    public void setOnImageAdapterClick(OnImageAdapterClick onImageAdapterClick) {
        this.onImageAdapterClick = onImageAdapterClick;
    }

    @Override
    public int getItemViewType(int position) {
        if (stringList.get(position).equals(ADD_ICON)) {
            return ADD;
        } return NORMAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        if (viewType == ADD) {
            return new AddViewHolder(layoutInflater.inflate(
                    R.layout.item_container_add_image,
                    parent,
                    false
            ));
        }
        View view = layoutInflater.inflate(R.layout.item_container_post_thumbnail, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (stringList.get(position).equals(ADD_ICON)) {
            AddViewHolder addViewHolder = (AddViewHolder) holder;
        } else {
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            myViewHolder.setImage(stringList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return stringList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPost, btnRemove;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPost = itemView.findViewById(R.id.imgPost);
            btnRemove = itemView.findViewById(R.id.btnRemove);

            btnRemove.setOnClickListener(view -> onImageAdapterClick.onRemoveClick(getAdapterPosition()));
        }

        public void setImage(String url) {
            Glide.with(context).load(url).into(imgPost);
        }
    }

    class AddViewHolder extends RecyclerView.ViewHolder {

        public AddViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(view -> onImageAdapterClick.onAdd(getAdapterPosition()));
        }
    }

    public interface OnImageAdapterClick {
        void onAdd(int pos);

        void onRemoveClick(int pos);
    }
 }
