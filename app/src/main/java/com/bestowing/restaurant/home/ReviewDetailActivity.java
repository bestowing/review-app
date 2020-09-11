package com.bestowing.restaurant.home;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bestowing.restaurant.R;
import com.bestowing.restaurant.ReviewInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ReviewDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detail);

        ReviewInfo reviewInfo = (ReviewInfo)getIntent().getSerializableExtra("reviewInfo");
        TextView userComment = findViewById(R.id.user_comment);
        userComment.setText(reviewInfo.getUserComment());

        TextView createdAt = findViewById(R.id.createAtTextView);
        createdAt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(reviewInfo.getCreatedAt()));

        LinearLayout contentsLayout = findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> photoList = reviewInfo.getPhotos();

        if (contentsLayout.getTag() == null || !contentsLayout.getTag().equals(photoList)) {
            contentsLayout.setTag(photoList);
            contentsLayout.removeAllViews();
            if(photoList != null) {
                for (int i = 0; i < photoList.size(); i++) {
                    String contents = photoList.get(i);
                    ImageView imageView = new ImageView(this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    Glide.with(this).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                }
            }

        }
    }
}