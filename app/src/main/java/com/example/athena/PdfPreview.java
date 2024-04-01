package com.example.athena;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class PdfPreview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        EdgeToEdge.enable(this);

        // Receive the PDF title passed from SearchPage
        String pdfTitleFull = getIntent().getStringExtra("pdfTitleFull");
        String pdfTitle = getIntent().getStringExtra("pdfTitle");

        // Display PDF details and preview
        displayPdfDetailsAndPreview(pdfTitleFull, pdfTitle);

        setContentView(R.layout.activity_pdf_preview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void displayPdfDetailsAndPreview(String pdfTitleFull, String pdfTitle) {
        Log.d(null, "displayPdfDetailsAndPreview: Searching path");
        DatabaseReference pdfRef = FirebaseDatabase.getInstance().getReference("pdf");

        pdfRef.orderByChild("pdfTitleFull").equalTo(pdfTitleFull).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(null, "displayPdfDetailsAndPreview: Found path");
                for (DataSnapshot pdfSnapshot : dataSnapshot.getChildren()) {
                    // Get PDF details
                    String uploaderEmail = pdfSnapshot.child("uploader").getValue(String.class);
                    String pdfTags = pdfSnapshot.child("pdfTags").getValue(String.class);
                    boolean saved = pdfSnapshot.child("saved").getValue(Boolean.class);
                    String pdfFileName = pdfSnapshot.child("pdfTitleFull").getValue(String.class);

                    // Display PDF details
                    Button saveButton = findViewById(R.id.saveButton);
                    TextView uploaderEmailTextView = findViewById(R.id.uploaderEmailTextView);
                    TextView pdfTagsTextView = findViewById(R.id.pdfTagsTextView);
                    TextView pdfNameTextView = findViewById(R.id.pdfNameTextView);

                    uploaderEmailTextView.setText("Uploaded by: " + uploaderEmail);
                    pdfNameTextView.setText("Name: " + pdfTitle);
                    pdfTagsTextView.setText("Tags: " + pdfTags);
                    if (saved == false) {
                        saveButton.setText("Not saved");
                    }else {
                        saveButton.setText("Saved");
                    }

                    // Load PDF into PDF viewer
                    loadPdfIntoViewer(pdfFileName);
                    Log.d(null, "displayPdfDetailsAndPreview: Searching for pdf named: "+ pdfFileName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                // For simplicity, you can add a toast or log message here
            }
        });
    }

    private void loadPdfIntoViewer(String pdfFileName) {
        // Get a reference to the Firebase Storage instance
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        Log.d(null, "loadPdfIntoViewer: Searching for pdf named: "+ pdfFileName);
        // Construct a reference to the PDF file
        StorageReference pdfRef = storageRef.child("pdf/" + pdfFileName+".pdf");

        // Fetch the download URL of the PDF file
        pdfRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Load the PDF into PDFView using the retrieved URL
                PDFView pdfView = findViewById(R.id.pdfView);
                pdfView.fromUri(uri).load();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors
                Log.e("PdfPreview", "Failed to fetch PDF URL: " + e.getMessage());
            }
        });
    }

}