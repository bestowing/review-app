package com.bestowing.restaurant.home.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bestowing.restaurant.R;
import com.bestowing.restaurant.TestActivity;
import com.bestowing.restaurant.home.HomeActivity;

import net.daum.mf.map.api.MapView;

public class StoreListFragment extends Fragment {
    public StoreListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_store_list, container, false);
        //MapView mapView = new MapView(HomeActivity.mContext);

        //ViewGroup mapViewContainer = (ViewGroup) rootView.findViewById(R.id.map_view);
        //mapViewContainer.addView(mapView);
        rootView.findViewById(R.id.test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.mContext, TestActivity.class);
                startActivity(intent);
            }
        });
        return rootView;
    }
}