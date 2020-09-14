package com.bestowing.restaurant.home.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bestowing.restaurant.FirebaseHelper;
import com.bestowing.restaurant.R;
import com.bestowing.restaurant.ReviewInfo;
import com.bestowing.restaurant.home.ReviewDetailActivity;
import com.bestowing.restaurant.home.WriteReviewActivity;
import com.bestowing.restaurant.home.listener.OnReviewListener;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private ArrayList<ReviewInfo> mDataset;
    private Activity activity;
    private FirebaseHelper firebaseHelper;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public ReviewAdapter(Activity activity, ArrayList<ReviewInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;

        firebaseHelper = new FirebaseHelper(activity);
    }

    public void setOnReviewListener(OnReviewListener onReviewListener) {
        firebaseHelper.setOnPostListener(onReviewListener);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        final ViewHolder viewHolder = new ViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getFragmentManager();
                Intent intent = new Intent(activity, ReviewDetailActivity.class);
                intent.putExtra("reviewInfo", mDataset.get(viewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        cardView.findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(view, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView writer_nickname = cardView.findViewById(R.id.writer_nickname);
        CircleImageView writer_profile = cardView.findViewById(R.id.writer_profile);
        writer_nickname.setText(mDataset.get(position).getUserInfo().getNickName());
        if (mDataset.get(position).getUserInfo().getPhotoUrl().equals("")) {
            writer_profile.setImageResource(R.drawable.default_profile);
        } else {
            try {
                Glide.with(activity).load(mDataset.get(position).getUserInfo().getPhotoUrl()).error(R.drawable.default_profile).into(writer_profile);
            } catch (Exception e) {}
        }
        TextView title = cardView.findViewById(R.id.title);
        String _title = mDataset.get(position).getTitle();
        if (_title.length() > 20) {
            String summary = _title.substring(0, 19).concat("...");
            title.setText(summary);
        } else {
            title.setText(_title);
        }

        TextView user_comment = cardView.findViewById(R.id.user_comment);
        String comment = mDataset.get(position).getUserComment();
        if (comment.length() > 30) {
            String summary = comment.substring(0, 30).concat("...");
            user_comment.setText(summary);
        } else {
            user_comment.setText(comment);
        }
        TextView createdAtTextView = cardView.findViewById(R.id.createAtTextView);
        createdAtTextView.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(mDataset.get(position).getCreatedAt()));
        /*
        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentsList = mDataset.get(position).getPhotos();

        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentsList)) {
            contentsLayout.setTag(contentsList);
            contentsLayout.removeAllViews();
            if(contentsList != null) {
                for (int i = 0; i < contentsList.size(); i++ ){
                    String contents = contentsList.get(i);
                    ImageView imageView = new ImageView(activity);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    try {
                        Glide.with(activity).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                    } catch (Exception e) {}
                }
            }
        }

         */
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
                        startNewActivity(WriteReviewActivity.class, mDataset.get(position));
                        return true;
                    case R.id.delete:
                        firebaseHelper.deleteStorage(mDataset.get(position));
                        return true;
                    default:
                        return false;
                }
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.review, popup.getMenu());
        popup.show();
    }

    private void startNewActivity(Class c, ReviewInfo reviewInfo) {
        Intent intent = new Intent(this.activity, c);
        intent.putExtra("reviewInfo", reviewInfo);
        activity.startActivity(intent);
    }
}