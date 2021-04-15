package com.example.fyp_staycation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_staycation.R;
import com.example.fyp_staycation.classes.Messages;
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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.HolderChat> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    private Context context;
    private List<Messages> messagesList;
    private List<Messages> items2;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private DatabaseReference reference;

    public ChatAdapter(Context context, List<Messages>messagesList){
        this.context=context;
        this.messagesList=messagesList;
        items2=new ArrayList<>(messagesList);

        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("User");
    }


    public void setItem(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    public List<Messages> getMessagesList() {
        return messagesList;
    }

    @NonNull
    @Override
    public HolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.row_group_chat_right,parent,false);
            return new HolderChat(view);
        }
        else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_group_chat_left,parent,false);
            return new HolderChat(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HolderChat holder, int position) {

        Messages messages = messagesList.get(position);

        String timestamp = messages.getTimestamp();
        String message = messages.getMessage();
        String senderId = messages.getSender();

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime =  DateFormat.format("dd/mm/yyyy hh:mm:ss", calendar).toString();

        /*reference.orderByChild(userID).equalTo(messages.getSender())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    String name = "" + ds.child("username").getValue();

                    holder.nameTv.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);
        //holder.nameTv.setText(senderId);
        setUserName(messages, holder);
    }

    private void setUserName(Messages messages, HolderChat holder) {

        userID = firebaseAuth.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.orderByChild(userID).equalTo(messages.getSender())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String name = "" + ds.child("username").getValue();

                            holder.nameTv.setText(name);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesList.get(position);


        if(messagesList.get(position).getSender().equals(firebaseAuth.getCurrentUser().getEmail())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }

    public static class HolderChat extends RecyclerView.ViewHolder{

        private TextView nameTv, messageTv, timeTv;
        public HolderChat(@NonNull View itemView) {
            super(itemView);

            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);

        }
    }


}
