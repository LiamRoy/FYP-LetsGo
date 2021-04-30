package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_staycation.adapters.GroupChatAdapter;
import com.example.fyp_staycation.adapters.TripAdapter;
import com.example.fyp_staycation.adapters.UserAdapter;
import com.example.fyp_staycation.classes.Connections;
import com.example.fyp_staycation.classes.Locations;
import com.example.fyp_staycation.classes.Participants;
import com.example.fyp_staycation.classes.Trip;
import com.example.fyp_staycation.classes.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TripsActivity extends AppCompatActivity {

    private RecyclerView groupsRv;
    private FirebaseAuth firebaseAuth;
    private List<Trip> items;
    private ArrayList<Trip> tripArrayList;
    private TripAdapter tripAdapter;
    private DatabaseReference databaseReference;
    private TextView groupTitle, tripDate, tripCreated, homeTitle;
    FirebaseRecyclerAdapter<Trip, TripAdapter.ViewHolder> adapter;
    private ImageView imageView;
    private LinearLayoutManager manager;
    private ArrayList<Participants> userList;
    Trip trips;
    private String tripID,time;
    private Toolbar toolbar;
    private FirebaseUser user;

    public void TripAdapter(List<Trip> items) {
        this.items = items;
    }
    public TripsActivity(){}
    public TripsActivity(String tripID,String time){this.tripID=tripID;
    this.time=time;}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //RUNNING MENU2.XML OVER ACTIVITY
        inflater.inflate(R.menu.trip_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.showMyTrips:
                Intent intentHome = new Intent(TripsActivity.this, MyTripActivity.class);
                startActivity(intentHome);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(TripsActivity.this,MainActivity.class));
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("trip is null", "");
        } else {
            tripID = extras.getString("tid");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Trips");
        groupTitle = findViewById(R.id.groupTitle);
        groupsRv = findViewById(R.id.rvGroup);
        //imageView = findViewById(R.id.star);

        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        imageView=(ImageView)findViewById(R.id.profile_image_details);
        user = FirebaseAuth.getInstance().getCurrentUser();
        homeTitle=(TextView)findViewById(R.id.homeTitle);

        getUserDetails();

        firebaseAuth = FirebaseAuth.getInstance();

        /*addList();
        loadGroupChatList();*/
        tripArrayList = new ArrayList<Trip>();
        tripAdapter = new TripAdapter(TripsActivity.this,tripArrayList);
        groupsRv.setAdapter(tripAdapter);
        //loadTrips();



        //groupsRv.setLayoutManager(new LinearLayoutManager(this));
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

    private void loadTrips() {
        tripArrayList=new ArrayList<>();
        databaseReference.orderByChild("tid").equalTo(tripID)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()) {
                        String tripTitle = "" + ds.child("tripTitle").getValue();
                        String date = "Date of Trip: " + ds.child("date").getValue();
                        String createdBy = "Trip Created by: " + ds.child("createdBy").getValue();
                        String time = "Meeting Time: " + ds.child("meetingTime").getValue();
                        tripID = ""+ds.child("tid").getValue();
                        trips = new Trip(tripTitle,date,createdBy,time,tripID);
                        //groupChatList.clear();
                        tripArrayList.add(trips);
                }
                tripAdapter = new TripAdapter(TripsActivity.this,tripArrayList);
                groupsRv.setAdapter(tripAdapter);
                tripAdapter.notifyDataSetChanged();
                // = new GroupChatAdapter(addList());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Trip> options =
                new FirebaseRecyclerOptions.Builder<Trip>()
                        .setQuery(databaseReference, Trip.class)
                        .build();

        FirebaseRecyclerAdapter<Trip, TripAdapter.ViewHolder> adapter =
                new FirebaseRecyclerAdapter <Trip, TripAdapter.ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull TripAdapter.ViewHolder holder, int position, @NonNull Trip model) {


                        Log.e("Trip id test", ""+ model.getTid());
                        Log.e("meeting time is ", ""+model.getMeettime());
                        holder.tripTitle.setText(model.getTripTitle());
                        holder.date.setText("Date of Trip: " + model.getDate());
                        holder.createdBy.setText("Trip Created by: " + model.getCreatedBy());
                        holder.tripTime.setText("Meeting Time: " + model.getMeettime());
                        holder.tripId.setText("ID: " + model.getTid());
                        imageView = findViewById(R.id.star);
                        tripID = model.getTid();

                        getAllUsers();

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(TripsActivity.this,ParticipantsActivity.class);
                                intent.putExtra("tid",model.getTid());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public TripAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_trip_list, parent, false);
                        TripAdapter.ViewHolder holder = new TripAdapter.ViewHolder(view);
                        return holder;
                    }

                };
        tripAdapter = new TripAdapter(TripsActivity.this,addList());
        groupsRv.setAdapter(adapter);
        adapter.startListening();

    }

    private List<Trip> addList() {

        List<Trip> items = new ArrayList<>();
        items.add(new Trip());
        return items;

    }

    private void getAllUsers() {

        userList = new ArrayList<>();
        //Log.e("size of users test 1", ""+ userList.size());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Trips");
        reference.child(tripID).child("Participants")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String username = "" + ds.child("username").getValue();
                            //if(list.getUid().equals(user.getUid())) {
                            Participants participants = new Participants(username);
                            userList.add(participants);
                            for(Participants participants1 : userList) {
                                Log.e("test", ""+username);
                                if(userList.size()>=2){
                                    Log.e("users are bigger than 2", ""+ userList.size());
                                    imageView.setVisibility(View.VISIBLE);
                                }
                            }
                            //userList.add(list);
                            //}
                        }
                        Log.e("size of users", ""+ userList.size());


                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

}