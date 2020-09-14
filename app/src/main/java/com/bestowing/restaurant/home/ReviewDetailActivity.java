package com.bestowing.restaurant.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bestowing.restaurant.MyViewPager;
import com.bestowing.restaurant.home.adapter.ViewPagerAdapter;
import com.bumptech.glide.Glide;
import com.bestowing.restaurant.R;
import com.bestowing.restaurant.ReviewInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewDetailActivity extends AppCompatActivity {
    MyViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.horizon_enter, R.anim.none);
        setContentView(R.layout.activity_review_detail);

        ReviewInfo reviewInfo = (ReviewInfo)getIntent().getSerializableExtra("reviewInfo");
        if (reviewInfo.getPhotos() != null) {
            viewPager = findViewById(R.id.viewPager);
            //viewPager.setClipToPadding(false);
            //viewPager.setPageMargin(1px);
            viewPager.setPadding(1, 1, 1, 1);
            viewPager.setAdapter(new ViewPagerAdapter(this, reviewInfo.getPhotos()));
        }

        TextView writer_nickname = findViewById(R.id.writer_nickname);
        writer_nickname.setText(reviewInfo.getUserInfo().getNickName());
        CircleImageView writer_profile = findViewById(R.id.writer_profile);
        if (reviewInfo.getUserInfo().getPhotoUrl().equals("")) {
            writer_profile.setImageResource(R.drawable.default_profile);
        } else {
            try {
                Glide.with(this).load(reviewInfo.getUserInfo().getPhotoUrl()).error(R.drawable.default_profile).into(writer_profile);
            } catch (Exception e) {}
        }

        TextView title = findViewById(R.id.title);
        title.setText(reviewInfo.getTitle());

        TextView userComment = findViewById(R.id.user_comment);
        userComment.setText(reviewInfo.getUserComment());

        TextView createdAt = findViewById(R.id.createAtTextView);
        createdAt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(reviewInfo.getCreatedAt()));
        /*
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

         */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, R.anim.horizon_exit);
    }
}