package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_staycation.classes.Connections;
import com.example.fyp_staycation.classes.Locations;
import com.example.fyp_staycation.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class LocationDetailsActivity extends AppCompatActivity {

    private Uri ImageUri;
    private StorageReference LocationImagesRef;
    private Button addToTrip;
    private ImageView imageView;
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
    String user1;
    private String userID;

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

        user = FirebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        dbref = FirebaseDatabase.getInstance().getReference().child("Locations");
        condb = FirebaseDatabase.getInstance().getReference().child("Connections");

        addToTrip = (Button) findViewById(R.id.joinBtn);
        imageView = (ImageView) findViewById(R.id.location_image_details);

        title = (TextView) findViewById(R.id.locationTitle);
        city = (TextView) findViewById(R.id.locationCity);
        county = (TextView) findViewById(R.id.location_county);
        description = (TextView) findViewById(R.id.location_description);

        getLocationDetails(location);
        addToTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addingToTrip();
            }
        });


        boolean connection = false;
    }

    private void addingToTrip() {
        user1 = user.getUid();

        User userprofile = new User();
        profile = userprofile.getUsername();

        final HashMap<String, Object> tripMap = new HashMap<>();

        tripMap.put("groupId", location);

        condb.child(location).updateChildren(tripMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        HashMap<String, Object> members = new HashMap<>();
                        members.put("User", user.getUid());
                        //members.put("Username", profile);
                        Connections connections = new Connections();
                        connections.setGroupId(location);
                        //connections.setProfileName(profile);
                        //Toast.makeText(LocationDetailsActivity.this, "test " + profile.toString(), Toast.LENGTH_SHORT).show();

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Connections").child(location);
                        db.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(members)
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
}