package com.bestowing.restaurant.home.listener;

import com.bestowing.restaurant.CommentInfo;

public interface OnCommentListener {
    void onDelete(CommentInfo commentInfo);
    void onModify();
    void onLike(int position, boolean is_like);
}
