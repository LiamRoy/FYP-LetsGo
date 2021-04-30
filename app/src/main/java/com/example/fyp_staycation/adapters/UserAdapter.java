package com.example.fyp_staycation.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_staycation.ItemClickListener;
import com.example.fyp_staycation.ParticipantsActivity;
import com.example.fyp_staycation.R;
import com.example.fyp_staycation.ViewProfileActivity;
import com.example.fyp_staycation.classes.Messages;
import com.example.fyp_staycation.classes.Participants;
import com.example.fyp_staycation.classes.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.HolderUser>{

    private Context context;
    private List<Participants> userList;
    private List<Participants> items2;

    public UserAdapter(Context context, List<Participants> userList){
        this.context=context;
        this.userList=userList;
        items2=new ArrayList<>(userList);

        //firebaseAuth = FirebaseAuth.getInstance();
        //reference = FirebaseDatabase.getInstance().getReference().child("User");
    }

    public List<Participants> getItem() {
        return userList;
    }

    @NonNull
    @Override
    public UserAdapter.HolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_user_list, parent, false);
        return new UserAdapter.HolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.HolderUser holder, int position) {
        Participants participants = userList.get(position);

        String test = participants.getUsername();
        Log.e("part test", ""+test);
        holder.userName.setText(participants.getUsername());
        Picasso.get().load(participants.getImage()).into(holder.image);
        //holder.userEmail.setText(participants.getUserEmail());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewProfileActivity.class);
                intent.putExtra("uid",participants.getUid());
                startActivity(context, intent, null);
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class HolderUser extends RecyclerView.ViewHolder {

        ImageView image;
        public TextView userName, userEmail;

        public HolderUser(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userName);
            image = itemView.findViewById(R.id.profile_image_details);
            //userEmail = itemView.findViewById(R.id.userEmail);
        }
    }

}
