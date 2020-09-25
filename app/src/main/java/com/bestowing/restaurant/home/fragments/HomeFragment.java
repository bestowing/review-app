package com.bestowing.restaurant.home.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bestowing.restaurant.CustomDialog;
import com.bestowing.restaurant.R;
import com.bestowing.restaurant.ReviewInfo;
import com.bestowing.restaurant.UserInfo;
import com.bestowing.restaurant.home.HomeActivity;
import com.bestowing.restaurant.home.adapter.ReviewAdapter;
import com.bestowing.restaurant.home.listener.OnReviewListener;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {
    //DB
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private DocumentSnapshot lastVisible;

    // VIEW
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout search_option;
    private ReviewAdapter reviewAdapter;
    private RecyclerView recyclerView;
    private TextView option_latest;
    private TextView option_popular;
    private TextView selected_position;

    // DATA
    private ArrayList<ReviewInfo> reviewList;
    private HashMap<String, UserInfo> userInfos; // 유저 ID를 key, 유저 정보를 value로 저장하는 자료구조
    private HashMap<CustomDialog.Positions, Boolean> positionFilter;

    private boolean isUpdating;
    private boolean isVanishing;
    int successCount;
    private int downloadCnt;
    private boolean isLatest;

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        reviewList = new ArrayList<>();
        userInfos = new HashMap<>();
        reviewAdapter = new ReviewAdapter(HomeActivity.mContext, reviewList, HomeActivity.mContext.user.getUid());
        reviewAdapter.setOnReviewListener(onReviewListener);
        isLatest = true;
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        search_option = rootView.findViewById(R.id.search_option);
        option_latest = rootView.findViewById(R.id.option_latest);
        option_popular = rootView.findViewById(R.id.option_popular);
        option_latest.setOnClickListener(onClickListener);
        option_popular.setOnClickListener(onClickListener);
        //ToDo: 유저가 설정한 필터링 설정을 기기에 저장하기
        positionFilter = new HashMap<>();
        positionFilter.put(CustomDialog.Positions.정문, true);
        selected_position = rootView.findViewById(R.id.selected_position);
        selected_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomDialog dialog = new CustomDialog(HomeActivity.mContext, R.style.PopupTheme);
                dialog.setFilters(positionFilter);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.show();
                dialog.setDialogResult(new CustomDialog.OnMyDialogResult() {
                    @Override
                    public void finish(HashMap<CustomDialog.Positions, Boolean> result) {
                        dialog.dismiss();
                        setFilterText(result);
                    }
                });
            }
        });
        recyclerView = rootView.findViewById(R.id.review_item_view);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.mContext));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(reviewAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount && !isUpdating) {
                    reviewUpdates();
                }

                if (dy > 10) {
                    if (search_option.getVisibility() != View.GONE && !isVanishing) {
                        Animation animation = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, 0.0f,
                                Animation.RELATIVE_TO_SELF, 0.0f,
                                Animation.RELATIVE_TO_SELF, 0.0f,
                                Animation.RELATIVE_TO_SELF, 1.0f);
                        animation.setDuration(100);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                isVanishing = true;
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                search_option.setVisibility(View.GONE);
                                isVanishing = false;
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        search_option.startAnimation(animation);
                    }
                } else if (dy < -10) {
                    if (search_option.getVisibility() != View.VISIBLE) {
                        search_option.setVisibility(View.VISIBLE);
                        Animation animation = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, 0.0f,
                                Animation.RELATIVE_TO_SELF, 0.0f,
                                Animation.RELATIVE_TO_SELF, 1.0f,
                                Animation.RELATIVE_TO_SELF, 0.0f);
                        animation.setDuration(100);
                        search_option.setAnimation(animation);
                    }
                }
            }
        });
        swipeRefreshLayout = rootView.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { // 기존 데이터를 버리고 초기화
            @Override
            public void onRefresh() {
                reviewList.clear();
                userInfos.clear();
                lastVisible = null;
                reviewAdapter.notifyDataSetChanged();
                reviewUpdates();
            }
        });
        reviewUpdates();
        return rootView;
    }

    private void setFilterText(HashMap<CustomDialog.Positions, Boolean> result) {
        positionFilter = result;
        final int filterSize = positionFilter.size();
        String text = "";
        if (filterSize == 7) {
            text = "학교와 혜화역";
        } else if (filterSize == 1) {
            if (positionFilter.containsKey(CustomDialog.Positions.정문) ||
                    positionFilter.containsKey(CustomDialog.Positions.쪽문) ||
                    positionFilter.containsKey(CustomDialog.Positions.철문)) {
                text = "성균관대학교 ";
            }
            for (CustomDialog.Positions position : positionFilter.keySet()) {
                text += position.toString();
            }
        } else {
            if (positionFilter.containsKey(CustomDialog.Positions.정문) && positionFilter.containsKey(CustomDialog.Positions.철문) && positionFilter.containsKey(CustomDialog.Positions.쪽문)) {
                text = "성균관대학교";
            } else if (positionFilter.containsKey(CustomDialog.Positions.대명거리) &&
                    positionFilter.containsKey(CustomDialog.Positions.마로니에) &&
                    positionFilter.containsKey(CustomDialog.Positions.소나무길) &&
                    positionFilter.containsKey(CustomDialog.Positions.혜화로터리)) {
                text = "혜화역";
            } else {
                text = "설정하신 곳";
            }
        }
        selected_position.setText(text);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        final int baseBlack = ContextCompat.getColor(HomeActivity.mContext, R.color.colorBaseBlack);
        final int baseGray = ContextCompat.getColor(HomeActivity.mContext, R.color.baseGray);
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.option_latest:
                    if (!isLatest) {
                        isLatest = true;
                        option_latest.setTextColor(baseBlack);
                        option_latest.setBackgroundResource(R.drawable.select_bottom_underline);
                        option_popular.setTextColor(baseGray);
                        option_popular.setBackgroundResource(R.drawable.unselect_bottom_underline);
                        reviewList.clear();
                        lastVisible = null;
                        reviewUpdates();
                        showToast("최신순으로 정렬해요.");
                    }
                    break ;
                case R.id.option_popular:
                    if (isLatest) {
                        isLatest = false;
                        option_popular.setTextColor(baseBlack);
                        option_latest.setBackgroundResource(R.drawable.unselect_bottom_underline);
                        option_latest.setTextColor(baseGray);
                        option_popular.setBackgroundResource(R.drawable.select_bottom_underline);
                        reviewList.clear();
                        reviewUpdates();
                        showToast("인기순으로 정렬해요.");
                    }
                    break ;
            }
        }
    };

    private OnReviewListener onReviewListener = new OnReviewListener() {
        @Override
        public void onDelete(ReviewInfo reviewInfo) {
            reviewList.remove(reviewInfo);
            reviewAdapter.notifyDataSetChanged();
        }

        @Override
        public void onModify() {}

        @Override
        public void onLike(int position, boolean is_like) {
            reviewList.get(position).like(is_like);
            reviewList.get(position).addLike(HomeActivity.mContext.user.getUid(), is_like);
            reviewAdapter.notifyItemChanged(position);
            if (is_like)
                showToast("좋아요!");
            else
                showToast("좋아요를 취소해요.");
        }
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

    private void reviewUpdates() {
        isUpdating = true;
        final CollectionReference collectionReference = db.collection("reviews");
        final int review_size = reviewList.size();
        Date date = review_size == 0 ? new Date() : reviewList.get(review_size - 1).getCreatedAt();
        if (isLatest) {
            collectionReference.orderBy("createdAt", Query.Direction.DESCENDING)
                    .whereLessThan("createdAt", date).limit(10).get()
                    .addOnCompleteListener(onCompleteListener);
        } else {
            if (lastVisible == null) {
                collectionReference.orderBy("likeNum", Query.Direction.DESCENDING)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        lastVisible = queryDocumentSnapshots.getDocuments()
                                .get(queryDocumentSnapshots.size() - 1);
                    }
                }).addOnCompleteListener(onCompleteListener);
            } else {
                collectionReference.orderBy("likeNum", Query.Direction.DESCENDING)
                        .startAfter(lastVisible)
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() != 0) {
                            lastVisible = queryDocumentSnapshots.getDocuments()
                                    .get(queryDocumentSnapshots.size() - 1);
                        }

                    }
                }).addOnCompleteListener(onCompleteListener);
            }

        }
    }

    private OnCompleteListener<QuerySnapshot> onCompleteListener = new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task) {
            if (task.isSuccessful()) {
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
                                document.getId(),
                                (Map<String, Boolean>)document.get("like"),
                                document.getLong("likeNum"),
                                (ArrayList<String>) document.getData().get("tags"),
                                document.getDouble("rating"),
                                document.getData().get("restaurantId").toString());
                        reviewList.add(review);
                    } else {    // 새로 받은 리뷰의 유저 아이디가 기존 리스트에 존재하지 않음 -> 일단 userinfo는 생략
                        review = new ReviewInfo(
                                document.getData().get("title").toString(),
                                document.getData().get("userComment").toString(),
                                (ArrayList<String>) document.getData().get("photos"),
                                null,
                                writer,
                                new Date(document.getDate("createdAt").getTime()),
                                document.getId(),
                                (Map<String, Boolean>)document.get("like"),
                                document.getLong("likeNum"),
                                (ArrayList<String>) document.getData().get("tags"),
                                document.getDouble("rating"),
                                document.getData().get("restaurantId").toString());
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
    };

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

    public void reviewItemAdded(ReviewInfo reviewInfo) {
        this.reviewList.add(0, reviewInfo);
        this.reviewAdapter.notifyItemInserted(0);
        this.recyclerView.scrollToPosition(0);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
