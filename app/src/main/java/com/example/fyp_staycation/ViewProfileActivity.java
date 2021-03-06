package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fyp_staycation.classes.Locations;
import com.example.fyp_staycation.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ViewProfileActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID,profileId;
    private ImageView profileImage,homeImg,imageView;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String locationRandomKey, downloadImageUrl;
    private StorageReference profileImagesRef;
    private ProgressDialog loadingBar;
    private Toolbar toolbar;
    private TextView homeTitle,usernameTxt,emailTxt,phoneTxt,descTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Log.e("username is null", "");
        } else {
            profileId = extras.getString("uid");
        }

        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        imageView=(ImageView) findViewById(R.id.profile_image_details);
        homeImg = findViewById(R.id.homeImg);
        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfileActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User");
        userID = user.getUid();
        profileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingBar = new ProgressDialog(this);

        homeTitle = (TextView) findViewById(R.id.homeTitle);
        profileImage = (ImageView) findViewById(R.id.profileImage);

        usernameTxt=(TextView) findViewById(R.id.profileUsername);
        emailTxt=(TextView) findViewById(R.id.profileEmail);
        phoneTxt=(TextView)findViewById(R.id.profileNumber);
        descTxt=(TextView)findViewById(R.id.profileDescription);

        getUserDetails(profileId);
        getCurrentUser();
    }

    private void getCurrentUser() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user!=null){
                    String name = user.getUsername();
                    if(user.getImage()!=null) {
                        String image = user.getImage();
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

    private void getUserDetails(String profileId) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User");
        reference.child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    User user1 = snapshot.getValue(User.class);
                    if(user1 != null){
                        usernameTxt.setText(user1.getUsername());
                        emailTxt.setText(user1.getEmail());
                        phoneTxt.setText(user1.getPhoneNum());
                        descTxt.setText(user1.getDescription());
                        Picasso.get().load(user1.getImage()).into(profileImage);
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