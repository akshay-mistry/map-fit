package com.example.mapfitapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountCreationActivity extends AppCompatActivity {

    EditText nameText;
    EditText emailText;
    EditText passwordText;
    Button accountBut;
    String name;
    String email;
    String password;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    private static final String KEY = "KEY001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(AccountCreationActivity.this);
        setContentView(R.layout.activity_account_creation);
        mAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailTextCreate);
        passwordText = findViewById(R.id.passwordTextCreate);
        accountBut = findViewById(R.id.accountButCreate);
        accountBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailText.getText().toString();
                password = passwordText.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password);
                Intent login = new Intent(AccountCreationActivity.this, MainActivity.class);
                startActivity(login);

            }
        });

    }
}