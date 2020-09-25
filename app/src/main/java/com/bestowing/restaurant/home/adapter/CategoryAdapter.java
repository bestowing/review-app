package com.bestowing.restaurant.home.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bestowing.restaurant.CategoryInfo;
import com.bestowing.restaurant.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private ArrayList<CategoryInfo> mDataset;
    private Activity activity;

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public CategoryAdapter(ArrayList<CategoryInfo> mDataset, Activity activity) {
        this.mDataset = mDataset;
        this.activity = activity;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.view_category, parent, false);
        final ViewHolder viewHolder = new ViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "하이루", Toast.LENGTH_SHORT).show();
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        final ImageView category_img = cardView.findViewById(R.id.category_img);
        final TextView category_title = cardView.findViewById(R.id.category_title);
        category_img.setImageResource(mDataset.get(position).getCategoryImage());
        category_title.setText(mDataset.get(position).getCategoryTitle());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
