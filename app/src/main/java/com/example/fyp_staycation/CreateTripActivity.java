package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class CreateTripActivity extends AppCompatActivity {

    private TextView title, county;
    private EditText date,meetTime;
    private DatePickerDialog picker;
    private String location = "";
    private FirebaseUser user;
    private Button createTrip;
    private FloatingActionButton createTrip2;
    private DatabaseReference tripDB, userDB;
    String user1, newUsername;
    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;
    private Button btnPicker;
    //private TextView latLng;
    private int PLACE_PICKER_REQUEST = 1;
    private Geocoder geocoder;
    private String name,countyName,username;
    private GoogleMap map;
    private OnMapReadyCallback onMapReadyCallback;
    private LatLng latLng;
    private String cords;
    private double latId, lngId, latId1, lngId1;
    private String image;
    private Toolbar toolbar;
    private TextView homeTitle;
    private ImageView imageView,homeImg;
    private Locations locations;

    private GoogleMap.OnMapClickListener mapClickListener;
    public CreateTripActivity(){

    }
    public CreateTripActivity(String image,String name, String countyName,String username, double latId, double lngId) {
        this.countyName = countyName;
        this.image=image;
        this.username = username;
        this.latId=latId;
        this.lngId=lngId;
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
                Intent intentMap = new Intent(CreateTripActivity.this, NearMeActivity.class);
                startActivity(intentMap);
                break;
            case R.id.home:
                Intent intentHome = new Intent(CreateTripActivity.this, HomeActivity.class);
                startActivity(intentHome);
                break;
            case R.id.View:
                Intent intentProfile = new Intent(CreateTripActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.Trips:
                Intent intentTrip = new Intent(CreateTripActivity.this, TripsActivity.class);
                startActivity(intentTrip);
                break;
            case R.id.Connections:
                Intent intentGroup = new Intent(CreateTripActivity.this, GroupChatActivity.class);
                intentGroup.putExtra("lid",locations.getLid());
                startActivity(intentGroup);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(CreateTripActivity.this,MainActivity.class));
        }

        return true;
    }

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

        locations = new Locations();
        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Locations");
        databaseReference.child(location).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Locations locations = snapshot.getValue(Locations.class);
                if(locations!=null){
                    title.setText(locations.getTitle());
                    name = locations.getCity();
                    countyName = locations.getCounty();
                    latId = locations.getLat();
                    lngId = locations.getLng();
                    Log.e("test", String.valueOf(latId));
                    county.setText("County " + countyName);
                }
                Log.e("test", "test");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        geocoder = new Geocoder(this);
        newUsername = "";
        user = FirebaseAuth.getInstance().getCurrentUser();
        tripDB = FirebaseDatabase.getInstance().getReference().child("Trips");
        userDB = FirebaseDatabase.getInstance().getReference().child("User");
        user1 = user.getUid();
        title = (TextView) findViewById(R.id.tripTitle);
        county = (TextView) findViewById(R.id.tripCounty);
        //getLocationDetails(location);

        imageView = (ImageView) findViewById(R.id.profile_image_details);
        homeTitle = (TextView) findViewById(R.id.homeTitle);
        homeImg = (ImageView) findViewById(R.id.homeImg);
        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHome = new Intent(CreateTripActivity.this, HomeActivity.class);
                startActivity(intentHome);
            }
        });
        getUserDetails();


        userDB.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user!=null){
                    username = user.getUsername();
                    image = user.getImage();
                    Log.e("test", username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        meetTime = (EditText) findViewById(R.id.pickTime);
        meetTime.setInputType(InputType.TYPE_NULL);
        meetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TimePickerDialog timePicker = new TimePickerDialog(CreateTripActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                int hour,min;
                                hour = hourOfDay;
                                min=minute;
                                String time = hourOfDay + ":" + minute;
                                SimpleDateFormat f24hours = new SimpleDateFormat("HH:mm");
                                try {
                                    Date date = f24hours.parse(time);
                                    SimpleDateFormat f12hours = new SimpleDateFormat("hh:mm aa");
                                    meetTime.setText(f12hours.format(date));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        },12 ,0 ,false);
                timePicker.show();
            }
        });


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
    }

    private void getUserDetails() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user!=null){
                    String name = user.getUsername();
                    if(user.getImage()!=null) {
                        String image = user.getImage();
                        Log.e("testing123", image);
                        homeTitle.setText("Hi " + name);
                        Picasso.get().load(user.getImage()).into(imageView);
                    }else{
                        Picasso.get().load(R.drawable.profile_icon_foreground).into(imageView);
                    }
                }
                Log.e("test", "test");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                            Log.e("test", String.valueOf(county));
//                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//                            MarkerOptions options = new MarkerOptions().position(latLng).title("Current Location");
                            //DONT FORGET TO FIX THIS!!!
//                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
//                            googleMap.addMarker(options);
                            try {
                                List<Address> addressList = geocoder.getFromLocation(latId, lngId, 1);
                                if(addressList.size()>0) {
                                    Address address = addressList.get(0);
                                    latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                    MarkerOptions options = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude()))
                                            .title(address.getLocality()).title("Location of Trip");
                                    map.addMarker(options);
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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

        trip.put("createdBy", username);
        trip.put("tid", timestamp);
        trip.put("tripTitle", location);
        trip.put("date", date.getText().toString());
        trip.put("meettime", meetTime.getText().toString());
        trip.put("lat", latId1);
        trip.put("lng", lngId1);
        trip.put("address", name);


        tripDB.child(timestamp).updateChildren(trip)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        HashMap<String, Object> members = new HashMap<>();

                        //members.put("Username", newUsername);
                        members.put("userEmail", user.getEmail());
                        members.put("uid", user.getUid());
                        members.put("username", username);
                        members.put("image", image);
                        Trip trip1 = new Trip();
                        trip1.setTid(timestamp);
                        trip1.setDate(date.getText().toString());
                        trip1.setMeettime(meetTime.getText().toString());
                        trip1.setCreatedBy(username);
                        trip1.setTripTitle(location);
                        trip1.setLat(latId);
                        trip1.setLng(lngId);
                        trip1.setAddress(name);
                        trip1.setParticipants(String.valueOf(user));

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Trips").child(timestamp);
                        db.child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(members)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(CreateTripActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(CreateTripActivity.this, "Trip Created", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(CreateTripActivity.this, "Trip is now in Trip Screen", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }
                });

    }

//    private void getLocationDetails(String location) {
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Connections");
//        reference.child(location).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()){
//                    Connections connections = snapshot.getValue(Connections.class);
//                    if(connections != null){
//                        title.setText(connections.getCtitle());
//                    }
//                    else {
//                        System.out.println("Error");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

}