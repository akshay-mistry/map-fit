package com.example.mapfitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    EditText emailText;
    EditText passwordText;
    Button signinBut;
    Button accountBut;
    String email;
    String currentEmail;
    String password;
    Context mContext;
    private static final String KEY = "KEY001";
    private static int REQUEST_CODE = 1;
    DatabaseReference databaseReference;
    ArrayList<ActivityInformation> activeUsers;
    boolean existingUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(MainActivity.this);
        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailTextLogin);
        passwordText = findViewById(R.id.passwordTextLogin);
        signinBut = findViewById(R.id.signinButLogin);
        accountBut = findViewById(R.id.accountButLogin);
        mContext = this;
        existingUser = false;
        checkPermission(REQUEST_CODE);

        databaseReference = FirebaseDatabase.getInstance().getReference("activeusers");

        signinBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailText.getText().toString();
                password = passwordText.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(KEY, "Signed In");
                            Toast.makeText(MainActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                            addUser();
                            finish();
                            Intent login = new Intent(MainActivity.this, MapActivity.class);
                            startActivity(login);
                        } else {
                            Log.d(KEY, "Could Not Sign In");
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
        accountBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createAccount = new Intent(MainActivity.this, AccountCreationActivity.class);
                startActivity(createAccount);

            }
        });

    }


    public void checkPermission(int requestCode) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACTIVITY_RECOGNITION }, requestCode);
        }
        else {
            Toast.makeText(mContext, "Location and Activity Permissions Granted", Toast.LENGTH_LONG).show();
            emailText.setEnabled(true);
            passwordText.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Location and Activity Permissions Granted", Toast.LENGTH_LONG).show();
                emailText.setEnabled(true);
                passwordText.setEnabled(true);
            }
        }
        else {
            Toast.makeText(mContext, "Location and Activity Permissions Denied", Toast.LENGTH_LONG).show();
        }
        return;
    }

    public void addUser() {
        activeUsers = new ArrayList<>();
        currentUser = mAuth.getCurrentUser();
        currentEmail = currentUser.getEmail();
        validateEmail(currentEmail, new UserExistsCallback() {
            @Override
            public void onCallback(boolean value) {
                if (!value) {
                    String user = databaseReference.push().getKey();
                    ActivityInformation activityInformation = new ActivityInformation(currentEmail, 0, 0, user, null, " ", " ");
                    databaseReference.child(user).setValue(activityInformation);
                }

            }
        });


    }

    private void validateEmail(String email, final UserExistsCallback callback) {

        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key = dataSnapshot.getKey();
                Log.d(key, "USERKEY");
                callback.onCallback(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

