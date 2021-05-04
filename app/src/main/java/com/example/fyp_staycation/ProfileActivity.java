package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_staycation.classes.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private Button logout;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    private ImageView InputProductImage,profileImage, homeBtn;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String locationRandomKey, downloadImageUrl;
    private StorageReference profileImagesRef;
    private ProgressDialog loadingBar;
    private Toolbar toolbar;
    private TextView homeTitle;

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
                Intent intentMap = new Intent(ProfileActivity.this, NearMeActivity.class);
                startActivity(intentMap);
                break;
            case R.id.home:
                Intent intentHome = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intentHome);
                break;
            case R.id.View:
                Intent intentProfile = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.Trips:
                Intent intentTrip = new Intent(ProfileActivity.this, TripsActivity.class);
                startActivity(intentTrip);
                break;
            case R.id.Connections:
                Intent intentGroup = new Intent(ProfileActivity.this, GroupChatActivity.class);
                //intentGroup.putExtra("lid",locations.getLid());
                startActivity(intentGroup);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("User");
        userID = user.getUid();
        profileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingBar = new ProgressDialog(this);

        homeTitle = (TextView) findViewById(R.id.homeTitle);
        InputProductImage = (ImageView) findViewById(R.id.profile_image_details);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        //final TextView profiletext = (TextView) findViewById(R.id.txtProfile);
        final TextView usernametext = (TextView) findViewById(R.id.profileUsername);
        final TextView emailtext = (TextView) findViewById(R.id.profileEmail);
        final TextView descriptionText = (TextView) findViewById(R.id.profileDescription);
        final TextView phonetext = (TextView) findViewById(R.id.profileNumber);
        homeBtn = (ImageView) findViewById(R.id.homeImg);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if(userProfile != null){
                    String username = userProfile.getUsername();
                    String email = userProfile.getEmail();
                    String description = userProfile.getDescription();
                    String phoneNum = userProfile.getPhoneNum();
                    Picasso.get().load(userProfile.getImage()).into(profileImage);
                    Picasso.get().load(userProfile.getImage()).into(InputProductImage);

                    //profiletext.setText("Welcome " + username);
                    usernametext.setText(username);
                    homeTitle.setText("Hi " + username);
                    emailtext.setText(email);
                    descriptionText.setText(description);
                    phonetext.setText(phoneNum);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void OpenGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            profileImage.setImageURI(ImageUri);
        }
        storeInformation();
    }

    private void storeInformation() {

        loadingBar.setTitle("Add New Profile Image");
        loadingBar.setMessage("Dear User, please wait while we are adding the new Image to your Account.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        locationRandomKey = userID;
        final StorageReference filePath = profileImagesRef.child(ImageUri.getLastPathSegment() + locationRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(ProfileActivity.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(ProfileActivity.this, "got the profile image Url Successfully...", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }

    private void SaveProductInfoToDatabase() {

        HashMap<String, Object> profileMap = new HashMap<>();
        profileMap.put("image", downloadImageUrl);

        reference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(profileMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    loadingBar.dismiss();
                    Toast.makeText(ProfileActivity.this, "Profile Image is added successfully..", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    loadingBar.dismiss();
                    String message = task.getException().toString();
                    Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}