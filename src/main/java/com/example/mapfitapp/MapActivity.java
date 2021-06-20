package com.example.mapfitapp;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    Context mContext;
    LatLng coordinates;
    MarkerOptions markerOptions;
    Marker marker;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    double latitude;
    double longitude;
    Boolean authInProgress;
    private Location lastKnownLocation;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    Button startWorkout;
    private static int REQUEST_CODE = 1;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    ArrayList<ActivityInformation> activeUsers;
    int count;
    TextView testText;
    Location startingLoc;
    String email;
    ActivityInformation currentUserActivity;
    int steps;
    boolean update;
    String workout;
    boolean created;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.fitness_nav) {
            Intent fitnessActivity = new Intent(MapActivity.this, FitnessActivity.class);
            fitnessActivity.putExtra("USER", currentUserActivity.getUser());
            fitnessActivity.putExtra("WORKOUT", workout);
            fitnessActivity.putExtra("CREATED", created);
            startActivity(fitnessActivity);
        }
        if (id == R.id.signout_nav) {
            update = false;
            String key = currentUserActivity.getUser();
            databaseReference.child(key).removeValue();
            mAuth.signOut();
            Intent signOut = new Intent(MapActivity.this, MainActivity.class);
            startActivity(signOut);
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        count = 0;
        update = true;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            workout = bundle.getString("WORKOUT");
            created = bundle.getBoolean("CREATED");
        }
        testText = findViewById(R.id.testText);
        if (workout != null)
        {
            if (workout.length() > 0)
                testText.setText(workout + " Workout");
        }
        startingLoc = null;
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        mContext = this;
        authInProgress = false;
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    updateCurrentLocation();
                }
            }
        };

        locationRequest = new LocationRequest();
        locationRequest.setInterval(200);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);
        checkPermission(REQUEST_CODE);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("activeusers");
        activeUsers = new ArrayList<>();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser.getEmail() != null)
            email = firebaseUser.getEmail();
        Log.d(email, "CURRENTEMAIL");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ArrayList<String> keys = new ArrayList<>();
                for (DataSnapshot d : snapshot.getChildren()) {
                    keys.add(d.getKey());
                    ActivityInformation activityInformation = d.getValue(ActivityInformation.class);

                    Log.d(activityInformation.getUser(), "USER");
                    Log.d(String.valueOf(latitude), "LAT");
                    activeUsers.add(activityInformation);

                }

                for (ActivityInformation a : activeUsers) {
                    if (a.getEmail() != null) {
                        if (a.getEmail().equals(email)) {
                            currentUserActivity = a;
                        }
                        Log.d(a.getEmail(), "EMAIL");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateCurrentLocation() {

        checkPermission(REQUEST_CODE);
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {

                    lastKnownLocation = task.getResult();
                    if (lastKnownLocation != null) {
                        latitude = lastKnownLocation.getLatitude();
                        longitude = lastKnownLocation.getLongitude();
                        coordinates = new LatLng(latitude, longitude);
                        Log.d("Coordinates: " + latitude + " , " + longitude, "KEYCOORD");

                        if (currentUserActivity!=null)
                        {
                            if (currentUserActivity.getEmail() != null || !update)
                            {
                                databaseReference.child(currentUserActivity.getUser()).child("latitude").setValue(latitude);
                                databaseReference.child(currentUserActivity.getUser()).child("longitude").setValue(longitude);
                            }
                        }


                        updateMarkerLocations();


                        if(marker == null){
                            markerOptions = new MarkerOptions();
                            markerOptions.position(coordinates);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon));
                            markerOptions.title("Current Location");
                            marker = map.addMarker(markerOptions);

                        }
                        else {
                            marker.setPosition(coordinates);
                        }

                        map.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 16));


                    }
                }
            }
        });
        
    }


    public void updateMarkerLocations() {
        for (ActivityInformation a : activeUsers) {
            if (a.getEmail() != null) {
                if (!a.getEmail().equals(email)) {
                    LatLng location = new LatLng(a.getLatitude(), a.getLongitude());
                    if (a.getMarker() == null) {
                        MarkerOptions newMarkerOptions = new MarkerOptions();
                        newMarkerOptions.position(location);
                        switch (a.getWorkoutType())
                        {
                            case "Running":
                                newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.run));
                                break;
                            case "Walking":
                                newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.run));
                                break;
                            case "Jogging":
                                newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.run));
                                break;
                            case "Basketball":
                                newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.basketball));
                                break;
                            case "Soccer":
                                newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.soccer));
                                break;
                            case "Volleyball":
                                newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.volleyball));
                                break;
                            case "Football":
                                newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.football));
                                break;
                            case "Swimming":
                                newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.swimming));
                                break;
                            default:
                                newMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.othericon));
                                break;
                        }

                        newMarkerOptions.title(a.getEmail());
                        a.setMarker(map.addMarker(newMarkerOptions));
                        a.getMarker().setTag(a);
                    } else {
                        a.getMarker().setTag(a);
                        a.getMarker().setPosition(location);
                    }
                }
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16));
                String email = marker.getTitle();
                ActivityInformation markerActivity = null;
                for (ActivityInformation a : activeUsers) {
                    if (a.getEmail() != null)
                    {
                        if (a.getEmail().equals(email))
                        {
                            markerActivity = a;
                        }
                    }
                }
                if (markerActivity != null) {
                    Information information = new Information(markerActivity, currentUserActivity, map, coordinates);
                    information.show(getSupportFragmentManager(), "popup");
                }
                return false;
            }
        });
        updateCurrentLocation();

    }

    public void checkPermission(int requestCode) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapActivity.this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACTIVITY_RECOGNITION}, requestCode);
        }
    }

}
