package com.bestowing.restaurant.home.listener;

import com.bestowing.restaurant.ReviewInfo;

public interface OnReviewListener {
    void onDelete(ReviewInfo reviewInfo);
    void onModify();
    void onLike(int position, boolean is_like);
}