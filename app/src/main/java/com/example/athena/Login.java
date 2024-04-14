package com.example.athena;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

//public class Login extends AppCompatActivity {
//
//    TextInputEditText editTextEmail, editTextPassword;
//    Button buttonLogin;
//    FirebaseAuth mAuth;
//    ProgressBar progressBar;
//    TextView textView;
//    DatabaseReference adminRef;
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent intent = new Intent(getApplicationContext(), SearchPage.class);
//            startActivity(intent);
//            finish();
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        EdgeToEdge.enable(this);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
////        TextView logtxt = findViewById(R.id.loginTxt);
////        String l = logtxt.getText().toString().trim();
//
//
//        mAuth = FirebaseAuth.getInstance();
//
//        adminRef = FirebaseDatabase.getInstance().getReference("admins");
//
//        editTextEmail = findViewById(R.id.email);
//        editTextPassword = findViewById(R.id.password);
//        buttonLogin = findViewById(R.id.btn_login);
//
//        progressBar = findViewById(R.id.progressBar);
//
//        textView = findViewById(R.id.RegisterNow);
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), Register.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        buttonLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                progressBar.setVisibility(View.VISIBLE);
//                String email, password;
//                email = String.valueOf(editTextEmail.getText());
//                password = String.valueOf(editTextPassword.getText());
//
//                if (TextUtils.isEmpty(email)) {
//                    Toast.makeText(Login.this, "Where's the email?", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (TextUtils.isEmpty(password)) {
//                    Toast.makeText(Login.this, "Where's the password?", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (!isInternetAvailable()) {
//                    Toast.makeText(Login.this, "No internet connection", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
//                    return;
//                }
//
//                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        progressBar.setVisibility(View.GONE);
//                        if (task.isSuccessful()) {
//                            Toast.makeText(Login.this, "hooray!! You're Logged in", Toast.LENGTH_SHORT).show();
//                            Intent intent;
//                            if (checkAdminStatus(email)) {
//                                Log.d(null, "onComplete: User is admin and will now get redirected to admin page");
//                                intent = new Intent(getApplicationContext(), Saved.class);
//                            } else {
//                                Log.d(null, "onComplete: User is not an admin and will now get redirected to user page");
//                                intent = new Intent(getApplicationContext(), Saved.class);
//                            }
//                            startActivity(intent);
//                            finish();
//                        } else if (!isInternetAvailable()) {
//                            Toast.makeText(Login.this, "Get that internet on!!", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(Login.this, "That's not you", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//
//    }
//
//    private boolean checkAdminStatus(final String email) {
//        Log.d(null, "onDataChange: User "+ email + " Logged in");
//
//        int atIndex = email.indexOf('@');
//        final String adminEmailWithoutDomain = email.substring(0, atIndex);
//        Log.d(null, "onDataChange: User Without domain "+ adminEmailWithoutDomain);
//
//        final boolean[] isAdmin = {false}; // Using an array to hold the boolean value
//
//        // Use a TaskCompletionSource to wait for the async operation to complete
//        final TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
//
//        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {
//                        String adminEmail = adminSnapshot.getKey();
//                        if (adminEmailWithoutDomain.equals(adminEmail)) {
//                            Boolean isAdminValue = adminSnapshot.getValue(Boolean.class);
//                            if (isAdminValue != null && isAdminValue) {
//                                isAdmin[0] = true;
//                                break;
//                            }
//                        }
//                    }
//                }
//                taskCompletionSource.setResult(null); // Signal that the operation is complete
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle database error
//                taskCompletionSource.setException(databaseError.toException()); // Signal that an error occurred
//            }
//        });
//
//        // Wait for the async operation to complete
//        try {
//            Tasks.await(taskCompletionSource.getTask());
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//            // Handle exception
//        }
//
//        return isAdmin[0];
//    }
//
//
//    private boolean isInternetAvailable() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager != null) {
//            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//        }
//        return false;
//    }
//
//}


public class Login extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    DatabaseReference adminRef;
    private EditText passwordEditText;
    private Button peekPasswordButton;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), SearchPage.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        adminRef = FirebaseDatabase.getInstance().getReference("admins");

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.RegisterNow);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });

        passwordEditText = findViewById(R.id.password);
        peekPasswordButton = findViewById(R.id.peekPasswordButton);

        // Set onClickListener to toggle password visibility
        peekPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle password visibility
                if (passwordEditText.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // If password is visible, hide it
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    peekPasswordButton.setText("Show"); // Change button text to "Show"
                } else {
                    // If password is hidden, show it
                    passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    peekPasswordButton.setText("Hide"); // Change button text to "Hide"
                }

                // Move cursor to the end of the password EditText
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Login.this, "Where's the email?", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, "Where's the password?", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isInternetAvailable()) {
                    Toast.makeText(Login.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Hooray! You're logged in", Toast.LENGTH_SHORT).show();
                            checkAdminStatusAndProceed(email);
                        } else if (!isInternetAvailable()) {
                            Toast.makeText(Login.this, "Get that internet on!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Login.this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void checkAdminStatusAndProceed(final String email) {
        Log.d(null, "onDataChange: User " + email + " Logged in");

        int atIndex = email.indexOf('@');
        final String adminEmailWithoutDomain = email.substring(0, atIndex);
        Log.d(null, "onDataChange: User Without domain " + adminEmailWithoutDomain);

        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isAdmin = false;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot adminSnapshot : dataSnapshot.getChildren()) {
                        String adminEmail = adminSnapshot.getKey();
                        if (adminEmailWithoutDomain.equals(adminEmail)) {
                            Boolean isAdminValue = adminSnapshot.getValue(Boolean.class);
                            if (isAdminValue != null && isAdminValue) {
                                isAdmin = true;
                                break;
                            }
                        }
                    }
                }
                redirectToPage(isAdmin);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Login.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void redirectToPage(boolean isAdmin) {
        Intent intent;
        if (isAdmin) {
            Log.d(null, "User is admin and will now get redirected to admin page");
            intent = new Intent(getApplicationContext(), AdminMainActivity.class);
        } else {
            Log.d(null, "User is not an admin and will now get redirected to user page");
            intent = new Intent(getApplicationContext(), SearchPage.class);
        }
        startActivity(intent);
        finish();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}