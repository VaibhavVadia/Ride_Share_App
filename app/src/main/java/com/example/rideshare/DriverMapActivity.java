package com.example.rideshare;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;


public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location mLastLocation;
    private Button mLogout;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    Marker mCurrLocationMarker;
    private String customerId ="";
    private FirebaseAuth mAuth;
    private Boolean isLoggingOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        getAssignedCustomer();

        mLogout = (Button) findViewById(R.id.driverlogout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoggingOut =true;
                disconnectCustomer();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(DriverMapActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }
    }
    private void getAssignedCustomer(){
        String driverId = FirebaseAuth.getInstance().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(driverId).child("Customer ID");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    customerId= dataSnapshot.getValue().toString();
                    getAssignedCustomerPickupLocation();

                }
                else {
                    customerId = "";
                    if (pickupMarker != null) {
                        pickupMarker.remove();
                    }
                    if (assignedCustomerPickUpRefListner != null) {
                        assignedCustomerPickUpRef.removeEventListener(assignedCustomerPickUpRefListner);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    Marker pickupMarker;
    private DatabaseReference assignedCustomerPickUpRef;
    private ValueEventListener assignedCustomerPickUpRefListner;
    private void getAssignedCustomerPickupLocation(){
        assignedCustomerPickUpRef = FirebaseDatabase.getInstance().getReference().child("customerRequest").child(customerId);
        assignedCustomerPickUpRefListner = assignedCustomerPickUpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && !customerId.equals("")){
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLong = 0;
                    if (map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());

                    }
                    if (map.get(1) != null) {
                        locationLong = Double.parseDouble(map.get(1).toString());

                    }
                    LatLng customerLatLang = new LatLng(locationLat,locationLong);
                    pickupMarker=mMap.addMarker(new MarkerOptions().position(customerLatLang).title("Pick Up Location"));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(DriverMapActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                if(getApplicationContext()!=null) {
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                    }
                    mAuth = FirebaseAuth.getInstance();
                    if (mAuth.getCurrentUser() != null) {


                        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
                        DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");
                        GeoFire geoFireAvailable = new GeoFire(refAvailable);
                        GeoFire geoFireWorking = new GeoFire(refWorking);

                        switch (customerId) {
                            case "":
                                geoFireAvailable.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                                geoFireWorking.removeLocation(userId);
                                break;

                            default:
                                geoFireWorking.setLocation(userId, new GeoLocation(location.getLatitude(), location.getLongitude()));
                                geoFireAvailable.removeLocation(userId);
                                break;

                        }
                    }
                }


            }
        }
    };




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void disconnectCustomer(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference refAvailable = FirebaseDatabase.getInstance().getReference("driversAvailable");
        DatabaseReference refWorking = FirebaseDatabase.getInstance().getReference("driversWorking");

            GeoFire geoFireAvailable = new GeoFire(refAvailable);
            geoFireAvailable.removeLocation(userId);

            GeoFire geoFireWorking = new GeoFire(refWorking);
            geoFireWorking.removeLocation(userId);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isLoggingOut){
            disconnectCustomer();
        }

    }
}