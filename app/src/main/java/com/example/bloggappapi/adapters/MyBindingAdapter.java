package com.example.bloggappapi.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.example.bloggappapi.R;
import com.example.bloggappapi.models.Image;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

public class MyBindingAdapter {

    @BindingAdapter("setProfileImage")
    public static void setProfile(ImageView imageView, String url) {
        if (!url.isEmpty()) {
            Glide.with(imageView.getContext())
                    .load(url)
                    .circleCrop()
                    .error(R.drawable.ic_empty_profile)
                    .into(imageView);
        }
    }

    @BindingAdapter("setPostImage")
    public static void setPost(ImageView imageView, List<Image> url) {
        if (url.size() > 0) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(imageView.getContext())
                    .load(url.get(0).getUrl())
                    .into(imageView);
        } else {
            imageView.setVisibility(View.GONE);
        }
    }

    @BindingAdapter("setShimmerText")
    public static void setShimmer(TextView textView, boolean isLoading) {
        if (isLoading) {
            textView.setBackgroundColor(ContextCompat.getColor(textView.getContext(), R.color.backgroundShimmer));
        } else {
            textView.setBackgroundColor(ContextCompat.getColor(textView.getContext(), R.color.colorPrimaryDark));
        }
    }

    @BindingAdapter("setShimmerImage")
    public static void setShimmerEffect(ImageView imageView, boolean isLoading) {
        if (isLoading) {
            imageView.setColorFilter(ContextCompat.getColor(imageView.getContext(), R.color.backgroundShimmer));
        } else {
            imageView.clearColorFilter();
        }
    }
}
