package com.example.fyp_staycation.adapters;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_staycation.GroupChatActivity;
import com.example.fyp_staycation.ItemClickListener;
import com.example.fyp_staycation.R;
import com.example.fyp_staycation.classes.Connections;
import com.example.fyp_staycation.classes.Locations;
import com.example.fyp_staycation.classes.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.ViewHolder> {

    public Context context;
    private List<Connections> groupChatList;
    private List<Connections> items2;
    LayoutInflater layoutInflater;
    public ItemClickListener listener;

    public GroupChatAdapter(List<Connections> groupChatList) {
        super();

        this.groupChatList = groupChatList;
        items2=new ArrayList<>(groupChatList);
        //layoutInflater = LayoutInflater.from(context);
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
        holder.groupTitle.setText(connections.getGroupId());

        loadLastMessage(connections, holder);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void loadLastMessage(Connections connections, ViewHolder holder) {

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


