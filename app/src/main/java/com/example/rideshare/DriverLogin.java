package com.example.rideshare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DriverLogin extends AppCompatActivity {
    private EditText driverEmail;
    private EditText driverPassword;
    private TextView driverSignUp;
    private Button driverConitnue;


    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener fireBaseAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        mAuth = FirebaseAuth.getInstance();

        fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){

                    Intent intent = new Intent(DriverLogin.this, DriverMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        driverEmail = (EditText) findViewById(R.id.driveremail);
        driverPassword=(EditText) findViewById(R.id.driverpassword);
    Button  driverConitnue = (Button) findViewById(R.id.continuelogindriver);

        driverConitnue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();

            }
        });


        driverSignUp = (TextView) findViewById(R.id.driversignup);
        driverSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });


    }

    private void Login(){
        final String loginEmail = driverEmail.getText().toString();
        final String loginPassword = driverPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnCompleteListener(DriverLogin.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(DriverLogin.this, "Email Password Combination Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SignUp(){
        Intent intent = new Intent(DriverLogin.this, DriverSignup.class);
        startActivity(intent);
        finish();
        return;
    }

    @Override
    protected void onStart() {

        super.onStart();

        mAuth.addAuthStateListener(fireBaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(fireBaseAuthListener);
    }
}
