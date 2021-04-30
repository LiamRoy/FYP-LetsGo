package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_staycation.classes.Connections;
import com.example.fyp_staycation.classes.Locations;
import com.example.fyp_staycation.classes.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class LocationDetailsActivity extends AppCompatActivity {

    private Uri ImageUri;
    private StorageReference LocationImagesRef;
    private FloatingActionButton addToTrip, cancelBtn;
    private ImageView imageView,linkImage;
    private DatePickerDialog picker;
    private EditText date;
    private TextView title,city,county,description;
    private DatabaseReference databaseReference, dbref;
    private DatabaseReference condb;
    private FirebaseUser user;
    private String location ="";
    private String profile;
    private boolean connections;
    private String locationRandomKey, downloadImageUrl;
    String tripsRandomKey;
    String user1,link;
    private String userID;
    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;
    private int PLACE_PICKER_REQUEST = 1;
    private Geocoder geocoder;
    private String name,countyName,username;
    private GoogleMap map;
    private OnMapReadyCallback onMapReadyCallback;
    private LatLng latLng;
    private String cords;
    private double latId, lngId, latId1, lngId1;
    private Toolbar toolbar;
    private TextView homeTitle;

    public LocationDetailsActivity(){

    }

    public LocationDetailsActivity(String link,String name, String countyName,String username, double latId, double lngId) {
        this.link=link;
        this.countyName = countyName;
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
                Intent intentMap = new Intent(LocationDetailsActivity.this, NearMeActivity.class);
                startActivity(intentMap);
                break;
            case R.id.home:
                Intent intentHome = new Intent(LocationDetailsActivity.this, HomeActivity.class);
                startActivity(intentHome);
                break;
            case R.id.View:
                Intent intentProfile = new Intent(LocationDetailsActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.Trips:
                Intent intentTrip = new Intent(LocationDetailsActivity.this, TripsActivity.class);
                startActivity(intentTrip);
                break;
            case R.id.Connections:
                Intent intentGroup = new Intent(LocationDetailsActivity.this, GroupChatActivity.class);
                //intentGroup.putExtra("lid",locations.getLid());
                startActivity(intentGroup);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(LocationDetailsActivity.this,MainActivity.class));
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Log.e("location is null","");
            Log.e("username is null", "");
        } else {
            location = extras.getString("lid");
            profile = extras.getString("username");
        }

        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Locations");
        databaseReference.child(location).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Locations locations = snapshot.getValue(Locations.class);
                if(locations!=null){
                    name = locations.getCity();
                    countyName = locations.getCounty();
                    latId = locations.getLat();
                    lngId = locations.getLng();
                    Log.e("test", String.valueOf(latId));
                    county.setText(countyName);
                }
                Log.e("test", "test");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();

        LocationImagesRef = FirebaseStorage.getInstance().getReference().child("Location Images");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        dbref = FirebaseDatabase.getInstance().getReference().child("Locations");
        condb = FirebaseDatabase.getInstance().getReference().child("Connections");

        addToTrip = (FloatingActionButton) findViewById(R.id.joinBtn);
        cancelBtn = (FloatingActionButton) findViewById(R.id.cancel_button);
        imageView = (ImageView) findViewById(R.id.location_image_details);
        linkImage = (ImageView) findViewById(R.id.linkImage);

        title = (TextView) findViewById(R.id.locationTitle);
        city = (TextView) findViewById(R.id.locationCity);
        county = (TextView) findViewById(R.id.location_county);
        description = (TextView) findViewById(R.id.location_description);

        getLocationDetails(location);
        linkImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserint = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(browserint);
            }
        });

        geocoder = new Geocoder(this);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.locMap);
        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(LocationDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
        else{
            ActivityCompat.requestPermissions(LocationDetailsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44 );
        }

        addToTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToTrip();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addingToTrip() {
        user1 = user.getUid();
        String groupId = location;
        User userprofile = new User();
        profile = userprofile.getUsername();
        String timestamp = ""+System.currentTimeMillis();
        final HashMap<String, Object> tripMap = new HashMap<>();

        tripMap.put("groupId", location);
        tripMap.put("title", location);
        //tripMap.put("image", downloadImageUrl);

        condb.child(location).updateChildren(tripMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        HashMap<String, Object> members = new HashMap<>();
                        members.put("user", user.getUid());
                        members.put("title",location);
                        //members.put("Username", profile);
                        Connections connections = new Connections();
                        connections.setCtitle(location);
                        //connections.setProfileName(user.getUid());
                        //connections.setProfileName(profile);
                        //Toast.makeText(LocationDetailsActivity.this, "test " + profile.toString(), Toast.LENGTH_SHORT).show();

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Connections").child(location);
                        db.child("members").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(members)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Toast.makeText(LocationDetailsActivity.this, "Groups Created", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }
                });

    }

    private void getLocationDetails(String location) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Locations");
        reference.child(location).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Locations locations = snapshot.getValue(Locations.class);
                    if(locations != null){
                        link = String.valueOf(snapshot.child("link").getValue());
                        Log.e("Link test", "" +  link);
                        title.setText(locations.getTitle());
                        city.setText(locations.getCity());
                        county.setText(locations.getCounty());
                        description.setText(locations.getDescription());
                        Picasso.get().load(locations.getImage()).into(imageView);
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
                                            .title(address.getLocality()).title("Current Location");
                                    map.addMarker(options);
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}