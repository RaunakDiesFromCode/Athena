package com.example.athena;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//
//import com.google.firebase.database.ValueEventListener;
//
//public class AdminMainActivity extends AppCompatActivity {
//
//    private DatabaseReference databaseReference;
//    Button logoutBtn;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }
//
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_admin_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        View buttonContainer = findViewById(R.id.buttonContainer);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            buttonContainer.setPadding(0, 0, 0, getNavigationBarHeight());
//        }
//
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("pdf");
//        logoutBtn = findViewById(R.id.logout);
//        ScrollView mainLayout = findViewById(R.id.scroller);
//
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        addDynamicLinearLayouts();
//
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    boolean verified = snapshot.child("verified").getValue(Boolean.class);
//                    if (!verified) {
//                        String pdfTitle = snapshot.child("pdfTitle").getValue(String.class);
//                        //method to create all the buttons
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle errors
//            }
//        });
//
//    }
//
//    private void addDynamicLinearLayouts() {
//        LinearLayout mainLayout = findViewById(R.id.mainLayout); // assuming you have a LinearLayout in your activity layout to hold the dynamic layouts
//
//        LayoutInflater inflater = LayoutInflater.from(this);
//        for (int i = 0; i < 4; i++) {
//            View dynamicLayout = inflater.inflate(R.layout.dynamic_linear_layout, mainLayout, false);
//            // Customize dynamicLayout if needed
//            mainLayout.addView(dynamicLayout);
//        }
//    }
//
//
//
//
//
//    private int getNavigationBarHeight() {
//        Resources resources = getResources();
//        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            return resources.getDimensionPixelSize(resourceId);
//        }
//        return 0;
//    }
//
//}

public class AdminMainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    Button logoutBtn;
    String ID; // Get the ID of the PDF
    String pdfUrl;
    String pdfTitleFull;
    String uploader;
//    String saved;
    String pdfTags; // Retrieve pdfTags data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        View buttonContainer = findViewById(R.id.buttonContainer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            buttonContainer.setPadding(0, 0, 0, getNavigationBarHeight());
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("pdf");
        logoutBtn = findViewById(R.id.logout);
        ScrollView mainLayout = findViewById(R.id.scroller);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LinearLayout mainLayout = findViewById(R.id.mainLayout); // assuming you have a LinearLayout in your activity layout to hold the dynamic layouts
                mainLayout.removeAllViews(); // Clear existing views

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    pdfUrl = snapshot.child("pdfUrl").getValue(String.class);

                    pdfTitleFull = snapshot.child("pdfTitleFull").getValue(String.class);

                    pdfTags = snapshot.child("pdfTags").getValue(String.class);

                    ID = snapshot.getKey();

                    uploader = snapshot.child("uploader").getValue(String.class);

                    boolean verified = snapshot.child("verified").getValue(Boolean.class);
                    if (!verified) {
                        String pdfTitle = snapshot.child("pdfTitle").getValue(String.class);
                        String pdfKey = snapshot.getKey();

                        createPdfButton(mainLayout, pdfTitle, pdfKey);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }

    private void createPdfButton(LinearLayout mainLayout, String pdfTitle, String pdfKey) {
        LayoutInflater inflater = LayoutInflater.from(this);

        // Inflate the custom layout for the PDF item
        View dynamicLayout = inflater.inflate(R.layout.dynamic_linear_layout, mainLayout, false);

        // Find the buttons in the custom layout
        Button nameButton = dynamicLayout.findViewById(R.id.nameButton);
        Button tickButton = dynamicLayout.findViewById(R.id.tickButton);

        // Set PDF title to the name button
        nameButton.setText(pdfTitle);

        // Set onClickListener for the name button (optional)
        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event of the name button
                // For example, you can open the PDF or perform any other action
                Intent intent = new Intent(getApplicationContext(), AdminPdfPreview.class);
                Log.d(null, "onClick: pdf title = " + pdfTitle);
                Log.d(null, "onClick: pdf url = " + pdfUrl);
                intent.putExtra("pdfTitle", pdfTitle);
                intent.putExtra("URL", pdfUrl);
                intent.putExtra("ID", ID);
                intent.putExtra("pdfTitleFull", pdfTitleFull);
                intent.putExtra("uploader", uploader);
                intent.putExtra("pdfTags", pdfTags);
                startActivity(intent);
                Log.d(null, "onClick: PDf preview button clicked");
            }
        });

        // Set onClickListener for the tick button
        tickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event of the tick button
                // For example, you can mark the PDF as verified in the database
//                markPdfAsVerified(pdfKey);
                Log.d(null, "onClick: PDf check button clicked");
                databaseReference.child(pdfKey).child("verified").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean currentStatus = dataSnapshot.getValue(Boolean.class);
                        databaseReference.child(pdfKey).child("verified").setValue(!currentStatus); // Toggle the value
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                    }
                });
            }
        });

        // Add the custom layout to the main layout
        mainLayout.addView(dynamicLayout);
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
