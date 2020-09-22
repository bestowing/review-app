package com.bestowing.restaurant.home;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bestowing.restaurant.CommentInfo;
import com.bestowing.restaurant.MyViewPager;
import com.bestowing.restaurant.R;
import com.bestowing.restaurant.ReviewInfo;
import com.bestowing.restaurant.UserInfo;
import com.bestowing.restaurant.Utility;
import com.bestowing.restaurant.home.adapter.CommentAdapter;
import com.bestowing.restaurant.home.adapter.ViewPagerAdapter;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pm10.library.CircleIndicator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewDetailActivity extends AppCompatActivity {
    private Utility utility;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String TAG = "ReviewDetailActivity";
    private FirebaseFirestore db;

    private CommentAdapter commentAdapter;
    private EditText input_form;
    private TextView comments_nbr;
    private RecyclerView recyclerView;
    private ArrayList<CommentInfo> commentList;
    private ArrayList<String> tagList;
    private HashMap<String, UserInfo> userInfos;
    private ReviewInfo reviewInfo;
    private int downloadCnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.horizon_enter, R.anim.none);
        setContentView(R.layout.activity_review_detail);
        utility = new Utility();
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, this);
        userInfos = new HashMap<>();
        db = FirebaseFirestore.getInstance();

        comments_nbr = findViewById(R.id.comments_nbr);

        reviewInfo = (ReviewInfo)getIntent().getSerializableExtra("reviewInfo");
        // 사진
        ArrayList<String> photos = null;
        try {
            photos = reviewInfo.getPhotos();
        } catch (NullPointerException ignored) {}
        if (photos != null) {
            MyViewPager viewPager = findViewById(R.id.viewPager);
            viewPager.setPadding(1, 1, 1, 1);
            viewPager.setAdapter(new ViewPagerAdapter(this, reviewInfo.getPhotos()));
            if (photos.size() >= 2) {
                CircleIndicator circleIndicator = findViewById(R.id.circle_indicator);
                circleIndicator.setupWithViewPager(viewPager);
            }
        }
        // 태그
        tagList = reviewInfo.getTags();
        TextView tag1 = findViewById(R.id.tag1);
        TextView tag2 = findViewById(R.id.tag2);
        TextView tag3 = findViewById(R.id.tag3);
        if (tagList != null) {
            int tagSize = tagList.size();
            switch (tagSize) {
                case 3:
                    String tag3Content = "#" + tagList.get(2);
                    tag3.setText(tag3Content);
                    tag3.setPaintFlags(tag3.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tag3.setVisibility(View.VISIBLE);
                case 2:
                    String tag2Content = "#" + tagList.get(1);
                    tag2.setText(tag2Content);
                    tag2.setPaintFlags(tag2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tag2.setVisibility(View.VISIBLE);
                case 1:
                    String tag1Content = "#" + tagList.get(0);
                    tag1.setText(tag1Content);
                    tag1.setPaintFlags(tag1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tag1.setVisibility(View.VISIBLE);
                    break;
            }
        }
        // 댓글정보 받아오기
        recyclerView = findViewById(R.id.comment_item_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentAdapter);
        // 별점
        double lating = reviewInfo.getRating();
        ImageView star5 = findViewById(R.id.star5);
        ImageView star4 = findViewById(R.id.star4);
        ImageView star3 = findViewById(R.id.star3);
        ImageView star2 = findViewById(R.id.star2);
        ImageView star1 = findViewById(R.id.star1);
        Log.d("test123", "이 리뷰의 별점은: " + lating);
        if (lating >= 1) {
            star1.setImageResource(R.drawable.ic_star_color);
            if (lating >= 2) {
                star2.setImageResource(R.drawable.ic_star_color);
                if (lating >= 3) {
                    star3.setImageResource(R.drawable.ic_star_color);
                    if (lating >= 4) {
                        star4.setImageResource(R.drawable.ic_star_color);
                        if (lating == 5) {
                            star5.setImageResource(R.drawable.ic_star_color);
                        } else if (lating != 4){
                            star5.setImageResource(R.drawable.ic_star_half);
                        }
                    } else {
                        if (lating != 3)
                            star4.setImageResource(R.drawable.ic_star_half);
                    }
                } else {
                    if (lating != 2)
                        star3.setImageResource(R.drawable.ic_star_half);
                }
            } else {
                if (lating != 1)
                    star2.setImageResource(R.drawable.ic_star_half);
            }
        } else {
            if (lating != 0)
                star1.setImageResource(R.drawable.ic_star_half);
        }


        ImageView like = findViewById(R.id.like);
        if (reviewInfo.getLike() != null && reviewInfo.getLike().containsKey(user.getUid())) {
            like.setImageResource(R.drawable.ic_like);
        }
        TextView likeNum = findViewById(R.id.like_num);
        likeNum.setText(Long.toString(reviewInfo.getLikeNum()));
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
        createdAt.setText(utility.calculateTimeStamp(reviewInfo.getCreatedAt()));
        ImageView ic_send = findViewById(R.id.ic_send);
        input_form = findViewById(R.id.input_form);
        ic_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 작성한 댓글 보내기
                checkUpload();
            }
        });
        commentUpdates();
    }

    private void checkUpload() {
        final String comment = input_form.getText().toString();
        if (comment.length() == 0) {
            showToast("댓글을 적어도 한글자 이상 입력해주세요.");
            return ;
        }
        input_form.setText("");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 유저 ID값 필요
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        final DocumentReference documentReference = firebaseFirestore.collection("reviews").document(reviewInfo.getId()); // 현재 리뷰의 아이디값을 받음
        final Date date = new Date();
        final CommentInfo commentInfo = new CommentInfo(comment, null, user.getUid(), date, null);
        documentReference.collection("comments").add(commentInfo).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                showToast("댓글을 남겼어요.");
                commentInfo.setUserInfo(HomeActivity.mContext.getMyInfo());
                commentList.add(commentInfo);
                completeUpdateSign();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showToast("댓글 남기기에 실패했어요. 서버에 문제가 있는 것 같아요.");
            }
        });
    }

    private void commentUpdates() {
        DocumentReference documentReference = db.collection("reviews").document(reviewInfo.getId());
        CollectionReference collectionReference = documentReference.collection("comments");
        final int comment_size = commentList.size();
        Date date = comment_size == 0 ? new Date() : commentList.get(comment_size - 1).getCreatedAt();
        collectionReference.orderBy("createdAt", Query.Direction.ASCENDING)
                .whereLessThan("createdAt", date).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int unknown_num = 0;
                    ArrayList<CommentInfo> unknown = new ArrayList<>(); // 알 수 없는 유저의 아이디
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CommentInfo comment;
                        String writer = document.getData().get("writer").toString();
                        if (userInfos.containsKey(writer)) { // 새로 받은 리뷰의 유저 아이디가 기존 리스트에 존재함
                            comment = new CommentInfo(
                                    document.getData().get("content").toString(),
                                    userInfos.get(writer),
                                    writer,
                                    new Date(document.getDate("createdAt").getTime()),
                                    document.getId());
                            commentList.add(comment);
                        } else {    // 새로 받은 리뷰의 유저 아이디가 기존 리스트에 존재하지 않음 -> 일단 userinfo는 생략
                            comment = new CommentInfo(
                                    document.getData().get("content").toString(),
                                    null,
                                    writer,
                                    new Date(document.getDate("createdAt").getTime()),
                                    document.getId());
                            commentList.add(comment);
                            unknown.add(comment);
                            unknown_num++;
                        }
                    }
                    getUserInfo(unknown, unknown_num);
                } else {
                    Log.d("test123", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void getUserInfo(final ArrayList<CommentInfo> unknown, final int unknown_num) {
        downloadCnt = 0;
        if (unknown_num == 0) {
            completeUpdateSign();
        }
        for (int i=0; i<unknown_num; i++) {
            final String userId = unknown.get(i).getWriter();

            final int target_review = commentList.indexOf(unknown.get(i));
            DocumentReference docRef = db.collection("users").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        String nickName = "default";
                        String photo = "";
                        try {
                            if (document.getData().containsKey("nickName"))
                                nickName = document.getData().get("nickName").toString();
                            if (document.getData().containsKey("photoUrl"))
                                photo = document.getData().get("photoUrl").toString();
                        } catch (Exception e) {
                            Log.d("test123", e.getMessage());
                        }
                        UserInfo userInfo = new UserInfo(nickName, photo);
                        userInfos.put(userId, userInfo);
                        commentList.get(target_review).setUserInfo(userInfo);
                        if (++downloadCnt == unknown_num) {
                            completeUpdateSign();
                        }
                    }
                }
            });
        }
    }

    private void completeUpdateSign() {
        //swipeRefreshLayout.setRefreshing(false);
        commentAdapter.notifyDataSetChanged();
        String nbr = Integer.toString(commentList.size());
        comments_nbr.setText(nbr);
        //isUpdating = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.none, R.anim.horizon_exit);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}