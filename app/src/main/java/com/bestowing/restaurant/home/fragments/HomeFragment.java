package com.bestowing.restaurant.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bestowing.restaurant.R;
import com.bestowing.restaurant.ReviewInfo;
import com.bestowing.restaurant.home.HomeActivity;
import com.bestowing.restaurant.home.WriteReviewActivity;
import com.bestowing.restaurant.home.adapter.ReviewAdapter;
import com.bestowing.restaurant.home.listener.OnReviewListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

public class HomeFragment extends Fragment {
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private ReviewAdapter reviewAdapter;
    private ArrayList<ReviewInfo> reviewList;
    private SwipeRefreshLayout swipeLayout;

    public HomeFragment() {}

    private int successCount;
    private boolean isUpdating;
    private boolean topScrolled;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        rootView.findViewById(R.id.write_review_btn).setOnClickListener(onClickListener);

        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(HomeActivity.mContext, reviewList);
        reviewAdapter.setOnReviewListener(onReviewListener);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        RecyclerView recyclerView = rootView.findViewById(R.id.review_item_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.mContext));
        recyclerView.setAdapter(reviewAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                if(newState == 1 && firstVisibleItemPosition == 0){
                    topScrolled = true;
                }
                if(newState == 0 && topScrolled){
                    reviewList.clear();
                    reviewUpdates();
                    topScrolled = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

                if((totalItemCount - 1 <= lastVisibleItemPosition) && !isUpdating) {
                    reviewUpdates();
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });

        reviewUpdates();
        return rootView;
    }

    private OnReviewListener onReviewListener = new OnReviewListener() {
        @Override
        public void onDelete(ReviewInfo reviewInfo) {
            reviewList.remove(reviewInfo);
            reviewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onModify() {}
    };

    private void storeUploader(String id){
        if(successCount == 0) {
            db.collection("reviews").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast("게시글을 삭제했어요.");
                            reviewUpdates();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast("삭제하지 못했어요.");
                        }
                    });
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.write_review_btn:
                    startNewActivity(WriteReviewActivity.class);
                    break;
            }
        }
    };

    private void reviewUpdates() {
        isUpdating = true;
        CollectionReference collectionReference = db.collection("reviews");
        Date date = reviewList.size() == 0 ? new Date() : reviewList.get(reviewList.size() - 1).getCreatedAt();
        collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).whereLessThan("createdAt", date).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                reviewList.add(new ReviewInfo(
                                        document.getData().get("userComment").toString(),
                                        (ArrayList<String>) document.getData().get("photos"),
                                        document.getData().get("writer").toString(),
                                        new Date(document.getDate("createdAt").getTime()),
                                        document.getId()));
                                isUpdating = false;
                            }
                            reviewAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("test123", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void startNewActivity(Class c) {
        Intent intent = new Intent(getContext(), c);
        startActivity(intent);
    }

    private void showToast(String msg) {
        Toast.makeText(HomeActivity.mContext, msg, Toast.LENGTH_SHORT).show();
    }

    private String storageUrlToName(String url){
        return url.split("\\?")[0].split("%2F")[url.split("\\?")[0].split("%2F").length - 1];
    }
}
