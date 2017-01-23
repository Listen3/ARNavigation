package com.soaringnova.novascenic.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.soaringnova.novascenic.R;

/**
 * Created by Be on 2017/1/23.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arnavigation);
    }
}

