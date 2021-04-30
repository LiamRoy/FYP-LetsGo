package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.format.DateFormat;
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

import com.example.fyp_staycation.adapters.ChatAdapter;
import com.example.fyp_staycation.adapters.GroupChatAdapter;
import com.example.fyp_staycation.adapters.UserAdapter;
import com.example.fyp_staycation.classes.Connections;
import com.example.fyp_staycation.classes.Locations;
import com.example.fyp_staycation.classes.Messages;
import com.example.fyp_staycation.classes.Participants;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GroupChatActivity extends AppCompatActivity {

    private RecyclerView groupsRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Connections> groupChatList;
    private GroupChatAdapter groupChatAdapter;
    private GroupChatAdapter.ViewHolder holder;
    private DatabaseReference databaseReference;
    private TextView groupTitle;
    FirebaseRecyclerAdapter<Connections, GroupChatAdapter.ViewHolder> adapter;
    private LinearLayoutManager manager;
    private String groupId, location;
    private Context context;
    private ImageView imageView;
    private Messages messages;
    private Toolbar toolbar;
    private TextView homeTitle;


    public GroupChatActivity(){

    }
    public GroupChatActivity(String groupId){
        this.groupId=groupId;
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
                Intent intentMap = new Intent(GroupChatActivity.this, NearMeActivity.class);
                startActivity(intentMap);
                break;
            case R.id.home:
                Intent intentHome = new Intent(GroupChatActivity.this, HomeActivity.class);
                startActivity(intentHome);
                break;
            case R.id.View:
                Intent intentProfile = new Intent(GroupChatActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.Trips:
                Intent intentTrip = new Intent(GroupChatActivity.this, TripsActivity.class);
                startActivity(intentTrip);
                break;
            case R.id.Connections:
                Intent intentGroup = new Intent(GroupChatActivity.this, GroupChatActivity.class);
                //intentGroup.putExtra("lid",locations.getLid());
                startActivity(intentGroup);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(GroupChatActivity.this,MainActivity.class));
        }

        return true;
    }

    private Connections connections;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("trip is null", "");
        } else {
            location = extras.getString("lid");

        }

        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        groupChatList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Connections");
        groupTitle = findViewById(R.id.groupTitle);
        groupsRv = findViewById(R.id.rvGroup);
        firebaseAuth = FirebaseAuth.getInstance();
        //imageView = (ImageView) findViewById(R.id.groupImage);
        groupChatAdapter = new GroupChatAdapter(GroupChatActivity.this, groupChatList);
        groupsRv.setAdapter(groupChatAdapter);
        //loadConnectionsInfo();
        connections = new Connections();
        messages = new Messages();
        loadGroupChatList();
        //loadLastMessage(messages, holder);

//        addList();
//        groupsRv.setHasFixedSize(true);
//        groupsRv.setLayoutManager(new LinearLayoutManager(this));
//        groupsRv.setAdapter(groupChatAdapter);
//        ;
        //groupsRv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadConnectionsInfo() {
        //groupId = extras.getString("groupId");
        connections = new Connections();

        databaseReference.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Connections connections = snapshot.getValue(Connections.class);
                    //String keys=datas.getKey();
                    if (connections != null) {
                        groupId = connections.getCtitle();
                    }
                    Log.e("id test", groupId + " is here");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadLastMessage(Messages messages, GroupChatAdapter.ViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Connections");
        databaseReference.child("Messages").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String groupId = snapshot.getKey();
                        Log.e("key test", ""+ groupId);
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String message = "" + ds.child("message").getValue();
                            Log.e("message test", ""+ message);
                            String timestamp = "" + ds.child("timestamp").getValue();
                            String sender = "" + ds.child("sender").getValue();

                            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                            calendar.setTimeInMillis(Long.parseLong(timestamp));
                            String dateTime =  DateFormat.format("dd/mm/yyyy hh:mm:ss", calendar).toString();

                            holder.message.setText(message);
                            holder.name.setText(sender);
                            holder.time.setText(dateTime);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
                            reference.orderByChild("uid").equalTo(sender)
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot ds: snapshot.getChildren()){
                                                String name1 = ""+ds.child("email").getValue();
                                                holder.name.setText(name1);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private List<Connections> addList() {

        List<Connections> items = new ArrayList<>();
        items.add(new Connections());
        return items;

    }
    private void loadGroupChatList() {
        groupChatList=new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatList.clear();
                for(DataSnapshot ds: snapshot.getChildren()) {
                    if (ds.child("members").child(firebaseAuth.getCurrentUser().getUid()).exists()) {
                        String Title = "" + ds.child("title").getValue();
                        String members = "" + ds.child("members").getValue();
                        String messages = "" + ds.child("Messages").getValue();
                        connections = new Connections(Title,messages,members);
                        //groupChatList.clear();
                        groupChatList.add(connections);
                    }
                }
                groupChatAdapter = new GroupChatAdapter(GroupChatActivity.this, groupChatList);
                groupsRv.setAdapter(groupChatAdapter);
                groupChatAdapter.notifyDataSetChanged();
                // = new GroupChatAdapter(addList());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}