package com.example.athena;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
import java.util.HashSet;
import java.util.Set;

public class SearchPage extends AppCompatActivity {

    FirebaseAuth auth;
    Button logoutBtn;
    TextView textView;
    FirebaseUser user;

    DatabaseReference databaseReference;
    DatabaseReference recentSearchesRef;
    LinearLayout searchResultsLayout;
    ScrollView searchResultsScrollView;
    EditText searchWord;
    private String previousSearchTerm = "";

    private ArrayList<String> recentSearches = new ArrayList<>(3); // Holds the last 3 search terms
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_page);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Load recent searches from SharedPreferences
        loadRecentSearches();

        // Update the UI to reflect recent searches
        updateRecentSearchButtons();


        auth = FirebaseAuth.getInstance();
        logoutBtn = findViewById(R.id.logout);
        searchWord = findViewById(R.id.searchTxt);
        searchResultsScrollView = findViewById(R.id.searchResultsScrollView);
        databaseReference = FirebaseDatabase.getInstance().getReference("pdf");
        recentSearchesRef = FirebaseDatabase.getInstance().getReference("recent_searches");



        Button newButton = findViewById(R.id.new_item);
        Button searchButton = findViewById(R.id.search);
        Button savedButton = findViewById(R.id.saved);
        Button searchTxtBtn = findViewById(R.id.uploadPdfBtn);

        searchWord.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) { // Check for the search action
                    // Perform search action here
                    Button searchPDFButton = findViewById(R.id.uploadPdfBtn);
                    searchPDFButton.performClick();
                    return true;
                }
                return false;
            }
        });

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);



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
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Start New.java activity
//                Intent intent = new Intent(getApplicationContext(), SearchPage.class);
//                startActivity(intent);
//                finish();
//            }
//        });

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

                    // Update recent searches
                    updateRecentSearches(currentSearchTerm); // Call updateRecentSearches here
                }
            }
        });

    }

    // Load recent searches from SharedPreferences
    // Load recent searches from SharedPreferences
    private void loadRecentSearches() {
        Set<String> savedSearches = sharedPreferences.getStringSet("recent_searches", null);
        if (savedSearches != null) {
            recentSearches.addAll(savedSearches);
        }
    }

    // Save recent searches to SharedPreferences
    private void saveRecentSearches() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("recent_searches", new HashSet<>(recentSearches));
        editor.apply();
    }

    // Update recent searches list and SharedPreferences
    private void updateRecentSearches(String searchTerm) {
        if (!recentSearches.contains(searchTerm.toLowerCase())) {
            // Remove the oldest search term if the list has reached its maximum size
            if (recentSearches.size() == 3) {
                recentSearches.remove(0);
            }
            // Add the new search term
            recentSearches.add(searchTerm);

            // Save recent searches
            saveRecentSearches();

            // Update the UI to reflect recent searches
            updateRecentSearchButtons();
        }
    }

    private void updateRecentSearchButtons() {
        // Clear existing recent search buttons
        clearRecentSearchButtons();

        // Update the text of the recent search buttons based on the recentSearches list
        for (int i = 0; i < recentSearches.size(); i++) {
            String searchTerm = recentSearches.get(i);
            int buttonId = getResources().getIdentifier("recent_searches_" + (i + 1), "id", getPackageName());
            Button button = findViewById(buttonId);
            button.setText(searchTerm);
            button.setVisibility(View.VISIBLE); // Ensure the button is visible

            // Set OnClickListener for the button
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Set the button's text to the EditText view
                    searchWord.setText(searchTerm);

                    // Perform search action by programmatically clicking the search button
                    Button searchPDFButton = findViewById(R.id.uploadPdfBtn);
                    searchPDFButton.performClick();
                }
            });
        }
    }

    private void clearRecentSearchButtons() {
        // Clear text and hide all recent search buttons
        for (int i = 0; i < 3; i++) {
            int buttonId = getResources().getIdentifier("recent_searches_" + (i + 1), "id", getPackageName());
            Button button = findViewById(buttonId);
            button.setText("");
            button.setVisibility(View.GONE);
        }
    }

    private void populateSearchResults() {
        fetchSearchResultsFromFirebase();
    }

    private void fetchSearchResultsFromFirebase() {
        String wrd = searchWord.getText().toString().trim().toLowerCase();
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
                    if (pdfTags != null && pdfTags.toLowerCase().contains(wrd)) {
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
                            String pdfTitleFull,saved, Tags = "", url = "";
                            // Iterate through the dataSnapshot to find the matching pdfTitle
                            for (DataSnapshot pdfSnapshot : dataSnapshot.getChildren()) {
                                // Get the value of pdfTitleFull
                                pdfTitleFull = pdfSnapshot.child("pdfTitleFull").getValue(String.class);
                                saved = pdfSnapshot.child("saved").getValue(String.class);
                                Tags = pdfSnapshot.child("pdfTags").getValue(String.class);
                                url = pdfSnapshot.child("pdfUrl").getValue(String.class);
                                String pdfId = pdfSnapshot.getKey();

                                // Start PdfPreviewActivity and pass the PDF title and pdfTitleFull
                                Intent intent = new Intent(SearchPage.this, PdfPreview.class);
                                intent.putExtra("pdfTitle", pdfTitle);
                                intent.putExtra("pdfTitleFull", pdfTitleFull);
                                intent.putExtra("Tags", Tags);
                                intent.putExtra("saved", saved);
                                intent.putExtra("URL", url);
                                intent.putExtra("ID", pdfId);
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


    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }
}