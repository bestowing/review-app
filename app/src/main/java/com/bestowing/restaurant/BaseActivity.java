package com.bestowing.restaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

abstract class BaseActivity extends AppCompatActivity {
    private int transitionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}