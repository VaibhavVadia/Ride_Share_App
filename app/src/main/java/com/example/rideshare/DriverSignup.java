package com.example.rideshare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverSignup extends AppCompatActivity {

    private EditText dname;
    private EditText demail;
    private EditText dpassword;
    private EditText dphoneNumber;
    private EditText dcarNumber;
    private EditText dcarModel;
    private Button daccount;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener fireBaseAuthListener;

    private Registration reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_signup);

        mAuth = FirebaseAuth.getInstance();
        fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){

                    Intent intent = new Intent(DriverSignup.this, DriverMapActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
        dname = (EditText) findViewById(R.id.dName);
        dcarNumber= findViewById(R.id.dCarNumber);
        dcarModel= findViewById(R.id.dCarModel);
        demail= (EditText) findViewById(R.id.dEmail);
        dpassword = (EditText) findViewById(R.id.dPassword);
        dphoneNumber=(EditText) findViewById(R.id.dPhoneNumber);
       daccount = (Button) findViewById(R.id.dAccount);

        daccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRegistration();

            }
        });
    }
    private void getRegistration(){
        final String setName = dname.getText().toString();
        final String setCarNumber = dcarNumber.getText().toString();
        final String setCarModel = dcarModel.getText().toString();
        final String setEmail = demail.getText().toString();
        final String setPassword = dpassword.getText().toString();
        final String setPhoneNumber = dphoneNumber.getText().toString();
        mAuth.createUserWithEmailAndPassword(setEmail,setPassword).addOnCompleteListener(DriverSignup.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(DriverSignup.this, "Sign Up Error", Toast.LENGTH_SHORT).show();

                }else{
                    reg = new Registration();
                    String user_Id = mAuth.getCurrentUser().getUid();
                    DatabaseReference current_user = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(user_Id);
                    reg.setFirstName(setName);
                    reg.setCarNumber(setCarNumber);
                    reg.setCarModel(setCarModel);
                    reg.setEmailAddress(setEmail);
                    reg.setPhoneNumber(setPhoneNumber);
                    current_user.setValue(reg);
                }

            }
        });
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
