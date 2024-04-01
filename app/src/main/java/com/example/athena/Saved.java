package com.example.athena;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Saved extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_saved);

        Button newButton = findViewById(R.id.new_item);
        Button searchButton = findViewById(R.id.search);
        Button savedButton = findViewById(R.id.saved);

        View buttonContainer = findViewById(R.id.buttonContainer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            buttonContainer.setPadding(0, 0, 0, getNavigationBarHeight());
        }

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start New.java activity
                Intent intent = new Intent(getApplicationContext(), New.class);
                startActivity(intent);
                finish();
            }
        });
        savedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start New.java activity
                Intent intent = new Intent(getApplicationContext(), Saved.class);
                startActivity(intent);
                finish();
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start New.java activity
                Intent intent = new Intent(getApplicationContext(), SearchPage.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}