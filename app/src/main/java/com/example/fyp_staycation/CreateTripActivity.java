package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_staycation.classes.Connections;
import com.example.fyp_staycation.classes.Locations;
import com.example.fyp_staycation.classes.Trip;
import com.example.fyp_staycation.classes.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;

public class CreateTripActivity extends AppCompatActivity {

    private TextView title;
    private EditText date;
    private DatePickerDialog picker;
    private String location = "";
    private FirebaseUser user;
    private Button createTrip;
    private FloatingActionButton createTrip2;
    private DatabaseReference tripDB, userDB;
    String user1, newUsername;
    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.e("location is null", "");
        } else {
            location = extras.getString("groupId");
        }

        newUsername = "";
        user = FirebaseAuth.getInstance().getCurrentUser();
        tripDB = FirebaseDatabase.getInstance().getReference().child("Trips");
        userDB = FirebaseDatabase.getInstance().getReference().child("Users");
        user1 = user.getUid();
        title = (TextView) findViewById(R.id.tripTitle);
        getLocationDetails(location);

        date = (EditText) findViewById(R.id.pickDate);
        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(CreateTripActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });


        createTrip2 = (FloatingActionButton) findViewById(R.id.createTripBtn2);
        createTrip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTrip();
            }
        });


        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.tripMap);
        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(CreateTripActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
        else{
            ActivityCompat.requestPermissions(CreateTripActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44 );
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
            public void onSuccess(Location location) {
                if(location != null){
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(latLng).title("Current Location");

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            googleMap.addMarker(options);
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

    private void createTrip() {


        /*userDB.child(user1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    newUsername = userProfile.getUsername();
                }
                else {
                    Toast.makeText(CreateTripActivity.this,"error",Toast.LENGTH_SHORT).show();
                }
                Log.e("test", "test");
                Log.e("test", newUsername);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        String timestamp = ""+System.currentTimeMillis();


        final HashMap<String, Object> trip = new HashMap<>();

        trip.put("createdBy", user.getEmail());
        trip.put("tid", timestamp);
        trip.put("tripTitle", location);
        trip.put("date", date.getText().toString());

        tripDB.child(timestamp).updateChildren(trip)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        HashMap<String, Object> members = new HashMap<>();


                        Toast.makeText(CreateTripActivity.this, "test " + newUsername, Toast.LENGTH_SHORT).show();
                        //members.put("Username", newUsername);
                        members.put("userEmail", user.getEmail());
                        members.put("uid", user.getUid());
                        Trip trip1 = new Trip();
                        trip1.setTid(timestamp);
                        trip1.setDate(date.getText().toString());
                        trip1.setCreatedBy(user.getEmail());
                        trip1.setTripTitle(location);
                        trip1.setParticipants(String.valueOf(user));




                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Trips").child(timestamp);
                        db.child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(members)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(CreateTripActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(CreateTripActivity.this, "Trip Created", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }
                });

    }

    private void getLocationDetails(String location) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Connections");
        reference.child(location).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Connections connections = snapshot.getValue(Connections.class);
                    if(connections != null){
                        title.setText(connections.getGroupId());

                    }
                    else {
                        System.out.println("Error");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}