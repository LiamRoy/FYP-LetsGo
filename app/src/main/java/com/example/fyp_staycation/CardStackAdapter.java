package com.example.fyp_staycation;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_staycation.classes.Locations;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static androidx.core.content.ContextCompat.startActivity;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private android.content.Context context;
    private List<Locations> items;
    private List<Locations> items2;
    public ItemClickListener listener;

    public CardStackAdapter(Context context, List<Locations> items) {
        super();
        this.context=context;
        this.items = items;
        items2=new ArrayList<>(items);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Locations locations = items.get(position);
        holder.title.setText(locations.getTitle());
        holder.category.setText(locations.getCategory());
        holder.city.setText(locations.getCity());
        holder.county.setText(locations.getCounty());
        Picasso.get().load(locations.getImage()).into(holder.image);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,LocationDetailsActivity.class);
                intent.putExtra("lid",locations.getLid());
                startActivity(context, intent, null);
            }
        });

    }

    public void setItem(List<Locations> items) {
        this.items = items;
    }

    public void setItemClickListener(ItemClickListener listener)
    {
        this.listener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView category, title, city, county;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
            category = itemView.findViewById(R.id.item_category);
            city = itemView.findViewById(R.id.item_city);
            county = itemView.findViewById(R.id.item_county);


        }



        void setData(Locations data) {
            Picasso.get()
                    .load(data.getImage())
                    .fit()
                    .centerCrop()
                    .into(image);
            title.setText(data.getTitle());
            category.setText(data.getCategory());
            city.setText(data.getCity());
            county.setText(data.getCounty());
        }
    }

    public List<Locations> getItem() {
        return items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}