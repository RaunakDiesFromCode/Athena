package com.example.athena;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SearchPage extends AppCompatActivity {

    FirebaseAuth auth;
    Button logoutBtn;
    TextView textView;
    FirebaseUser user;

    DatabaseReference databaseReference;
    LinearLayout searchResultsLayout;
    ScrollView searchResultsScrollView;
    EditText searchWord;
    private String previousSearchTerm = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_page);

        auth = FirebaseAuth.getInstance();
        logoutBtn = findViewById(R.id.logout);
        searchWord = findViewById(R.id.searchTxt);
        searchResultsScrollView = findViewById(R.id.searchResultsScrollView);
        databaseReference = FirebaseDatabase.getInstance().getReference("pdf");

        Button newButton = findViewById(R.id.new_item);
        Button searchButton = findViewById(R.id.search);
        Button savedButton = findViewById(R.id.saved);
        Button searchTxtBtn = findViewById(R.id.uploadPdfBtn);

        View buttonContainer = findViewById(R.id.buttonContainer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            buttonContainer.setPadding(0, 0, 0, getNavigationBarHeight());
        }


// Create a TextWatcher to monitor changes in the search term EditText
        searchWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Whenever the text changes, hide recent searches layout
                LinearLayout recentSearchesLayout = findViewById(R.id.searchResultsLayout);
                searchResultsScrollView.setVisibility(View.GONE);
                recentSearchesLayout.setVisibility(View.VISIBLE);
            }
        });

        searchTxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScrollView searchResultsScrollView = findViewById(R.id.searchResultsScrollView);
                LinearLayout recentSearchesLayout = findViewById(R.id.searchResultsLayout);
                ProgressBar progressBar = findViewById(R.id.progressBar);

                String currentSearchTerm = searchWord.getText().toString().trim();

                // Check if the search term is empty
                if (currentSearchTerm.isEmpty()) {
                    Toast.makeText(SearchPage.this, "Write something to search at least...", Toast.LENGTH_SHORT).show();
                } else {
                    LinearLayout btngrp = findViewById(R.id.searchResultsLayout);
                    searchResultsScrollView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    btngrp.setVisibility(View.GONE);

                    // Update previousSearchTerm
                    previousSearchTerm = currentSearchTerm;

                    // Populate search results
                    populateSearchResults();
                }
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

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

    private void populateSearchResults() {
        fetchSearchResultsFromFirebase();
    }

    private void fetchSearchResultsFromFirebase() {
        String wrd = searchWord.getText().toString().trim();
        ProgressBar progressBar = findViewById(R.id.progressBar);
        LinearLayout btngrp = findViewById(R.id.searchResultsLayout);
        Log.d(null, "fetchSearchResultsFromFirebase: search term = " + wrd);
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("pdf");

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> searchResults = new ArrayList<>();

                // Iterate through the data snapshot to fetch search results
                for (DataSnapshot pdfSnapshot : dataSnapshot.getChildren()) {
                    String pdfTitle = pdfSnapshot.child("pdfTitle").getValue(String.class);
                    String pdfTags = pdfSnapshot.child("pdfTags").getValue(String.class);

                    // Check if the PDF tags contain the searched tag
                    if (pdfTags != null && pdfTags.contains(wrd)) {
                        searchResults.add(pdfTitle);
                    }
                }

                // Once all search results are fetched, populate the UI with them
                displaySearchResults(searchResults);
                progressBar.setVisibility(View.GONE);
                btngrp.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void displaySearchResults(ArrayList<String> searchResults) {
        LinearLayout searchResultsLayout = findViewById(R.id.searchResultsLayout);
        searchResultsLayout.removeAllViews();

        for (String result : searchResults) {
            // Create a new button for each search result
            Button button = new Button(SearchPage.this);
            button.setText(result);
            button.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            // Attach an OnClickListener to the button
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String pdfTitle = ((Button) v).getText().toString();
//
//                    // Fetch the pdfTitleFull from Firebase based on the searched pdfTitle
//                    databaseReference.orderByChild("pdfTitle").equalTo(pdfTitle).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            // Iterate through the dataSnapshot to find the matching pdfTitle
//                            for (DataSnapshot pdfSnapshot : dataSnapshot.getChildren()) {
//                                // Get the value of pdfTitleFull
//                                String pdfTitleFull = pdfSnapshot.child("pdfTitleFull").getValue(String.class);
//
//                                // Call a method to initiate the download process
//                                downloadPDF(pdfTitleFull);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            // Handle error
//                            Toast.makeText(SearchPage.this, "Failed to fetch PDF details", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            });
//            searchResultsLayout.addView(button);

            button.setOnClickListener(new View.OnClickListener() {
                //                @Override
//                public void onClick(View v) {
//                    String pdfTitle = ((Button) v).getText().toString();
//
//                    // Start PdfPreviewActivity and pass the PDF title
//                    Intent intent = new Intent(SearchPage.this, PdfPreview.class);
//                    intent.putExtra("pdfTitle", pdfTitle);
//                    startActivity(intent);
//                }
                @Override
                public void onClick(View v) {
                    String pdfTitle = ((Button) v).getText().toString();

                    // Fetch the pdfTitleFull from Firebase based on the searched pdfTitle
                    databaseReference.orderByChild("pdfTitle").equalTo(pdfTitle).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Iterate through the dataSnapshot to find the matching pdfTitle
                            for (DataSnapshot pdfSnapshot : dataSnapshot.getChildren()) {
                                // Get the value of pdfTitleFull
                                String pdfTitleFull = pdfSnapshot.child("pdfTitleFull").getValue(String.class);

                                // Start PdfPreviewActivity and pass the PDF title and pdfTitleFull
                                Intent intent = new Intent(SearchPage.this, PdfPreview.class);
                                intent.putExtra("pdfTitle", pdfTitle);
                                intent.putExtra("pdfTitleFull", pdfTitleFull);
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle error
                            Toast.makeText(SearchPage.this, "Failed to fetch PDF details", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            searchResultsLayout.addView(button);
        }

        // Make the ScrollView containing search results visible
        ScrollView searchResultsScrollView = findViewById(R.id.searchResultsScrollView);
        searchResultsScrollView.setVisibility(View.VISIBLE);
    }


    private void downloadPDF(String fileName) {
        Toast.makeText(SearchPage.this, "Download started...", Toast.LENGTH_SHORT).show();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference pdfRef = storageRef.child("pdf/" + (fileName + ".pdf"));
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File localFile = new File(downloadsDir, fileName + ".pdf");
        pdfRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SearchPage.this, "File downloaded successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Log the exception for debugging purposes
                Log.e("DownloadError", "Failed to download file", exception);
                // Handle any errors
                Toast.makeText(SearchPage.this, "Failed to download file", Toast.LENGTH_SHORT).show();
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
