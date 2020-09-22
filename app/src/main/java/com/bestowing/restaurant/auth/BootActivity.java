package com.bestowing.restaurant.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.bestowing.restaurant.R;
import com.bestowing.restaurant.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BootActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if( user == null ) {                                 // 로그인된 상태인가?
            moveActivity(LoginActivity.class);               // 로그인 액티비티로
        } else {
            if( user.isEmailVerified() ) {                   // 이메일 인증을 한 사용자인가?
                moveActivity(HomeActivity.class);            // 홈 액티비티로
            } else {
                moveActivity(EmailVerifyActivity.class);     // 이메일 인증 액티비티로
            }
        }
    }

    private void moveActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
        finish();
    }
}
