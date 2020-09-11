package com.bestowing.restaurant.home.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bestowing.restaurant.R;
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

    public MyPageFragment() {}

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
        setUserInfo();
        return rootView;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.logout_btn:
                    ((HomeActivity)HomeActivity.mContext).logout();
                    break;
                case R.id.edit_info_btn:
                    startNewActivity(EditInfoActivity.class);
                    break;
            }
        }
    };

    private void setUserInfo() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userRef = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        userRef.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String photoUrl = null;
                    Uri uri = null;
                    try {
                        photoUrl = document.get("photoUrl").toString();
                        uri = Uri.parse(photoUrl);
                    } catch (Exception ignored) {}
                    user_nickname.setText(document.get("nickName").toString());
                    Glide.with((HomeActivity)HomeActivity.mContext).load(uri).error(R.drawable.default_profile).into(user_profile_photo);
                }
            }
        });
    }

    public void changeInfo() {
        setUserInfo();
    }

    private void startNewActivity(Class c) {
        Intent intent = new Intent(getContext(), c);
        startActivity(intent);
    }
}
