package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.fyp_staycation.adapters.UserAdapter;
import com.example.fyp_staycation.classes.Connections;
import com.example.fyp_staycation.classes.Locations;
import com.example.fyp_staycation.classes.Participants;
import com.example.fyp_staycation.classes.Trip;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearMeActivity extends AppCompatActivity {

    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;
    private int PLACE_PICKER_REQUEST = 1;
    private Geocoder geocoder;
    private String name,countyName,username;
    private GoogleMap map;
    private OnMapReadyCallback onMapReadyCallback;
    private LatLng latLng;
    private String cords,tripId;
    private double latId, lngId, latId1, lngId1;
    private ArrayList<Trip> mapPoints;
    private Trip trip;
    private Toolbar toolbar;
    private Locations locations;
    private TextView homeTitle;

    public NearMeActivity(){

    }
    public NearMeActivity(String tripId,String name, String countyName,String username, double latId, double lngId) {
        this.countyName = countyName;
        //this.image=image;
        this.username = username;
        this.latId=latId;
        this.lngId=lngId;
        this.tripId=tripId;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //RUNNING MENU2.XML OVER ACTIVITY
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.tripNearMe:
                Intent intentMap = new Intent(NearMeActivity.this, NearMeActivity.class);
                startActivity(intentMap);
                break;
            case R.id.home:
                Intent intentHome = new Intent(NearMeActivity.this, HomeActivity.class);
                startActivity(intentHome);
                break;
            case R.id.View:
                Intent intentProfile = new Intent(NearMeActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.Trips:
                Intent intentTrip = new Intent(NearMeActivity.this, TripsActivity.class);
                startActivity(intentTrip);
                break;
            case R.id.Connections:
                Intent intentGroup = new Intent(NearMeActivity.this, GroupChatActivity.class);
                intentGroup.putExtra("lid",locations.getLid());
                startActivity(intentGroup);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(NearMeActivity.this,MainActivity.class));
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_me);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("trip is null", "");
        } else {
            tripId = extras.getString("tid");

        }
        trip = new Trip();
        geocoder = new Geocoder(this);

        Log.e("trip id test", ""+tripId);
        toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.nearMeMap);
        client = LocationServices.getFusedLocationProviderClient(this);
        mapPoints = new ArrayList<Trip>();


        if (ActivityCompat.checkSelfPermission(NearMeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
        else{
            ActivityCompat.requestPermissions(NearMeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44 );
        }

    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location1) {
                if(location1 != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;
                            //Log.e("test", String.valueOf(county));
                            LatLng latLng = new LatLng(location1.getLatitude(),location1.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(latLng).title("Current Location");
                            //DONT FORGET TO FIX THIS!!!
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            //googleMap.addMarker(options);


                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Trips");
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Log.e("Value", "Test");
                                    for(DataSnapshot ds: snapshot.getChildren()) {
                                        Trip trip = ds.getValue(Trip.class);
                                        Log.e("title Test", ""+trip.getTripTitle());
                                        mapPoints.add(trip);
                                        try {
                                            List<Address> addressList = geocoder.getFromLocation(trip.getLat(), trip.getLng(), 1);
                                            if(addressList.size()>0) {
                                                Address address = addressList.get(0);
                                                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                                MarkerOptions option = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude()))
                                                        .title(trip.getTripTitle()).snippet(trip.getTid());
                                                map.addMarker(option);
                                                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                    @Override
                                                    public boolean onMarkerClick(Marker marker) {
                                                        String newMarker = marker.getSnippet();
                                                        Log.e("test", "" +newMarker);
                                                        Intent intent = new Intent(NearMeActivity.this,ParticipantsActivity.class);
                                                        intent.putExtra("tid",newMarker);
                                                        startActivity(intent);

                                                        return false;
                                                    }
                                                });
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    Log.e("map", "test");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                                @Override
                                public void onMapLongClick(LatLng latLng1) {

                                    Log.d("test", "onMapLongClick: " + latLng1.toString());
                                    latLng1 = new LatLng(latLng1.latitude, latLng1.longitude);
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(latLng1.latitude, latLng1.longitude, 1);
                                        if (addresses.size() > 0) {
                                            Address address = addresses.get(0);
                                            latLng1 = new LatLng(address.getLatitude(), address.getLongitude());
                                            MarkerOptions newOptions = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude()))
                                                    .title(address.getLocality()).title("Meet Up Point Set");
                                            map.addMarker(newOptions);
                                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15));
                                            latId1 = latLng1.latitude;
                                            lngId1 = latLng1.longitude;
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }


}