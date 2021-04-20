package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_staycation.adapters.GroupChatAdapter;
import com.example.fyp_staycation.classes.Connections;
import com.example.fyp_staycation.classes.Locations;
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

public class GroupChatActivity extends AppCompatActivity {

    private RecyclerView groupsRv;
    private FirebaseAuth firebaseAuth;
    private List<Connections> items;
    private GroupChatAdapter groupChatAdapter;
    private DatabaseReference databaseReference;
    private TextView groupTitle;
    FirebaseRecyclerAdapter<Connections, GroupChatAdapter.ViewHolder> adapter;
    private LinearLayoutManager manager;

    public void GroupChatAdapter(List<Connections> items) {
        this.items = items;
    }

    Connections connections;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Connections");
        groupTitle = findViewById(R.id.groupTitle);
        groupsRv = findViewById(R.id.rvGroup);
        firebaseAuth = FirebaseAuth.getInstance();

        connections = new Connections();

        //addList();
        //loadGroupChatList();


        //groupsRv.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Connections> options =
                new FirebaseRecyclerOptions.Builder<Connections>()
                        .setQuery(databaseReference, Connections.class)
                        .build();

        FirebaseRecyclerAdapter<Connections, GroupChatAdapter.ViewHolder> adapter =
                new FirebaseRecyclerAdapter <Connections, GroupChatAdapter.ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull GroupChatAdapter.ViewHolder holder, int position, @NonNull Connections model) {

                       //Toast.makeText(GroupChatActivity.this, model.getGroupId().toString(),Toast.LENGTH_SHORT).show();
                        holder.groupTitle.setText(model.getGroupId());
                        holder.name.setText(model.getProfileName());
                        holder.time.setText(model.getTime());
                        holder.message.setText(model.getMessage());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(GroupChatActivity.this,ChatActivity.class);
                                intent.putExtra("groupId",model.getGroupId());
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public GroupChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_groupchats_list, parent, false);
                        GroupChatAdapter.ViewHolder holder = new GroupChatAdapter.ViewHolder(view);
                        return holder;
                    }

                };
        groupChatAdapter = new GroupChatAdapter(addList());
        groupChatAdapter.notifyDataSetChanged();
        groupsRv.setAdapter(adapter);
        adapter.startListening();

    }

    private List<Connections> addList() {

        List<Connections> items = new ArrayList<>();

        items.add(new Connections());

        return items;

    }
   private void loadGroupChatList() {

       items=new ArrayList<>();
       databaseReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot ds: snapshot.getChildren()){
                   items.clear();
                   if(!ds.child("Members").child(firebaseAuth.getUid()).exists()){
                       Connections connections = ds.getValue(Connections.class);
                       String Title = "" + ds.child("groupId").getValue();
                       //groupTitle.setText(Title);
                       items.add(connections);
                   }
               }
               groupChatAdapter = new GroupChatAdapter(items);
               groupsRv.setAdapter(groupChatAdapter);

               // = new GroupChatAdapter(addList());
               //groupsRv.setAdapter(groupChatAdapter);
           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

}