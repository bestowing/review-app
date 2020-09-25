package com.bestowing.restaurant.home.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bestowing.restaurant.CommentInfo;
import com.bestowing.restaurant.FirebaseHelper;
import com.bestowing.restaurant.R;
import com.bestowing.restaurant.Utility;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private ArrayList<CommentInfo> mDataset;
    private Activity activity;
    private FirebaseHelper firebaseHelper;
    private Utility utility;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public CommentAdapter(ArrayList<CommentInfo> mDataset, Activity activity) {
        this.mDataset = mDataset;
        this.activity = activity;
        this.firebaseHelper = new FirebaseHelper(activity);
        this.utility = new Utility();
    }

    /*
    public void setOnCommentListener(OnCommentListener onCommentListener) {
        firebaseHelper.setOnCommentListener(onCommentListener);
    }
     */

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
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.modify:
                        //startNewActivity(WriteReviewActivity.class, mDataset.get(position));
                        return true;
                    case R.id.delete:
                        //firebaseHelper.deleteStorage(mDataset.get(position));
                        return true;
                    default:
                        return false;
                }
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.my_review, popup.getMenu());
        popup.show();
    }
}
