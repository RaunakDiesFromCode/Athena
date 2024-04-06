//package com.example.athena;
//
//import android.content.Intent;
//import android.content.res.Resources;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.LinearLayout;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class Saved extends AppCompatActivity {
//
//    private DatabaseReference databaseReference;
//    private FirebaseUser currentUser;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Enable edge-to-edge display
//        EdgeToEdge.enable(this);
//
//        // Make status bar transparent
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        }
//
//        setContentView(R.layout.activity_saved);
//
//        // Initialize Firebase
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        currentUser = auth.getCurrentUser();
//        databaseReference = FirebaseDatabase.getInstance().getReference("pdf");
//
//        Button newButton = findViewById(R.id.new_item);
//        Button searchButton = findViewById(R.id.search);
//        Button savedButton = findViewById(R.id.saved);
//
//        // Apply navigation bar padding adjustment
//        View buttonContainer = findViewById(R.id.buttonContainer);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            buttonContainer.setPadding(0, 0, 0, getNavigationBarHeight());
//        }
//
//        LinearLayout savedView = findViewById(R.id.savedView);
//
//        retrieveSavedPDFs(savedView);
//
//        // Assuming you have the dynamic count of buttons stored in a variable
////        int buttonCount = 4;
////
////        for (int i = 0; i < 4; i++) {
////            // Create a new Button
////            Button button = new Button(this);
////
////            // Set button properties
////            button.setLayoutParams(new LinearLayout.LayoutParams(
////                    LinearLayout.LayoutParams.MATCH_PARENT,
////                    LinearLayout.LayoutParams.WRAP_CONTENT
////            ));
////            button.setText("Button " + (i + 1));
////            button.setPadding(0,10, 0,10);
////
////            // Add the button to the savedView layout
////            savedView.addView(button);
////        }
//
//        newButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start New.java activity
//                Intent intent = new Intent(getApplicationContext(), New.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//        savedButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start New.java activity
//                Intent intent = new Intent(getApplicationContext(), Saved.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start New.java activity
//                Intent intent = new Intent(getApplicationContext(), SearchPage.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//    }
//
//    private void retrieveSavedPDFs(LinearLayout savedView) {
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot pdfSnapshot : dataSnapshot.getChildren()) {
//                    String savedEmails = pdfSnapshot.child("saved").getValue(String.class);
//                    if (savedEmails != null && savedEmails.contains(currentUser.getEmail())) {
//                        String pdfTitle = pdfSnapshot.child("pdfTitle").getValue(String.class);
//                        String pdfUrl = pdfSnapshot.child("pdfUrl").getValue(String.class);
//                        createPDFButton(savedView, pdfTitle, pdfUrl);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle error
//            }
//        });
//    }
//
//    // Create a button for the saved PDF
//    private void createPDFButton(LinearLayout savedView, String pdfTitle, String pdfUrl) {
//        if (pdfTitle != null && pdfUrl != null) {
//            Button button = new Button(this);
//            button.setLayoutParams(new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT
//            ));
//            button.setText(pdfTitle);
//            button.setPadding(20, 20, 20, 20); // Set padding
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getApplicationContext(), PdfPreview.class);
//                    Log.d(null, "onClick: pdf title = "+pdfTitle);
//                    Log.d(null, "onClick: pdf url = "+pdfUrl);
//                    if (pdfTitle != null && pdfUrl != null && ID != null && pdfTitleFull != null && saved != null) {
//                        intent.putExtra("pdfTitle", pdfTitle);
//                        intent.putExtra("URL", pdfUrl);
//                        intent.putExtra("ID", ID);
//                        intent.putExtra("pdfTitleFull", pdfTitleFull);
//                        intent.putExtra("saved", saved);
//                        startActivity(intent);
//                    } else {
//                        Log.e("Saved", "Some necessary data is null");
//                        // Handle null values appropriately, such as showing an error message to the user
//                    }
//                }
//            });
//            savedView.addView(button);
//        } else {
//            Log.e("Saved", "PDF title or URL is null");
//        }
//    }
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
//}


package com.example.athena;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Saved extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);

        // Make status bar transparent
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_saved);

        // Initialize Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("pdf");

        Button newButton = findViewById(R.id.new_item);
        Button searchButton = findViewById(R.id.search);
        Button savedButton = findViewById(R.id.saved);

        // Apply navigation bar padding adjustment
        View buttonContainer = findViewById(R.id.buttonContainer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            buttonContainer.setPadding(0, 0, 0, getNavigationBarHeight());
        }

        LinearLayout savedView = findViewById(R.id.savedView);

        retrieveSavedPDFs(savedView);

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
                // Start Saved.java activity (this activity)
                // No need to start the same activity again
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start SearchPage.java activity
                Intent intent = new Intent(getApplicationContext(), SearchPage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void retrieveSavedPDFs(LinearLayout savedView) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot pdfSnapshot : dataSnapshot.getChildren()) {
                    String savedEmails = pdfSnapshot.child("saved").getValue(String.class);
                    if (savedEmails != null && savedEmails.contains(currentUser.getEmail())) {
                        String pdfTitle = pdfSnapshot.child("pdfTitle").getValue(String.class);
                        String pdfUrl = pdfSnapshot.child("pdfUrl").getValue(String.class);
                        String pdfTitleFull = pdfSnapshot.child("pdfTitleFull").getValue(String.class);
                        String saved = pdfSnapshot.child("saved").getValue(String.class);
                        String pdfTags = pdfSnapshot.child("pdfTags").getValue(String.class); // Retrieve pdfTags data
                        String ID = pdfSnapshot.getKey(); // Get the ID of the PDF
                        createPDFButton(savedView, pdfTitle, pdfUrl, pdfTitleFull, saved, ID, pdfTags);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    // Create a button for the saved PDF
    private void createPDFButton(LinearLayout savedView, String pdfTitle, String pdfUrl, String pdfTitleFull, String saved, String ID, String pdfTags) {
        if (pdfTitle != null && pdfUrl != null && pdfTitleFull != null && saved != null && ID != null && pdfTags != null) {
            Button button = new Button(this);
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            button.setText(pdfTitle);
            button.setPadding(20, 20, 20, 20); // Set padding
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), PdfPreview.class);
                    Log.d(null, "onClick: pdf title = " + pdfTitle);
                    Log.d(null, "onClick: pdf url = " + pdfUrl);
                    intent.putExtra("pdfTitle", pdfTitle);
                    intent.putExtra("URL", pdfUrl);
                    intent.putExtra("ID", ID);
                    intent.putExtra("pdfTitleFull", pdfTitleFull);
                    intent.putExtra("saved", saved);
                    intent.putExtra("pdfTags", pdfTags);
                    startActivity(intent);
                }
            });
            savedView.addView(button);
        } else {
            Log.e("Saved", "Some necessary data is null");
            // Handle null values appropriately, such as showing an error message to the user
        }
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
