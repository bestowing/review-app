package com.bestowing.restaurant;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bestowing.restaurant.home.HomeActivity;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CustomDialog extends Dialog {
    public enum Positions {
        정문, 쪽문, 철문, 마로니에, 대명거리, 소나무길, 혜화로터리
    }

    OnMyDialogResult mDialogResult;

    private ArrayList<Chip> chips;
    private HashMap<Positions, Boolean> mFilter;
    private Context mContext;
    private Positions[] positions;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);
        // 다이얼로그의 배경을 투명으로 만든다.
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.windowAnimations = R.style.AnimationPopupStyle;
            window.setAttributes( params );
        }
        chips = new ArrayList<>();
        chips.add((Chip)findViewById(R.id.chip1));
        chips.add((Chip)findViewById(R.id.chip2));
        chips.add((Chip)findViewById(R.id.chip3));
        chips.add((Chip)findViewById(R.id.chip4));
        chips.add((Chip)findViewById(R.id.chip5));
        chips.add((Chip)findViewById(R.id.chip6));
        chips.add((Chip)findViewById(R.id.chip7));
        for (Positions position : mFilter.keySet()) {
            chips.get(position.ordinal()).setChecked(true);
        }
        positions = Positions.values();
        findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Chip chip : chips) {
                    if (chip.isChecked()) {
                        mFilter.put(positions[chips.indexOf(chip)], true);
                    }
                }
                if( mDialogResult != null ){
                    mDialogResult.finish(mFilter);
                }
            }
        });
    }

    public void setFilters(HashMap<Positions, Boolean> positionFilter) {
        this.mFilter = positionFilter;
    }

    public CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(HashMap<Positions, Boolean> result);
    }
}
