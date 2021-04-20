package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_staycation.adapters.ChatAdapter;
import com.example.fyp_staycation.adapters.UserAdapter;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParticipantsActivity extends AppCompatActivity {

    private Button join;
    private String tripID;
    private FirebaseUser user;
    private DatabaseReference tripDB;
    private TextView title,lat,lng;
    private RecyclerView userRv;
    private ArrayList<Participants> userList;
    private UserAdapter userAdapter;

    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;
    private Button btnPicker;
    //private TextView latLng;
    private int PLACE_PICKER_REQUEST = 1;
    private Geocoder geocoder;
    private String name, countyName;
    private GoogleMap map;
    private OnMapReadyCallback onMapReadyCallback;
    private double latId;
    private double lngId;
    private LatLng latLng;

    public ParticipantsActivity(){

    }

    public ParticipantsActivity(double latId, double lngId, String name){
        this.latId=latId;
        this.lngId=lngId;
        this.name=name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("trip is null", "");
        } else {
            tripID = extras.getString("tid");
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Trips");
        databaseReference.child(tripID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trip trip = snapshot.getValue(Trip.class);
                if(trip!=null){

                    name = trip.getAddress();
                    latId = trip.getLat();
                    lngId = trip.getLng();
                    Log.e("test", String.valueOf(latId));
                    Log.e("test", String.valueOf(lngId));
                    Log.e("test", name);
                }
                Log.e("test", "test");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getLocationDetails();
        getAllUsers();

        user = FirebaseAuth.getInstance().getCurrentUser();
        tripDB = FirebaseDatabase.getInstance().getReference().child("Trips");

        join = (Button) findViewById(R.id.joinButton);
        title = (TextView) findViewById(R.id.title);

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
                            Log.e("test", name);
//                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
//                            MarkerOptions options = new MarkerOptions().position(latLng).title("Current Location");
                            //DONT FORGET TO FIX THIS!!!
//                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
//                            googleMap.addMarker(options);
                            try {
                                List<Address> addressList = geocoder.getFromLocationName(name,1);
                                if (addressList.size() > 0) {
                                    Address address = addressList.get(0);
                                    latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                    MarkerOptions options = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude()))
                                            .title(address.getLocality());
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
        reference.child(tripID).child("Participants")
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
                        Log.e("test", String.valueOf(userList.size()));
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

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Trips").child(tripID);
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

    private void getLocationDetails() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trips");
        reference.orderByChild("tid").equalTo(tripID)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String Title = "" + ds.child("tripTitle").getValue();
                    title.setText(Title);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}