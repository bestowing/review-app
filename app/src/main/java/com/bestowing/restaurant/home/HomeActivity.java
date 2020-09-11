package com.bestowing.restaurant.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bestowing.restaurant.R;
import com.bestowing.restaurant.auth.LoginActivity;
import com.bestowing.restaurant.home.fragments.HomeFragment;
import com.bestowing.restaurant.home.fragments.MyPageFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();               // Auth 유저 정보

    public static HomeActivity mContext;

    private ImageButton home_btn, category_btn, rank_btn, my_page_btn; // 하단바의 이미지 버튼

    private FragmentManager manager;
    private HomeFragment homeFragment;
    protected MyPageFragment myPageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = this;

        manager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        myPageFragment = new MyPageFragment();
        manager.beginTransaction().add(R.id.frameLayout, homeFragment).add(R.id.frameLayout, myPageFragment).hide(myPageFragment).commit();

        // 버튼 및 프래그먼트 설정
        home_btn = findViewById(R.id.home_btn);
        home_btn.setImageResource(R.drawable.ic_home_selected);
        my_page_btn = findViewById(R.id.my_page_btn);
        home_btn.setOnClickListener(bottomBarClickListener);
        my_page_btn.setOnClickListener(bottomBarClickListener);
    }

    private View.OnClickListener bottomBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.home_btn:
                    manager.beginTransaction().hide(myPageFragment).show(homeFragment).commit();
                    home_btn.setImageResource(R.drawable.ic_home_selected);
                    //category_btn.setImageResource(R.drawable.ic_restaurant);
                    my_page_btn.setImageResource(R.drawable.ic_settings);
                    break;
                case R.id.my_page_btn :
                    manager.beginTransaction().hide(homeFragment).show(myPageFragment).commit();
                    home_btn.setImageResource(R.drawable.ic_home);
                    //category_btn.setImageResource(R.drawable.ic_restaurant);
                    my_page_btn.setImageResource(R.drawable.ic_settings_selected);
                    break;
            }
        }
    };

    public void logout() {
        AuthUI.getInstance()
                .signOut(this)
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

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
