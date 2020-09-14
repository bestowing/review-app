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
import com.bestowing.restaurant.UserInfo;
import com.bestowing.restaurant.home.HomeActivity;
import com.bestowing.restaurant.home.WriteReviewActivity;
import com.bestowing.restaurant.home.adapter.ReviewAdapter;
import com.bestowing.restaurant.home.listener.OnReviewListener;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private ReviewAdapter reviewAdapter;
    private RecyclerView recyclerView;
    private ArrayList<ReviewInfo> reviewList;
    private HashMap<String, UserInfo> userInfos; // 유저 ID를 key, 유저 정보를 value로 저장하는 자료구조
    private SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {}

    private boolean isUpdating;
    int successCount;
    private int downloadCnt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        rootView.findViewById(R.id.write_review_btn).setOnClickListener(onClickListener);

        reviewList = new ArrayList<>();
        userInfos = new HashMap<>();
        reviewAdapter = new ReviewAdapter(HomeActivity.mContext, reviewList);
        reviewAdapter.setOnReviewListener(onReviewListener);

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        recyclerView = rootView.findViewById(R.id.review_item_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.mContext));
        recyclerView.setAdapter(reviewAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount && isUpdating == false) {
                    reviewUpdates();
                }
            }
        });
        swipeRefreshLayout = rootView.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // 기존 데이터를 버리고 초기화
            @Override
            public void onRefresh() {
                reviewList.clear();
                userInfos.clear();
                reviewAdapter.notifyDataSetChanged();
                reviewUpdates();
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
        final int review_size = reviewList.size();
        Date date = review_size == 0 ? new Date() : reviewList.get(review_size - 1).getCreatedAt();
        collectionReference.orderBy("createdAt", Query.Direction.DESCENDING).whereLessThan("createdAt", date).limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("test123", "기존 리뷰 개수: " + review_size);
                            int unknown_num = 0;
                            ArrayList<ReviewInfo> unknown = new ArrayList<>(); // 알 수 없는 유저의 아이디 -> reviewList의 인덱스
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ReviewInfo review;
                                String writer = document.getData().get("writer").toString();
                                if (userInfos.containsKey(writer)) { // 새로 받은 리뷰의 유저 아이디가 기존 리스트에 존재함
                                    review = new ReviewInfo(
                                            document.getData().get("title").toString(),
                                            document.getData().get("userComment").toString(),
                                            (ArrayList<String>) document.getData().get("photos"),
                                            userInfos.get(writer),
                                            writer,
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId());
                                    reviewList.add(review);
                                } else {    // 새로 받은 리뷰의 유저 아이디가 기존 리스트에 존재하지 않음 -> 일단 userinfo는 생략
                                    review = new ReviewInfo(
                                            document.getData().get("title").toString(),
                                            document.getData().get("userComment").toString(),
                                            (ArrayList<String>) document.getData().get("photos"),
                                            writer,
                                            new Date(document.getDate("createdAt").getTime()),
                                            document.getId());
                                    reviewList.add(review);
                                    unknown.add(review);
                                    unknown_num++;
                                }
                            }
                            getUserInfo(unknown, unknown_num);
                        } else {
                            Log.d("test123", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // 새로 로딩한 리뷰의 유저 아이디를 모르는 경우, 디비에서 받아와서 넣어줌
    // unknown값은 인덱스값임
    // unknown_num은 개수임
    private void getUserInfo(final ArrayList<ReviewInfo> unknown, final int unknown_num) {
        downloadCnt = 0;
        if (unknown_num == 0) {
            completeReviewUpdateSign();
        }
        for (int i=0; i<unknown_num; i++) {
            final String userId = unknown.get(i).getWriter();

            final int target_review = reviewList.indexOf(unknown.get(i));
            DocumentReference docRef = db.collection("users").document(userId);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        String nickName = "default";
                        String photo = "";
                        try {
                            if (document.getData().containsKey("nickName"))
                                nickName = document.getData().get("nickName").toString();
                            if (document.getData().containsKey("photoUrl"))
                                photo = document.getData().get("photoUrl").toString();
                        } catch (Exception e) {
                            Log.d("test123", e.getMessage());
                        }
                        UserInfo userInfo = new UserInfo(nickName, photo);
                        userInfos.put(userId, userInfo);
                        reviewList.get(target_review).setUserInfo(userInfo);
                        if (++downloadCnt == unknown_num) {
                            completeReviewUpdateSign();
                        }
                    }
                }
            });
        }
    }

    private void completeReviewUpdateSign() {
        swipeRefreshLayout.setRefreshing(false);
        reviewAdapter.notifyDataSetChanged();
        isUpdating = false;
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
