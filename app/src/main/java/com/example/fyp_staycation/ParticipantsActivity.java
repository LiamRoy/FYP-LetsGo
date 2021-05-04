package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_staycation.adapters.ChatAdapter;
import com.example.fyp_staycation.adapters.UserAdapter;
import com.example.fyp_staycation.classes.Locations;
import com.example.fyp_staycation.classes.Participants;
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

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParticipantsActivity extends AppCompatActivity {

    private FloatingActionButton join;
    private String tid;
    private FirebaseUser user;
    private DatabaseReference tripDB,userDB;
    private TextView title,dateTrip,homeTitle;
    private TextView timeTrip;
    private RecyclerView userRv;
    private ArrayList<Participants> userList;
    private UserAdapter userAdapter;
    private ImageView imageView, homeImg;
    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;
    private Button btnPicker;
    //private TextView latLng;
    private int PLACE_PICKER_REQUEST = 1;
    private Geocoder geocoder;
    private String name, countyName, username,meet;
    private String image;
    private GoogleMap map;
    private OnMapReadyCallback onMapReadyCallback;
    private double latId;
    private double lngId;
    private LatLng latLng;
    private Toolbar toolbar;

    public ParticipantsActivity(){

    }

    public ParticipantsActivity(String meet,double latId, double lngId, String name, String username, String image){
        this.meet=meet;
        this.latId=latId;
        this.lngId=lngId;
        this.name=name;
        this.username=username;
        this.image=image;
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
                Intent intentMap = new Intent(ParticipantsActivity.this, NearMeActivity.class);
                startActivity(intentMap);
                break;
            case R.id.home:
                Intent intentHome = new Intent(ParticipantsActivity.this, HomeActivity.class);
                startActivity(intentHome);
                break;
            case R.id.View:
                Intent intentProfile = new Intent(ParticipantsActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.Trips:
                Intent intentTrip = new Intent(ParticipantsActivity.this, TripsActivity.class);
                startActivity(intentTrip);
                break;
            case R.id.Connections:
                Intent intentGroup = new Intent(ParticipantsActivity.this, GroupChatActivity.class);
                //intentGroup.putExtra("lid",locations.getLid());
                startActivity(intentGroup);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ParticipantsActivity.this,MainActivity.class));
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        Bundle extras = getIntent().getExtras();
        //tripID = extras.getString("tid");
        Intent intent = getIntent();
        tid = intent.getStringExtra("tid");
        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        imageView=(ImageView)findViewById(R.id.profile_image_details);
        homeTitle=(TextView)findViewById(R.id.homeTitle);
        join = (FloatingActionButton) findViewById(R.id.joinButton);
        title = (TextView) findViewById(R.id.title);
        dateTrip=(TextView) findViewById(R.id.dateTrip);
        timeTrip = findViewById(R.id.meetTime);
        user = FirebaseAuth.getInstance().getCurrentUser();

        homeImg = findViewById(R.id.homeImg);
        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ParticipantsActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Trips");
        databaseReference.child(tid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trip trip = snapshot.getValue(Trip.class);
                if(trip!=null){

                    tid = trip.getTid();
                    Log.e("tripID test", tid);
                    name = trip.getAddress();
                    latId = trip.getLat();
                    lngId = trip.getLng();
                    meet = trip.getMeettime();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getUserDetails();
        getLocationDetails();
        getAllUsers();

        user = FirebaseAuth.getInstance().getCurrentUser();
        tripDB = FirebaseDatabase.getInstance().getReference().child("Trips");

        userRv = (RecyclerView) findViewById(R.id.userRv);
        userRv.setHasFixedSize(true);
        userRv.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<Participants>();

        geocoder = new Geocoder(this);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.tripMap);
        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(ParticipantsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(ParticipantsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinTrip();
            }
        });

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("User");
        db.child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user!=null){
                    username = user.getUsername();
                    image = user.getImage();
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
                if (location1 != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            map = googleMap;

//                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//                            MarkerOptions options = new MarkerOptions().position(latLng).title("Current Location");
                            //DONT FORGET TO FIX THIS!!!
//                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
//                            googleMap.addMarker(options);
                            try {
                                List<Address> addressList = geocoder.getFromLocation(latId, lngId, 1);
                                if (addressList.size() > 0) {
                                    Address address = addressList.get(0);
                                    latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                    MarkerOptions options = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude()))
                                            .title(address.getLocality()).title("Meet Up Point");
                                    map.addMarker(options);
                                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    private void getAllUsers() {

        userList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trips");
        reference.child(tid).child("Participants")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            Participants participants = ds.getValue(Participants.class);
                            //if(list.getUid().equals(user.getUid())) {
                                userList.add(participants);
                                //userList.add(list);
                            //}
                        }

                        userAdapter = new UserAdapter(ParticipantsActivity.this,userList);
                        //userAdapter.notifyDataSetChanged();
                        userRv.setAdapter(userAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void joinTrip() {

        HashMap<String, Object> members = new HashMap<>();
        members.put("userEmail", user.getEmail());
        members.put("uid", user.getUid());
        members.put("username", username);
        members.put("image", image);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Trips").child(tid);
        db.child("Participants").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(members)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(ParticipantsActivity.this, "Added to Trip", Toast.LENGTH_SHORT).show();
                        userAdapter.notifyDataSetChanged();
                        //userRv.setAdapter(userAdapter);
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

    private void getLocationDetails() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trips");
        reference.orderByChild("tid").equalTo(tid)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String Title = "" + ds.child("tripTitle").getValue();
                    String date = "Date of Trip: " + ds.child("date").getValue();
                    String time = "Meeting Time: " + ds.child("meettime").getValue();
                    title.setText(Title);
                    dateTrip.setText(date);
                    timeTrip.setText(time);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}