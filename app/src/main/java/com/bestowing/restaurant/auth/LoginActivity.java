package com.bestowing.restaurant.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bestowing.restaurant.R;
import com.bestowing.restaurant.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    //private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth; // 로그인

    private EditText user_email, user_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        user_email = findViewById(R.id.user_id);
        user_pwd = findViewById(R.id.user_pwd);
        findViewById(R.id.sign_up_btn).setOnClickListener(onClickListener);
        findViewById(R.id.login_btn).setOnClickListener(onClickListener);
        findViewById(R.id.reset_pwd_btn).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn:
                    tryLogin();                                 // 입력받은 정보로 로그인 시도
                    break;
                case R.id.sign_up_btn:
                    startNewActivity(SignUpActivity.class);     // 회원가입 액티비티
                    break;
                case R.id.reset_pwd_btn:
                    startNewActivity(ResetPwdActivity.class);   // 비밀번호 재설정 액티비티
                    break;
            }
        }
    };

    private void tryLogin() {
        String email = user_email.getText().toString().trim();
        String pwd = user_pwd.getText().toString().trim();

        if(email.length() > 0 && pwd.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                showToast("로그인에 성공했어요.");
                                if( user.isEmailVerified() ) {
                                    moveActivity(HomeActivity.class);           // 홈 액티비티로
                                } else {
                                    moveActivity(EmailVerifyActivity.class);    // 이메일 인증 액티비티로
                                }
                            } else {
                                if(task.getException() != null){
                                    showToast(task.getException().toString());
                                }
                            }
                        }
                    });
        } else {
            showToast("이메일 혹은 비밀번호를 입력하지 않았어요.");
        }
    }

    private void startNewActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
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
