package com.example.firebaseoc.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import butterknife.ButterKnife;

/**
 * Base Activity Class that will allow to manage all the common code for the activities .
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * binding the views like the (text ,button ,image  ,etc )
     */
    private void bindViews(){
        ButterKnife.bind(this);
    }

    /**
     *  that abstract method get the layout resource ID .
     * @return layout resource ID .
     */
    abstract int getLayoutResourceId();

    /**
     * we override the OnCreate methode and the we set the context view and also we bind the views.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        bindViews();
        //throw new RuntimeException("Test Crash");
    }
}
