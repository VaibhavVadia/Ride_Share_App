package com.example.rideshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class MainActivity extends AppCompatActivity {
   private Button driver;
   private Button customer;

private CarouselView carouselView;

private int[] pictureSamples = {R.drawable.product1,R.drawable.product2};
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

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(pictureSamples.length);
        carouselView.setImageListener(imageListener);

        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueCustomer();
            }
        });

    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(pictureSamples[position]);
        }
    };

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
