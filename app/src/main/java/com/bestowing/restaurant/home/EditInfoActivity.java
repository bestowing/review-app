package com.bestowing.restaurant.home;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bestowing.restaurant.PhotoModuleActivity;
import com.bestowing.restaurant.R;
import com.bestowing.restaurant.UserInfo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

//Todo: 닉네임 중복검사 필요 + 닉네임만 변경한 경우 or 사진만 변경한 경우
public class EditInfoActivity extends PhotoModuleActivity {
    private FirebaseFirestore db;
    private RelativeLayout loaderLyaout;
    private StorageReference storageRef;
    private FirebaseUser user;
    private CircleImageView user_profile;

    EditText user_nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        user_nickname = findViewById(R.id.user_nickname);
        loaderLyaout = findViewById(R.id.loaderLyaout);
        findImageView(R.id.user_profile);
        findViewById(R.id.submit_btn).setOnClickListener(onClickListener);
        user_profile = findViewById(R.id.user_profile);
        findViewById(R.id.edit_profile_btn).setOnClickListener(onClickListener);
        setUserInfo();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.submit_btn:
                    profileUpdate();
                    break;
                case R.id.edit_profile_btn:
                    chooseForPhoto();
                    break;
            }
        }
    };

    private void setUserInfo() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        userRef.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String photoUrl = null;
                    Uri uri = null;
                    try {
                        photoUrl = document.get("photoUrl").toString();
                        uri = Uri.parse(photoUrl);
                    } catch (Exception ignored) {}
                    user_nickname.setText(document.get("nickName").toString());
                    Glide.with(getApplicationContext()).load(uri).error(R.drawable.default_profile).into(user_profile);
                }
            }
        });
    }

    private void profileUpdate() {
        final String nickName = user_nickname.getText().toString().trim();
        storageRef = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        loaderLyaout.setVisibility(View.VISIBLE);

        if(nickName.length() > 0) {
            loaderLyaout.setVisibility(View.VISIBLE);
            if(photoUploaded) {
                try {
                    final StorageReference photoReference = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");
                    UploadTask uploadTask = photoReference.putFile(upload_photo);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return photoReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                UserInfo userInfo = new UserInfo(nickName, downloadUri.toString());
                                upLoadInfo(userInfo);
                            } else {
                                showToast("회원정보 수정에 실패했어요.");
                            }
                        }
                    });
                } catch (Exception e) {}
            } else {
                UserInfo userInfo = new UserInfo(nickName, null);
                upLoadInfo(userInfo);
            }
        } else {
            showToast("닉네임을 입력해주세요.");
        }
    }

    private void upLoadInfo(final UserInfo userInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ((HomeActivity)HomeActivity.mContext).myPageFragment.changeInfo();
                        showToast("회원정보 등록을 성공했어요.");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("회원정보 등록에 실패했어요.");
                        Log.d("test123", "Error writing document " + e);
                    }
                });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
