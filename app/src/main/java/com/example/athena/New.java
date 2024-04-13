package com.example.athena;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;

public class New extends AppCompatActivity {

    Button btSelect;
    private Uri pdfData;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ActivityResultLauncher<Intent> launcher;
    private String pdfName;

    private ProgressBar progressBarCircular;

    private String userEmail;

    String pdfNameFull;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_new);

        Button uploadPdfBtn = findViewById(R.id.uploadPdfBtn);
        EditText tags = findViewById(R.id.tags);
        btSelect = findViewById(R.id.searchTxt);
        Button newButton = findViewById(R.id.new_item);
        Button searchButton = findViewById(R.id.search);
        Button savedButton = findViewById(R.id.saved);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();

        progressBarCircular = findViewById(R.id.progressBarCircular);

        View buttonContainer = findViewById(R.id.buttonContainer);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            buttonContainer.setPadding(0, 0, 0, getNavigationBarHeight());
        }

        uploadPdfBtn.setOnClickListener(v -> {
            String pdfTags = tags.getText().toString();
            if (pdfTags.isEmpty()) {
//                tags.setError("");
                Toast.makeText(this, "Tags cannot be empty", Toast.LENGTH_SHORT).show();
                tags.requestFocus();
            } else if (pdfData == null) {
                Toast.makeText(New.this, "Where's the PDF?", Toast.LENGTH_SHORT).show();
            } else {
                uploadPdf();
            }
        });

        btSelect.setOnClickListener(v -> openGallery());

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            pdfData = data.getData();
                            if (pdfData != null) {
                                handlePdfData();
                            }
                        }
                    }
                }
        );
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

    private void uploadPdf() {
        progressBarCircular.setVisibility(View.VISIBLE);
        pdfNameFull = pdfName + "-" + System.currentTimeMillis();
        StorageReference reference = storageReference.child("pdf/" +pdfNameFull + ".pdf");
        reference.putFile(pdfData).addOnSuccessListener(taskSnapshot -> {
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            uriTask.addOnSuccessListener(uri -> {
                progressBarCircular.setVisibility(View.GONE);
                uploadData(uri.toString());
            });
        }).addOnFailureListener(e -> {
            progressBarCircular.setVisibility(View.GONE);
            Toast.makeText(New.this, "Something's wrong there...", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadData(String downloadUrl) {
        EditText tags = findViewById(R.id.tags);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Get the user's emails
            userEmail = currentUser.getEmail();
        }
        Log.d(null, "uploadData: uploader = "+userEmail);
        String uniqueKey = databaseReference.child("pdf").push().getKey();

        HashMap<String, Object> data = new HashMap<>();



        data.put("uploader", userEmail);
        data.put("pdfTitle", pdfName);
        data.put("pdfTitleFull", pdfNameFull);
        data.put("pdfUrl", downloadUrl);
        data.put("pdfTags", tags.getText().toString());
        data.put("verified", false);
        data.put("saved", "");

        Log.d(null, "uploadData: uploader: "+userEmail);

        databaseReference.child("pdf").child(uniqueKey).setValue(data)
                .addOnSuccessListener(aVoid -> Toast.makeText(New.this, "Yay!! Your notes are uploaded", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(New.this, "Failed to upload PDF", Toast.LENGTH_SHORT).show());
        btSelect.setText("Click to Upload PDF");
        tags.setText("");
    }

//    private void uploadData(String downloadUrl) {
//        EditText tags = findViewById(R.id.tags);
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            // Get the user's email
//            String userEmail = currentUser.getEmail();
//            String uniqueKey = databaseReference.child("pdf").push().getKey();
//
//            // Retrieve the current value of "saved" from the database
//            databaseReference.child("pdf").child(uniqueKey).child("saved").addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    String currentSavedValue = dataSnapshot.getValue(String.class);
//                    if (currentSavedValue == null) {
//                        currentSavedValue = ""; // Initialize to empty string if it's null
//                    }
//
//                    // Concatenate the new user's email with the existing value of "saved", separated by a space
//                    String updatedSavedValue = currentSavedValue.isEmpty() ? userEmail : currentSavedValue + " " + userEmail;
//
//                    // Prepare data to be uploaded
//                    HashMap<String, Object> data = new HashMap<>();
//                    data.put("pdfTitle", pdfName);
//                    data.put("pdfTitleFull", pdfNameFull);
//                    data.put("pdfUrl", downloadUrl);
//                    data.put("pdfTags", tags.getText().toString());
//                    data.put("verified", false);
//                    data.put("saved", updatedSavedValue);
//
//                    // Upload the data to the database
//                    databaseReference.child("pdf").child(uniqueKey).setValue(data)
//                            .addOnSuccessListener(aVoid -> {
//                                Toast.makeText(New.this, "Yay!! Your notes are uploaded", Toast.LENGTH_SHORT).show();
//                                btSelect.setText("Click to Upload PDF");
//                                tags.setText("");
//                            })
//                            .addOnFailureListener(e -> Toast.makeText(New.this, "Failed to upload PDF", Toast.LENGTH_SHORT).show());
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    // Handle onCancelled
//                }
//            });
//        } else {
//            // Handle the case where currentUser is null
//            Toast.makeText(New.this, "User not authenticated", Toast.LENGTH_SHORT).show();
//        }
//    }


    @SuppressLint("Range")
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        launcher.launch(intent);
    }

    private void handlePdfData() {
        if (pdfData.toString().startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = New.this.getContentResolver().query(pdfData, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if (pdfData.toString().startsWith("file://")) {
            pdfName = new File(pdfData.toString()).getName();
        }
        btSelect.setText(pdfName);
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
