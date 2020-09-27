package com.bestowing.restaurant.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bestowing.restaurant.R;
import com.bestowing.restaurant.ReviewInfo;
import com.bestowing.restaurant.UserInfo;
import com.bestowing.restaurant.auth.LoginActivity;
import com.bestowing.restaurant.home.fragments.HomeFragment;
import com.bestowing.restaurant.home.fragments.MyPageFragment;
import com.bestowing.restaurant.home.fragments.RankFragment;
import com.bestowing.restaurant.home.fragments.StoreListFragment;
import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class HomeActivity extends AppCompatActivity {
    private final int REQUEST_IS_REVIEW_ADDED = 1;

    public FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();               // Auth 유저 정보
    private UserInfo myInfo;

    public static HomeActivity mContext;

    private ImageButton home_btn, category_btn, rank_btn, my_page_btn; // 하단바의 이미지 버튼
    private ImageButton write_review_btn;

    private FragmentManager manager;
    private HomeFragment homeFragment;
    private StoreListFragment storeListFragment;
    private RankFragment rankFragment;
    protected MyPageFragment myPageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;
        manager = getSupportFragmentManager();
        home_btn = findViewById(R.id.home_btn);
        home_btn.setImageResource(R.drawable.ic_home_selected);
        category_btn = findViewById(R.id.category_btn);
        rank_btn = findViewById(R.id.rank_btn);
        my_page_btn = findViewById(R.id.my_page_btn);
        write_review_btn = findViewById(R.id.write_review_btn);
        setMyInfoFromServer(); // 최초 실행시에만 서버에서 정보를 받아옴
    }

    private void setFragments() {
        homeFragment = new HomeFragment();
        storeListFragment = new StoreListFragment();
        rankFragment = new RankFragment();
        myPageFragment = new MyPageFragment();
        manager.beginTransaction().add(R.id.frameLayout, homeFragment)
                .add(R.id.frameLayout, storeListFragment).hide(storeListFragment)
                .add(R.id.frameLayout, rankFragment).hide(rankFragment)
                .add(R.id.frameLayout, myPageFragment).hide(myPageFragment)
                .commit();
        category_btn.setOnClickListener(bottomBarClickListener);
        home_btn.setOnClickListener(bottomBarClickListener);
        rank_btn.setOnClickListener(bottomBarClickListener);
        my_page_btn.setOnClickListener(bottomBarClickListener);
        write_review_btn.setOnClickListener(onClickListener);
    }

    private View.OnClickListener bottomBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.home_btn:
                    manager.beginTransaction().hide(storeListFragment).hide(rankFragment).hide(myPageFragment).show(homeFragment).commit();
                    home_btn.setImageResource(R.drawable.ic_home_selected);
                    category_btn.setImageResource(R.drawable.ic_restaurant);
                    rank_btn.setImageResource(R.drawable.ic_rank);
                    my_page_btn.setImageResource(R.drawable.ic_settings);
                    break;
                case R.id.category_btn:
                    manager.beginTransaction().hide(homeFragment).hide(rankFragment).hide(myPageFragment).show(storeListFragment).commit();
                    home_btn.setImageResource(R.drawable.ic_home);
                    category_btn.setImageResource(R.drawable.ic_restaurant_selected);
                    rank_btn.setImageResource(R.drawable.ic_rank);
                    my_page_btn.setImageResource(R.drawable.ic_settings);
                    break;
                case R.id.rank_btn:
                    manager.beginTransaction().hide(homeFragment).hide(storeListFragment).hide(myPageFragment).show(rankFragment).commit();
                    home_btn.setImageResource(R.drawable.ic_home);
                    category_btn.setImageResource(R.drawable.ic_restaurant);
                    rank_btn.setImageResource(R.drawable.ic_rank_selected);
                    my_page_btn.setImageResource(R.drawable.ic_settings);
                    break;
                case R.id.my_page_btn :
                    manager.beginTransaction().hide(homeFragment).hide(storeListFragment).hide(rankFragment).show(myPageFragment).commit();
                    home_btn.setImageResource(R.drawable.ic_home);
                    category_btn.setImageResource(R.drawable.ic_restaurant);
                    rank_btn.setImageResource(R.drawable.ic_rank);
                    my_page_btn.setImageResource(R.drawable.ic_settings_selected);
                    break;
            }
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.write_review_btn:
                    startNewActivityForResult(WriteReviewActivity.class);
                    break;
            }
        }
    };

    private void startNewActivityForResult(Class c) {
        Intent intent = new Intent(mContext, c);
        startActivityForResult(intent, REQUEST_IS_REVIEW_ADDED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IS_REVIEW_ADDED && resultCode == RESULT_OK) {
            ReviewInfo reviewInfo = (ReviewInfo)data.getSerializableExtra("reviewInfo");
            reviewInfo.setId(data.getStringExtra("reviewId"));
            reviewInfo.setUserInfo(mContext.getMyInfo());
            homeFragment.reviewItemAdded(reviewInfo);
        }
    }

    public void logout() {
        AuthUI.getInstance().signOut(this)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> task) {
                showToast("로그아웃에 성공했어요.");
                moveActivity(LoginActivity.class);
            }
        });
    }

    private void moveActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
        finish();
    }

    public void setMyInfoFromUser(UserInfo updatedMyInfo) {
        this.myInfo.setNickName(updatedMyInfo.getNickName());
        this.myInfo.setPhotoUrl(updatedMyInfo.getPhotoUrl());
    }

    private void setMyInfoFromServer() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        userRef.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String photoUrl = null;
                    String my_nickname = "미인증";
                    try {
                        my_nickname = document.get("nickName").toString();
                        photoUrl = document.get("photoUrl").toString();
                    } catch (Exception ignored) {}
                    myInfo = new UserInfo(my_nickname, photoUrl);
                    if (user.isEmailVerified() && myInfo.getNickName().equals("미인증")) {
                        myInfo.setNickName("익명");
                    }
                    setFragments();
                }
            }
        });
    }

    public UserInfo getMyInfo() {
        return myInfo;
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
