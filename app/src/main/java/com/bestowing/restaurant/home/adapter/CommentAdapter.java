package com.bestowing.restaurant.home.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bestowing.restaurant.CommentInfo;
import com.bestowing.restaurant.FirebaseHelper;
import com.bestowing.restaurant.R;
import com.bestowing.restaurant.ReviewInfo;
import com.bestowing.restaurant.Utility;
import com.bestowing.restaurant.home.WriteReviewActivity;
import com.bestowing.restaurant.home.listener.OnCommentListener;
import com.bestowing.restaurant.home.listener.OnReviewListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private ArrayList<CommentInfo> mDataset;
    private Activity activity;
    private ReviewInfo reviewInfo;
    private String myId;
    private FirebaseHelper firebaseHelper;
    private Utility utility;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public CommentAdapter(ArrayList<CommentInfo> mDataset, Activity activity, ReviewInfo reviewInfo, String myId) {
        this.mDataset = mDataset;
        this.activity = activity;
        this.reviewInfo = reviewInfo;
        this.myId = myId;
        this.utility = new Utility();
        this.firebaseHelper = new FirebaseHelper(activity);
    }

    public void setOnCommentListener(OnCommentListener onCommentListener) {
        firebaseHelper.setOnCommentListener(onCommentListener);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comments, parent, false);
        final CommentAdapter.ViewHolder viewHolder = new CommentAdapter.ViewHolder(cardView);
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopup(v, viewHolder.getAdapterPosition());
                return false;
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView nickname = cardView.findViewById(R.id.nickname);
        CircleImageView profile = cardView.findViewById(R.id.profile);
        nickname.setText(mDataset.get(position).getUserInfo().getNickName());
        if (mDataset.get(position).getUserInfo().getPhotoUrl().equals("")) {
            profile.setImageResource(R.drawable.default_profile);
        } else {
            try {
                Glide.with(activity).load(mDataset.get(position).getUserInfo().getPhotoUrl()).error(R.drawable.default_profile).into(profile);
            } catch (Exception e) {}
        }
        TextView contentView = cardView.findViewById(R.id.content);
        String content = mDataset.get(position).getContent();
        contentView.setText(content);
        TextView createdAtTextView = cardView.findViewById(R.id.createAtTextView);
        createdAtTextView.setText(utility.calculateTimeStamp(mDataset.get(position).getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showPopup(View v, final int position) {
        //MY_REVIEW_OPT
        PopupMenu popup = new PopupMenu(activity, v);
        if (mDataset.get(position).getWriter().equals(myId)) {
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.modify:
                            //startNewActivity(WriteReviewActivity.class, mDataset.get(position));
                            Toast.makeText(activity, "댓글 수정은 아직 지원하지 않아요.", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.delete:
                            firebaseHelper.deleteStorage(reviewInfo, mDataset.get(position));
                            return true;
                        case R.id.report:
                            Toast.makeText(activity, "신고가 접수되었어요.", Toast.LENGTH_SHORT).show();
                            return true;
                        default:
                            return false;
                    }
                }
            });
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.my_review, popup.getMenu());
            popup.show();
        } else {
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.report:
                            Toast.makeText(activity, "신고가 접수되었어요.", Toast.LENGTH_SHORT).show();
                            return true;
                        default:
                            return false;
                    }
                }
            });
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.other_review, popup.getMenu());
            popup.show();
        }
    }
}
