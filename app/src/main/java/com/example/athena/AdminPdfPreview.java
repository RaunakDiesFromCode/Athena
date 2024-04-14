package com.example.athena;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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
import java.util.List;


public class AdminPdfPreview extends AppCompatActivity {

    // Declare variables
    String pdfTitle, tags, URL, ID;
    private ViewPager viewPager;
    private List<Bitmap> renderedPages = new ArrayList<>();
    private PdfRenderer pdfRenderer;
    private int pageCount = 0;
    private PdfPagerAdapter adapter;
    private ProgressBar progressBar;
    private Button saveButton;
    private Button downloadButton;

    private BroadcastReceiver onCompleteReceiver;

    String uploader;

    private boolean isSaved = false;

    private Context context;
    private File tempFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_pdf_preview);


        // Enable edge-to-edge display
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        EdgeToEdge.enable(this);

        // Set up UI elements
        LinearLayout quickBtn = findViewById(R.id.quickBtn);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            quickBtn.setPadding(0, 0, 0, getNavigationBarHeight());
        }
        viewPager = findViewById(R.id.viewPager);
        progressBar = findViewById(R.id.progressBar);

        // Get data from intent
        Intent intent = getIntent();
        pdfTitle = intent.getStringExtra("pdfTitle");
        tags = intent.getStringExtra("pdfTags");
//        saved = intent.getStringExtra("saved");
        URL = intent.getStringExtra("URL");
        ID = intent.getStringExtra("ID");
        uploader = intent.getStringExtra("uploader");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null) {
//            // Get the user's emails
//            uploader = currentUser.getEmail();
//        }

        Log.d(null, "onCreate: Uploader " + uploader);

        saveButton = findViewById(R.id.saveButton);
        downloadButton = findViewById(R.id.downloadButton);
        saveButton.setVisibility(View.GONE);
//        downloadButton.setVisibility(View.GONE);

        // Set text for TextViews
        TextView pdfNameTextView = findViewById(R.id.pdfNameTextView);
        TextView pdfTagsTextView = findViewById(R.id.pdfTagsTextView);
        TextView uploaderEm = findViewById(R.id.uploader);
        pdfNameTextView.setText("Title: " + pdfTitle);
        pdfTagsTextView.setText("Tags: " + tags);
        uploaderEm.setText("Uploader: " + uploader);
//        pdfTagsTextView.setVisibility(View.GONE);
//        pdfNameTextView.setVisibility(View.GONE);

        // Parse URL
        Uri uri = Uri.parse(URL);

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Download and render PDF
        downloadAndRenderPdf(uri);
        setupDownloadButton();

    }



    // Method to set up the download button
    private void setupDownloadButton() {
        Button downloadButton = findViewById(R.id.downloadButton);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(null, "onClick: URL = " + URL);
                Log.d(null, "onClick: pdf title = " + pdfTitle);
                // Start download process
                downloadPDF(getApplicationContext(), URL, pdfTitle);
            }
        });
    }



    public static void downloadPDF(Context context, String storageUrl, String pdfTitle) {
        Toast.makeText(context, "PDF Downloading...", Toast.LENGTH_SHORT).show();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(storageUrl);

        // Get the Downloads folder path
        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        // Create the file object representing the PDF file in the Downloads folder
        File pdfFile = new File(downloadsFolder, pdfTitle + ".pdf");

        storageRef.getFile(pdfFile).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(context, "PDF Downloaded Successfully", Toast.LENGTH_SHORT).show();
            // File download success
            // Optionally, you can notify the user that the download is complete
            // For example, using a Toast
            // Toast.makeText(context, "PDF Downloaded Successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(exception -> {
            Toast.makeText(context, "PDF Download Failed", Toast.LENGTH_SHORT).show();
            // File download failed
            exception.printStackTrace();
            // Handle exceptions accordingly, e.g., notify the user about the failure
        });
    }

    // Method to register the BroadcastReceiver
    private void registerDownloadReceiver() {
        onCompleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Handle download completion
                long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                // Handle download completion...
            }
        };

        // Register the BroadcastReceiver with an intent filter
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(onCompleteReceiver, filter);
    }




    // Method to download and render PDF
    private void downloadAndRenderPdf(Uri pdfUri) {
        progressBar.setVisibility(View.VISIBLE);

        try {
            File localFile = File.createTempFile("temp_pdf", ".pdf");

            // Download the PDF file from Firebase Storage to the temporary file
            downloadFileFromFirebase(pdfUri, localFile);

        } catch (IOException e) {
            Toast.makeText(this, "A bad error occurred", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Method to download PDF file from Firebase Storage
    private void downloadFileFromFirebase(Uri uri, File tempFile) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString());
        storageRef.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // File downloaded successfully
                Log.d(null, "PDF file downloaded successfully");
                // Now that the file is downloaded, render and display it
                renderAndDisplayPdf(tempFile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                Log.e(null, "Error downloading PDF file from Firebase Storage: " + exception.getMessage());
            }
        });
    }

    // Method to render and display PDF
    private void renderAndDisplayPdf(File pdfFile) {
        progressBar.setVisibility(View.INVISIBLE);
        try {
            ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            if (parcelFileDescriptor != null) {
                pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                pageCount = pdfRenderer.getPageCount();

                // Render all pages
                for (int i = 0; i < pageCount; i++) {
                    PdfRenderer.Page page = pdfRenderer.openPage(i);
                    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    renderedPages.add(bitmap);
                    page.close();
                }

                // Set up ViewPager
                adapter = new PdfPagerAdapter(renderedPages);
                viewPager.setAdapter(adapter);
            } else {
                Log.e(null, "ParcelFileDescriptor is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(null, "Error opening PDF file: " + e.getMessage());
            Toast.makeText(this, "Error opening PDF file", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to get navigation bar height
    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pdfRenderer != null) {
            pdfRenderer.close();
        }
        if (onCompleteReceiver != null) {
            unregisterReceiver(onCompleteReceiver);
        }
//        cancelDownloadAndCleanup();
    }


    // Method to cancel ongoing tasks and cleanup resources

}
