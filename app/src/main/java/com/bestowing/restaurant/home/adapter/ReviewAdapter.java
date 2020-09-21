package com.bestowing.restaurant.home.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bestowing.restaurant.FirebaseHelper;
import com.bestowing.restaurant.MyViewPager;
import com.bestowing.restaurant.R;
import com.bestowing.restaurant.ReviewInfo;
import com.bestowing.restaurant.UserInfo;
import com.bestowing.restaurant.Utility;
import com.bestowing.restaurant.home.HomeActivity;
import com.bestowing.restaurant.home.ReviewDetailActivity;
import com.bestowing.restaurant.home.WriteReviewActivity;
import com.bestowing.restaurant.home.listener.OnReviewListener;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.pm10.library.CircleIndicator;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<ReviewInfo> mDataset;
    private Activity activity;
    private FirebaseHelper firebaseHelper;
    private String myId;
    private Utility utility;
    private final FirebaseFirestore db;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public ReviewAdapter(Activity activity, ArrayList<ReviewInfo> myDataset, String myId) {
        this.mDataset = myDataset;
        this.activity = activity;
        this.myId = myId;
        this.utility = new Utility();
        db = FirebaseFirestore.getInstance();
        firebaseHelper = new FirebaseHelper(activity);
    }

    public void setOnReviewListener(OnReviewListener onReviewListener) {
        firebaseHelper.setOnPostListener(onReviewListener);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        final ViewHolder viewHolder = new ViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getFragmentManager();
                Intent intent = new Intent(activity, ReviewDetailActivity.class);
                intent.putExtra("reviewInfo", mDataset.get(viewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopup(v, viewHolder.getAdapterPosition());
                return false;
            }
        });

        /*
        cardView.findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, viewHolder.getAdapterPosition());
            }
        });

         */

        cardView.findViewById(R.id.like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                firebaseHelper.clickLike(mDataset.get(position).getId(), myId, position);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        TextView writer_nickname = cardView.findViewById(R.id.writer_nickname);
        CircleImageView writer_profile = cardView.findViewById(R.id.writer_profile);
        final ImageView like = cardView.findViewById(R.id.like);
        final TextView likeNum = cardView.findViewById(R.id.like_num);
        TextView title = cardView.findViewById(R.id.title);
        //TextView user_comment = cardView.findViewById(R.id.user_comment);
        TextView createdAtTextView = cardView.findViewById(R.id.createAtTextView);
        TextView tag1 = cardView.findViewById(R.id.tag1);
        TextView tag2 = cardView.findViewById(R.id.tag2);
        TextView tag3 = cardView.findViewById(R.id.tag3);
        /*
        // 닉네임 세팅
        writer_nickname.setText(mDataset.get(position).getUserInfo().getNickName());
        // 프로필 사진 세팅
        String photo_profile = mDataset.get(position).getUserInfo().getPhotoUrl();
        // 프로필 사진을 설정하지 않았거나, 로딩중 에러가 발생하면 기본 사진으로 세팅
        if (photo_profile.equals("")) {
            writer_profile.setImageResource(R.drawable.default_profile);
        } else {
            try {
                Glide.with(activity).load(photo_profile).error(R.drawable.default_profile).into(writer_profile);
            } catch (Exception e) {}
        }
         */
        // 좋아요 버튼 세팅
        if (mDataset.get(position).getLike() != null && mDataset.get(position).getLike().containsKey(myId)) {
            like.setImageResource(R.drawable.ic_like);
        } else {
            like.setImageResource(R.drawable.ic_non_like);
        }
        // 좋아요 숫자 세팅
        likeNum.setText(Long.toString(mDataset.get(position).getLikeNum()));
        // 제목 세팅
        String _title = mDataset.get(position).getTitle();
        // 제목이 너무 길면 ... 처리하기
        if (_title.length() > 24) {
            String summary = _title.substring(0, 24).concat("...");
            title.setText(summary);
        } else {
            title.setText(_title);
        }
        // 본문 세팅
        /*
        String comment = mDataset.get(position).getUserComment();
        // 본문 내용이 너무 길면 ... 처리하기
        if (comment.length() > 30) {
            String summary = comment.substring(0, 30).concat("...");
            user_comment.setText(summary);
        } else {
            user_comment.setText(comment);
        }
         */
        // 타임스탬프 세팅
        //createdAtTextView.setText(utility.calculateTimeStamp(mDataset.get(position).getCreatedAt()));
        // 사진 뷰페이저 세팅
        ArrayList<String> photos = mDataset.get(position).getPhotos();
        MyViewPager viewPager = cardView.findViewById(R.id.viewPager);
        if (viewPager.getTag() == null || !viewPager.getTag().equals(photos)) {
            viewPager.setTag(photos);
            viewPager.removeAllViews();
            if (photos != null) {
                viewPager.setPadding(0, 1, 0, 1);
                viewPager.setAdapter(new ViewPagerAdapter(activity, photos));
                if (photos.size() >= 2) {
                    CircleIndicator circleIndicator = cardView.findViewById(R.id.circle_indicator);
                    circleIndicator.setupWithViewPager(viewPager);
                }
            }
        }
        // 태그 세팅
        ArrayList<String> tagsList = mDataset.get(position).getTags();
        if (tagsList != null) {
            int tagSize = tagsList.size();
            switch (tagSize) {
                case 3:
                    String tag3Content = "#" + tagsList.get(2);
                    tag3.setText(tag3Content);
                    tag3.setPaintFlags(tag3.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tag3.setVisibility(View.VISIBLE);
                case 2:
                    String tag2Content = "#" + tagsList.get(1);
                    tag2.setText(tag2Content);
                    tag2.setPaintFlags(tag2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tag2.setVisibility(View.VISIBLE);
                case 1:
                    String tag1Content = "#" + tagsList.get(0);
                    tag1.setText(tag1Content);
                    tag1.setPaintFlags(tag1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tag1.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.modify:
                        startNewActivity(WriteReviewActivity.class, mDataset.get(position));
                        return true;
                    case R.id.delete:
                        firebaseHelper.deleteStorage(mDataset.get(position));
                        return true;
                    default:
                        return false;
                }
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.review, popup.getMenu());
        popup.show();
    }

    private void startNewActivity(Class c, ReviewInfo reviewInfo) {
        Intent intent = new Intent(this.activity, c);
        intent.putExtra("reviewInfo", reviewInfo);
        activity.startActivity(intent);
    }
}