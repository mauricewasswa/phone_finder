package com.rengcorp.phone_finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.firebase.FirebaseApp;
import com.google.firebase.*;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

// Import other necessary packages
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private final int FINAL_PERMISSION_CODE = 1;
    private GoogleMap mMap;
    private DatabaseReference dBRef;
    private ValueEventListener locationListener1;
    private String userName; // Add this line
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        dBRef = FirebaseDatabase.getInstance().getReference("user_locations");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                updateUi(location);
            }
        };

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions from the user if needed.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, FINAL_PERMISSION_CODE);
        } else {
            // Permissions are granted, request location updates.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }

        Intent intent = getIntent();
        userName = intent.getStringExtra("user_name"); // Get the user's name from the intent

        // Add ValueEventListener to listen for changes in the database
        locationListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Iterate through the user data
                    if (!userSnapshot.getKey().equals(userName)) { // Exclude the user's own data
                        double latitude = userSnapshot.child("latitude").getValue(Double.class);
                        double longitude = userSnapshot.child("longitude").getValue(Double.class);

                        String userName=userSnapshot.child("name").getValue(String.class);

                        LatLng userLocation=new LatLng(latitude,longitude);
                        MarkerOptions markerOptions=new MarkerOptions()
                                .position(userLocation)
                                .title(userName);

                        mMap.addMarker(markerOptions);

//
//                        LatLng userLocation = new LatLng(latitude, longitude);
//                        mMap.addMarker(new MarkerOptions().position(userLocation).title(userSnapshot.getKey())); // Display user names as markers
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error reading data: " + databaseError.getMessage());
            }
        };
        dBRef.addValueEventListener(locationListener1);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        // Remove the ValueEventListener when the activity is destroyed
        dBRef.removeEventListener(locationListener1);
    }

    public void updateUi(Location location) {
        if (mMap != null && location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng userLocation = new LatLng(latitude, longitude);
            Intent intent = getIntent();
            String userName = intent.getStringExtra("user_name");
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(userName));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            getUserLocation(location);
        }
    }
    public void getUserLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            DatabaseReference userRef = dBRef.child(userName); // Use the user's name as the key
            UserLocation user = new UserLocation(latitude, longitude, System.currentTimeMillis(), userName);
            userRef.setValue(user); // Update the user's location instead of pushing a new key
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000000000, 0, locationListener);
        }
    }
}
