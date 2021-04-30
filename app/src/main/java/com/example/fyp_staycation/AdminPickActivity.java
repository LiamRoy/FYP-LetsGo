package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdminPickActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener {

    private String category, Title, City, County, Lname, saveCurrentDate, saveCurrentTime, description,link;
    private Button AddNewLocationButton;
    private ImageView InputProductImage;
    private EditText InputTitleName, InputCity, InputCounty, InputCategory, InputDescription,InputLink;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String locationRandomKey, downloadImageUrl;
    private StorageReference LocationImagesRef;
    private DatabaseReference LocationRef, CountyRef;
    private ProgressDialog loadingBar;

    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;
    private int PLACE_PICKER_REQUEST = 1;
    private Geocoder geocoder;
    private String name,countyName,username;
    private GoogleMap map;
    private OnMapReadyCallback onMapReadyCallback;
    private String cords;
    private LatLng latLng1;
    private double latId,lngId;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_pick);
        LocationImagesRef = FirebaseStorage.getInstance().getReference().child("Location Images");
        LocationRef = FirebaseDatabase.getInstance().getReference().child("Locations");
        CountyRef = FirebaseDatabase.getInstance().getReference().child("County");



        AddNewLocationButton = (Button) findViewById(R.id.btnadd);
        InputProductImage = (ImageView) findViewById(R.id.select_product_image);

        InputTitleName = (EditText) findViewById(R.id.title);
        InputCategory = (EditText) findViewById(R.id.Category);
        InputCity = (EditText) findViewById(R.id.city);
        InputCounty = (EditText) findViewById(R.id.county);
        InputDescription = (EditText) findViewById(R.id.description);
        InputLink=(EditText)findViewById(R.id.link);
        loadingBar = new ProgressDialog(this);


        geocoder = new Geocoder(this);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.tripMap);
        client = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(AdminPickActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
        else{
            ActivityCompat.requestPermissions(AdminPickActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44 );
        }

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });

        AddNewLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                ValidateProductData();
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
                            //Log.e("test", String.valueOf(county));
                             LatLng latLng = new LatLng(location1.getLatitude(), location1.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(latLng).title("Current Location");
                            //DON'T FORGET TO FIX THIS!!!
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            googleMap.addMarker(options);

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
                                            MarkerOptions options = new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude()))
                                                    .title(address.getLocality());
                                            map.addMarker(options);
                                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 15));
                                            latId = latLng1.latitude;
                                            lngId = latLng1.longitude;
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

    private void OpenGallery()
    {
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
            InputProductImage.setImageURI(ImageUri);
        }
    }


    private void ValidateProductData()
    {
        category = InputCategory.getText().toString();
        Title = InputTitleName.getText().toString();
        City = InputCity.getText().toString();
        County = InputCounty.getText().toString();
        description = InputDescription.getText().toString();
        link = InputLink.getText().toString();

        if (ImageUri == null)
        {
            Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Title))
        {
            Toast.makeText(this, "Please write Title...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(City))
        {
            Toast.makeText(this, "Please write City name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(County))
        {
            Toast.makeText(this, "Please write County name...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(category))
        {
            Toast.makeText(this, "Please write Category...", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Please write a Description",  Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(link))
        {
            Toast.makeText(this, "Please enter a Link",  Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoreProductInformation();
        }
    }



    private void StoreProductInformation()
    {
        loadingBar.setTitle("Add New Location");
        loadingBar.setMessage("Dear Admin, please wait while we are adding the new Location.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        locationRandomKey = Title;


        final StorageReference filePath = LocationImagesRef.child(ImageUri.getLastPathSegment() + locationRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(AdminPickActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AdminPickActivity.this, "Location Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

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

                            Toast.makeText(AdminPickActivity.this, "got the Location image Url Successfully...", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }



    private void SaveProductInfoToDatabase()
    {
        HashMap<String, Object> locationMap = new HashMap<>();
        locationMap.put("lid", locationRandomKey);
        locationMap.put("date", saveCurrentDate);
        locationMap.put("time", saveCurrentTime);
        locationMap.put("title", Title);
        locationMap.put("image", downloadImageUrl);
        locationMap.put("city", City);
        locationMap.put("county", County);
        locationMap.put("category", category);
        locationMap.put("description", description);
        locationMap.put("link", link);
        Log.e("latID", ""+ latId);
        locationMap.put("lat", latId);
        locationMap.put("lng", lngId);

        
        LocationRef.child(locationRandomKey).updateChildren(locationMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AdminPickActivity.this, AdminActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminPickActivity.this, "Location is added successfully..", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AdminPickActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        //RUNNING MENU2.XML OVER ACTIVITY
        inflater.inflate(R.menu.menu2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(AdminPickActivity.this,MainActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }
}