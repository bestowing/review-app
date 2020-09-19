package com.bestowing.restaurant;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bestowing.restaurant.home.adapter.ReviewAdapter;
import com.bestowing.restaurant.home.listener.OnReviewListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {
    private Activity activity;
    private OnReviewListener reviewListener;
    private int cnt;
    private final FirebaseFirestore db;

    public FirebaseHelper(Activity activity) {
        this.activity = activity;
        db = FirebaseFirestore.getInstance();
    }

    public void setOnPostListener(OnReviewListener reviewListener) {
        this.reviewListener = reviewListener;
    }

    public void deleteStorage(final ReviewInfo reviewInfo) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        final String id = reviewInfo.getId();
        ArrayList<String> photoList = reviewInfo.getPhotos();
        if (photoList != null) {
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

    public void clickLike(final String reviewId, final String myId) {
        final DocumentReference sfDocRef = FirebaseFirestore.getInstance().collection("reviews").document(reviewId);
        db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);

                Map<String, Boolean> like = (Map<String, Boolean>) snapshot.get("like");
                long likeNum = snapshot.getLong("likeNum");
                if (like == null) {
                    like = new HashMap<String, Boolean>();
                    like.put(myId, true);
                    likeNum = 1;
                    //ic_like.setImageResource(R.drawable.ic_like);
                    //Toast.makeText(activity, "좋아요!", Toast.LENGTH_SHORT).show();
                } else if (like.containsKey(myId)) {
                    like.remove(myId);
                    likeNum -= 1;
                    //ic_like.setImageResource(R.drawable.ic_non_like);
                    //Toast.makeText(activity, "좋아요를 취소했어요.", Toast.LENGTH_SHORT).show();
                } else {
                    like.put(myId, true);
                    likeNum += 1;
                    //ic_like.setImageResource(R.drawable.ic_like);
                    //Toast.makeText(activity, "좋아요!", Toast.LENGTH_SHORT).show();
                }
                //likeView.setText(Long.toString(likeNum));
                transaction.update(sfDocRef, "like", like);
                transaction.update(sfDocRef, "likeNum", likeNum);

                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //reviewListener.onLike();
                Log.d("debugReviewAdapter", "좋아요 기능 성공");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("debugReviewAdapter", e.getMessage());
                        Toast.makeText(activity, "좋아하지 못했을 수도 있어요.", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private String storageUrlToName(String url) {
        return url.split("\\?")[0].split("%2F")[url.split("\\?")[0].split("%2F").length - 1];
    }
}
