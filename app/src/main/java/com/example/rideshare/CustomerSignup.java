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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerSignup extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText phoneNumber;
    private Button account;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener fireBaseAuthListener;

    private Registration reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);

                mAuth = FirebaseAuth.getInstance();
        fireBaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){

                    Intent intent = new Intent(CustomerSignup.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };
        name = (EditText) findViewById(R.id.setName);
        email= (EditText) findViewById(R.id.setEmail);
        password = (EditText) findViewById(R.id.setPassword);
        phoneNumber=(EditText) findViewById(R.id.setPhone);
        account = (Button) findViewById(R.id.createAccount);

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRegistration();

            }
        });
    }
    private void getRegistration(){
        final String setName = name.getText().toString();
        final String setEmail = email.getText().toString();
        final String setPassword = password.getText().toString();
        final String setPhoneNumber = phoneNumber.getText().toString();
    mAuth.createUserWithEmailAndPassword(setEmail,setPassword).addOnCompleteListener(CustomerSignup.this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if(!task.isSuccessful()){
                Toast.makeText(CustomerSignup.this, "Sign Up Error", Toast.LENGTH_SHORT).show();

            }else{
                reg = new Registration();
                String user_Id = mAuth.getCurrentUser().getUid();
                DatabaseReference current_user = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(user_Id);
                reg.setFirstName(setName);
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
