package com.bestowing.restaurant.home.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bestowing.restaurant.R;
import com.bestowing.restaurant.CategoryInfo;
import com.bestowing.restaurant.home.HomeActivity;
import com.bestowing.restaurant.home.adapter.CategoryAdapter;

import java.util.ArrayList;

public class StoreListFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private ArrayList<CategoryInfo> categoryList;

    public StoreListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_store_list, container, false);
        recyclerView = rootView.findViewById(R.id.category_item_view);
        categoryList = new ArrayList<>();
        categoryList.add(new CategoryInfo("혼밥하기 좋은", R.drawable.fastfood, null));
        categoryList.add(new CategoryInfo("분위기 좋은", R.drawable.coffee, null));
        categoryList.add(new CategoryInfo("무한리필이 땡길때", R.drawable.steak, null));
        categoryList.add(new CategoryInfo("데이트 코스로 딱", R.drawable.date, null));
        categoryAdapter = new CategoryAdapter(categoryList, HomeActivity.mContext);
        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.mContext));
        recyclerView.setAdapter(categoryAdapter);
        categoryAdapter.notifyDataSetChanged();
        return rootView;
    }
}