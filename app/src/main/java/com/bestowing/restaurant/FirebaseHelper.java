package com.bestowing.restaurant;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bestowing.restaurant.home.listener.OnReviewListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FirebaseHelper {
    private Activity activity;
    private OnReviewListener reviewListener;
    private int cnt;

    public FirebaseHelper(Activity activity) {
        this.activity = activity;
    }

    public void setOnPostListener(OnReviewListener reviewListener){
        this.reviewListener = reviewListener;
    }

    public void deleteStorage(final ReviewInfo reviewInfo){
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        final String id = reviewInfo.getId();
        ArrayList<String> photoList = reviewInfo.getPhotos();
        if(photoList != null) {
            int Size = photoList.size();
            for (int i = 0; i < Size; i++) {
                String photo = photoList.get(i);
                cnt++;
                StorageReference targetRef = storageRef.child("reviews/" + id + "/" + storageUrlToName(photo));
                targetRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        cnt--;
                        deleteStore(id, reviewInfo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showToast("Error");
                    }
                });
            }
        } else {
            deleteStore(id, reviewInfo);
        }
    }

    private void deleteStore(final String id, final ReviewInfo reviewInfo) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if (cnt == 0) {
            firebaseFirestore.collection("reviews").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast("게시글을 삭제하였습니다.");
                            reviewListener.onDelete(reviewInfo);
                            //postsUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this.activity, msg, Toast.LENGTH_SHORT).show();
    }

    private String storageUrlToName(String url){
        return url.split("\\?")[0].split("%2F")[url.split("\\?")[0].split("%2F").length - 1];
    }
}
