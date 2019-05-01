package com.example.rideshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
   private Button driver;
   private Button customer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        driver = (Button) findViewById(R.id.driver);

        driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueDriver();
            }
        });

        customer =(Button) findViewById(R.id.rider);

        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueCustomer();
            }
        });

    }

    private void continueDriver(){
        Intent intent = new Intent(MainActivity.this, DriverLogin.class);
        startActivity(intent);
        return;

    }

    private void continueCustomer(){
        Intent intent = new Intent(MainActivity.this, CustomerLogin.class);
        startActivity(intent);
        return;
    }
}
