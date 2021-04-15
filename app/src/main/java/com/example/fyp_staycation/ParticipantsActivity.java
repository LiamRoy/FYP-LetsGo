package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_staycation.adapters.ChatAdapter;
import com.example.fyp_staycation.adapters.UserAdapter;
import com.example.fyp_staycation.classes.Participants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParticipantsActivity extends AppCompatActivity {

    private Button join;
    private String tripID;
    private FirebaseUser user;
    private DatabaseReference tripDB;
    private TextView title;
    private RecyclerView userRv;
    private ArrayList<Participants> userList;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            Log.d("location is null","");
        } else {
            tripID = extras.getString("tid");
        }

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



        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinTrip();
            }
        });
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
                        Toast.makeText(ParticipantsActivity.this, "Groups Created", Toast.LENGTH_SHORT).show();
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