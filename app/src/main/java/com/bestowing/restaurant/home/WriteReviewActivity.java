package com.bestowing.restaurant.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.bestowing.restaurant.PhotoModuleActivity;
import com.bestowing.restaurant.R;
import com.bestowing.restaurant.ReviewInfo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WriteReviewActivity extends PhotoModuleActivity {
    private LinearLayout parent_root;
    private ArrayList<ImageView> mView;
    private StorageReference storageRef;
    private ArrayList<Uri> mUri;
    private RelativeLayout loaderLayout;
    private EditText title;
    private EditText user_comment;

    private ReviewInfo reviewInfo;
    private int successCount = 0;

    private String testStringPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        storageRef = FirebaseStorage.getInstance().getReference();

        mView = new ArrayList<>();
        mUri = new ArrayList<>();

        loaderLayout = findViewById(R.id.loaderLyaout);
        parent_root = findViewById(R.id.parent_root);
        title = findViewById(R.id.title);
        user_comment = findViewById(R.id.user_comment);

        findViewById(R.id.picture_add_btn).setOnClickListener(onClickListener);
        findViewById(R.id.submit_btn).setOnClickListener(onClickListener);

        reviewInfo = (ReviewInfo) getIntent().getSerializableExtra("reviewInfo");
        initReview();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.picture_add_btn:
                    chooseForPhoto();
                    break;
                case R.id.submit_btn:
                    checkUpload();
                    break;
            }
        }
    };

    private ImageView.OnClickListener onImageClickListener = new ImageView.OnClickListener() {
        @Override
        public void onClick(final View view) {
            // 클릭하면 삭제함
            new AlertDialog.Builder(view.getContext())
                    .setTitle("삭제할까요?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int index = mView.indexOf(view);
                            if(reviewInfo != null) {
                                String[] list = mUri.get(index).toString().split("\\?");
                                String[] list2 = list[0].split("%2F");
                                String name = list2[list2.length - 1];
                                Log.e("로그: ","이름: "+name);

                                StorageReference photoRef = storageRef.child("reviews/"+ reviewInfo.getId()+"/"+name);
                                photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        showToast("파일을 삭제하였습니다.");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        showToast("파일을 삭제하는데 실패하였습니다."); // TODO: 왜 실패하는데 잘 삭제되는지
                                    }
                                });
                            }
                            mView.remove(index);
                            mUri.remove(index);
                            parent_root.removeView(view);
                        }
                    }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).show();

        }
    };

    private void addPicIcon(Uri uri) {
        final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.rightMargin = 10;
        ImageView imageView = new ImageView(WriteReviewActivity.this);
        mView.add(imageView);
        mUri.add(uri);
        findImageView(imageView);
        imageView.setLayoutParams(layoutParams);
        Glide.with(this).load(uri).override(1000).into(imageView);
        imageView.setId(ViewCompat.generateViewId());
        imageView.setOnClickListener(onImageClickListener);
        parent_root.addView(imageView);
    }

    @Override
    public void setImage(Uri uri) {
        addPicIcon(uri);
    }

    private void checkUpload() {
        final String titleText = title.getText().toString();
        final String commentText = user_comment.getText().toString();
        if (titleText.length() == 0) {
            showToast("제목을 적어도 한글자 이상 입력해주세요.");
            return ;
        }
        if (commentText.length() == 0) {
            showToast("리뷰 내용을 적어도 한글자 이상 입력해주세요.");
            return ;
        }
        final ArrayList<String> photoList = new ArrayList<>(); // 파이어베이스에 업로드후 그 사진의 Uri를 문자열로 변환하여 저장함
        loaderLayout.setVisibility(View.VISIBLE);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 유저 ID값 필요
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        final DocumentReference documentReference = reviewInfo == null ?
                firebaseFirestore.collection("reviews").document() :
                firebaseFirestore.collection("reviews").document(reviewInfo.getId());

        final Date date = reviewInfo == null ? new Date() : reviewInfo.getCreatedAt();

        final Map<String, Boolean> like = reviewInfo == null ? null : reviewInfo.getLike();
        final Long likeNum = reviewInfo == null ? 0 : reviewInfo.getLikeNum();

        // 사진을 올렸다가 삭제한 경우 -> 찾아서 삭제
        // 사진을 올렸다가 수정한 경우 -> 그대로 덮어씌움

        int Size = mUri.size();
        if(Size == 0) { // 사진을 올리지 않았을 경우
            ReviewInfo reviewInfo = new ReviewInfo(titleText, commentText, null, null, user.getUid(), date, null, like, likeNum);
            upLoadReview(documentReference, reviewInfo);
            return;
        }
        for(int i=0; i<Size; i++) { // 사진이 하나라도 있을 경우
            Uri uri = mUri.get(i);
            if(uri.toString().contains("https://firebasestorage.googleapis.com/v0/b/restaurant-feb04.appspot.com/o/reviews")) {
                photoList.add(uri.toString());
                successCount++;
                if(mUri.size() == successCount){
                    ReviewInfo reviewInfo = new ReviewInfo(titleText, commentText, photoList, null, user.getUid(), date, null, like, likeNum);
                    upLoadReview(documentReference, reviewInfo);
                }
            }
            String[] pathUri = uri.toString().split("\\.");
            final StorageReference photoReference = storageRef.child("reviews/" + documentReference.getId() + "/" + i + "." + pathUri[pathUri.length - 1]);
            try {
                UploadTask uploadTask = photoReference.putFile(uri);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d("test123", "에러: " + exception.toString());
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        photoReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                photoList.add(uri.toString());
                                successCount++;
                                if(mUri.size() == successCount){
                                    ReviewInfo reviewInfo = new ReviewInfo(titleText, commentText, photoList, null, user.getUid(), date, null, like, likeNum);
                                    upLoadReview(documentReference, reviewInfo);
                                }
                            }
                        });
                    }
                });
            } catch (Exception e) {}
        }
    }

    private void upLoadReview(final DocumentReference documentReference, final ReviewInfo reviewInfo) {
        documentReference.set(reviewInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("리뷰를 정상적으로 등록했어요.");
                        Intent intent = new Intent();
                        intent.putExtra("reviewInfo", reviewInfo);
                        intent.putExtra("reviewId", documentReference.getId());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("리뷰를 등록하는데 에러가 발생했어요.");
                    }
                });
    }

    private void initReview() {
        if(reviewInfo != null) {
            title.setText(reviewInfo.getTitle());
            user_comment.setText(reviewInfo.getUserComment());
            ArrayList<String> photoList = reviewInfo.getPhotos();
            if(photoList != null) {
                for (int i = 0; i < photoList.size(); i++) {
                    String photoPath = photoList.get(i);
                    addPicIcon(Uri.parse(photoPath));
                }
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
