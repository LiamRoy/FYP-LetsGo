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
import com.example.fyp_staycation.classes.Trip;
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

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    public Context context;
    private List<Trip> tripList;
    private List<Trip> items2;
    LayoutInflater layoutInflater;
    public ItemClickListener listener;

    public TripAdapter(List<Trip> tripList) {
        super();

        this.tripList = tripList;
        items2=new ArrayList<>(tripList);
        //layoutInflater = LayoutInflater.from(context);
    }

    public void setItem(List<Trip> tripList) {
        this.tripList = tripList;
    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }

    public List<Trip> getItem() {
        return tripList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_trip_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = tripList.get(position);

        holder.tripTitle.setText(trip.getTripTitle());
        holder.date.setText(trip.getDate());
        holder.createdBy.setText(trip.getCreatedBy());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tripTitle, date, createdBy;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tripTitle = itemView.findViewById(R.id.tripTitle);
            createdBy = itemView.findViewById(R.id.created);
            date = itemView.findViewById(R.id.tripDate);
        }
    }

}


