package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fyp_staycation.adapters.GroupChatAdapter;
import com.example.fyp_staycation.adapters.TripAdapter;
import com.example.fyp_staycation.classes.Connections;
import com.example.fyp_staycation.classes.Trip;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyTripActivity extends AppCompatActivity {

    private RecyclerView groupsRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Trip> tripArrayList;
    private TripAdapter tripAdapter;
    private DatabaseReference databaseReference;
    private TextView groupTitle, tripDate, tripCreated;
    FirebaseRecyclerAdapter<Trip, TripAdapter.ViewHolder> adapter;
    private LinearLayoutManager manager;
    private Trip trips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Trips");
        groupTitle = findViewById(R.id.groupTitle);
        groupsRv = findViewById(R.id.rvGroup);

        trips = new Trip();
        tripArrayList = new ArrayList<Trip>();
        firebaseAuth = FirebaseAuth.getInstance();
        tripAdapter = new TripAdapter(MyTripActivity.this,tripArrayList);
        groupsRv.setAdapter(tripAdapter);
        loadMyTrips();
        //groupsRv.setLayoutManager(new LinearLayoutManager(this));
    }

    /*@Override
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

                        //Toast.makeText(GroupChatActivity.this, model.getGroupId().toString(),Toast.LENGTH_SHORT).show();
                        holder.tripTitle.setText(model.getTripTitle());
                        holder.date.setText("Date of Trip: " + model.getDate());
                        holder.createdBy.setText("Trip Created by: " + model.getCreatedBy());
                        holder.name.setText(model.getFirebaseUser().getUid());
                        holder.time.setText(model.getTime());
                        holder.message.setText(model.getMessage());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MyTripActivity.this,ParticipantsActivity.class);
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
        tripAdapter = new TripAdapter(addList());
        groupsRv.setAdapter(adapter);
        adapter.startListening();

    }*/

    private void loadMyTrips() {
        tripArrayList=new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tripArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()) {
                    if (ds.child("Participants").child(firebaseAuth.getCurrentUser().getUid()).exists()) {
                        String tripTitle = "" + ds.child("tripTitle").getValue();
                        String date = "Date of Trip: " + ds.child("date").getValue();
                        String createdBy = "Trip Created by: " + ds.child("createdBy").getValue();
                        String time = "Meeting Time: " + ds.child("meettime").getValue();
                        String tripId = "ID: " + ds.child("tid").getValue();
                        trips = new Trip(tripTitle,date,createdBy,time,tripId);
                        //groupChatList.clear();
                        tripArrayList.add(trips);
                    }
                }
                tripAdapter = new TripAdapter(MyTripActivity.this,tripArrayList);
                groupsRv.setAdapter(tripAdapter);
                tripAdapter.notifyDataSetChanged();
                // = new GroupChatAdapter(addList());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private List<Trip> addList() {
        List<Trip> items = new ArrayList<>();
        items.add(new Trip());
        return items;
    }
}