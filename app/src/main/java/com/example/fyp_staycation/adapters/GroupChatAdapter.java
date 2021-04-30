package com.example.fyp_staycation.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_staycation.ChatActivity;
import com.example.fyp_staycation.GroupChatActivity;
import com.example.fyp_staycation.ItemClickListener;
import com.example.fyp_staycation.R;
import com.example.fyp_staycation.classes.Connections;
import com.example.fyp_staycation.classes.Locations;
import com.example.fyp_staycation.classes.Participants;
import com.example.fyp_staycation.classes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static androidx.core.content.ContextCompat.startActivity;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {

    private android.content.Context context;
    private List<Connections> groupChatList;
    private List<Connections> items2;
    private ItemClickListener listener;
    private Connections connections = new Connections();
    private FirebaseAuth firebaseAuth;

    public GroupChatAdapter(Context context, List<Connections> groupChatList){
        this.context=context;
        this.groupChatList=groupChatList;
        //items2=new ArrayList<>(groupChatList);
    }

    public void setItem(List<Connections> groupChatList) {
        this.groupChatList = groupChatList;
    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    public List<Connections> getItem() {
        return groupChatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_groupchats_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Connections connections = groupChatList.get(position);
        final String groupId = connections.getGroupId();

        holder.groupTitle.setText(connections.getGroupId());
        //holder.name.setText(connections.getProfileName());

        holder.name.setText("");
        holder.time.setText("");
        holder.message.setText("");

        loadLastMessage(connections, holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("groupId",connections.getGroupId());
                startActivity(context, intent, null);
            }
        });
    }

    private void loadLastMessage(Connections connections, ViewHolder holder) {

        firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Connections");
        databaseReference.child(connections.getGroupId()).child("Messages").limitToLast(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String message = "" + ds.child("message").getValue();
                            String timestamp = "" + ds.child("timestamp").getValue();
                            String sender = "" + ds.child("sender").getValue();

                            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                            calendar.setTimeInMillis(Long.parseLong(timestamp));
                            String dateTime =  DateFormat.format("dd/mm/yyyy hh:mm:ss", calendar).toString();

                            holder.message.setText(message);
                            holder.name.setText(sender);
                            holder.time.setText(dateTime);

//                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
//                            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    User user = snapshot.getValue(User.class);
//                                    if(user!=null){
//                                        String username;
//                                        username = user.getUsername();
//                                        Log.e("test", username);
//                                        holder.name.setText(username);
//                                    }
//                                    Log.e("test", "test");
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

    }



    @Override
    public int getItemCount() {
        return groupChatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView groupTitle, name, message, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groupTitle = itemView.findViewById(R.id.groupTitle);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }
    }

}


