package com.example.fyp_staycation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fyp_staycation.adapters.GroupChatAdapter;
import com.example.fyp_staycation.classes.Connections;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GroupChatFragment extends Fragment {

    private RecyclerView groupsRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Connections> groupChatList;
    private GroupChatAdapter groupChatAdapter;
    private DatabaseReference databaseReference;

    public GroupChatFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_group_chat, container, false);

        groupsRv = view.findViewById(R.id.rvGroup);

        firebaseAuth = FirebaseAuth.getInstance();

        loadGroupChatList();

        return view;
    }

    private void loadGroupChatList() {

        groupChatList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Connections");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatList.size();
                for(DataSnapshot ds: snapshot.getChildren()){
                    if(!ds.child(firebaseAuth.getCurrentUser().getUid()).exists()){
                        Connections connections = ds.getValue(Connections.class);
                        groupChatList.add(connections);
                    }
                }
                //groupChatAdapter = new GroupChatAdapter(, groupChatList);
                groupsRv.setAdapter(groupChatAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}