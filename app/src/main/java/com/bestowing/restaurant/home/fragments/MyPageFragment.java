package com.bestowing.restaurant.home.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bestowing.restaurant.R;
import com.bestowing.restaurant.UserInfo;
import com.bestowing.restaurant.home.EditInfoActivity;
import com.bestowing.restaurant.home.HomeActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPageFragment extends Fragment {
    private final int REQUEST_UPDATE_INFO = 2;

    public MyPageFragment() {}
    private UserInfo myInfo;

    private CircleImageView user_profile_photo;
    private TextView user_nickname;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_my_page, container, false);

        user_profile_photo = rootView.findViewById(R.id.user_profile_photo);
        user_nickname = rootView.findViewById(R.id.user_nickname);

        rootView.findViewById(R.id.edit_info_btn).setOnClickListener(onClickListener);
        rootView.findViewById(R.id.logout_btn).setOnClickListener(onClickListener);
        myInfo = null;
        try {
            myInfo = ((HomeActivity)getActivity()).getMyInfo();
        } catch (Exception ignored) {}
        setViewFromMyInfo();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        setViewFromMyInfo();
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logout_btn:
                    ((HomeActivity)getActivity()).logout();
                    break;
                case R.id.edit_info_btn:
                    startNewActivityForResult(EditInfoActivity.class);
                    break;
            }
        }
    };

    private void setViewFromMyInfo() {
        if (this.myInfo != null) {
            user_nickname.setText(this.myInfo.getNickName());
            Uri uri = null;
            try {
                uri = Uri.parse(this.myInfo.getPhotoUrl());
            } catch (Exception ignored) {}
            Glide.with((HomeActivity)HomeActivity.mContext).load(uri).error(R.drawable.default_profile).into(user_profile_photo);
        }
    }

    private void startNewActivityForResult(Class c) {
        Intent intent = new Intent(HomeActivity.mContext, c);
        intent.putExtra("myInfo", myInfo);
        startActivityForResult(intent, REQUEST_UPDATE_INFO);
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
