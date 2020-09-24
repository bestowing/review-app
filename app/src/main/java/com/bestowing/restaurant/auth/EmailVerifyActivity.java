package com.bestowing.restaurant.auth;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bestowing.restaurant.R;
import com.bestowing.restaurant.UserInfo;
import com.bestowing.restaurant.home.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmailVerifyActivity extends AppCompatActivity {
    private FirebaseUser user;

    Button verify_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify);

        user = FirebaseAuth.getInstance().getCurrentUser();

        TextView user_email = findViewById(R.id.user_email);
        user_email.setText(user.getEmail());

        findViewById(R.id.send_mail_btn).setOnClickListener(onClickListener);
        findViewById(R.id.not_verify_btn).setOnClickListener(onClickListener);
        verify_btn = findViewById(R.id.verify_btn);
        verify_btn.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.send_mail_btn:
                    sendEmail();
                    break;
                case R.id.not_verify_btn:
                    showModal();
                    break;
                case R.id.verify_btn:
                    user.reload();
                    if(user.isEmailVerified()) {
                        initInfo();
                        showToast("이메일 인증을 확인했어요. 일반 사용자로 앱을 시작해요.");
                    } else {
                        showToast("아직 인증이 되지 않은것 같아요. 잠시 후에 다시 시도해주세요.");
                    }
                    break;
            }
        }
    };

    private void sendEmail() {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            showToast("이메일을 보냈어요. 메일함을 확인해주세요.");
                            verify_btn.setClickable(true);
                        }
                    }
                });
    }

    private void showModal() {
        new AlertDialog.Builder(this)
                .setTitle("다음에 인증하기")
                .setMessage("인증을 하지 않고 둘러보기만 할 수 있어요. 인증은 언제나 다시 할 수 있어요.")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showToast("게스트 계정으로 앱을 시작해요.");
                        moveActivity(HomeActivity.class);
                    }
                })
                .setNeutralButton("취소", null)
                .create().show();
    }

    private void initInfo() {
        final UserInfo userInfo = new UserInfo("익명", null);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        moveActivity(HomeActivity.class);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("문제가 생겼어요. 회원정보 등록에 실패했어요.");
                        Log.d("test123", "Error writing document " + e);
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
