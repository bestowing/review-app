package com.bestowing.restaurant.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bestowing.restaurant.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {
    //private static final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;

    private EditText user_email, user_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        user_email = (EditText) findViewById(R.id.sign_up_email);
        user_pwd = (EditText) findViewById(R.id.sign_up_pwd);
        findViewById(R.id.sign_up_btn).setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sign_up_btn:
                    signUp();
                    break;
            }
        }
    };

    //Todo: if문 사용해서 이메일이나 비밀번호를 입력하지 않은 경우 체크 + 비밀번호 확인 칸을 추가해서 일치하지 않는 경우 체크
    private void signUp() {
        String email = user_email.getText().toString().trim();
        String pwd = user_pwd.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showToast("회원 가입에 성공했어요. 가입하신 정보로 로그인해주세요.");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            finish();
                        } else {
                            showToast(task.getException().toString());
                        }
                    }
                });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
