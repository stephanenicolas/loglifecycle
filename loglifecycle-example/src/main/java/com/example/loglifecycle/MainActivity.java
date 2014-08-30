package com.example.loglifecycle;

import android.app.Activity;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;

import com.github.stephanenicolas.loglifecycle.LogLifeCycle;

@LogLifeCycle
public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }
}
