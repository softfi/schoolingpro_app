package com.qdocs.smartschool;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.qdocs.smartschools.R;

import com.github.ybq.android.spinkit.SpinKitView;

public class LoaderView extends LinearLayout {
    private SpinKitView loader;

    public LoaderView(Context context) {
        this(context, null);
    }

    public LoaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoaderView, 0, 0);
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.activity_loader_view, this);
        LinearLayout root = view.findViewById(R.id.loader_root);
        loader = view.findViewById(R.id.spinner);
        root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }
}


