package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_staycation.classes.Locations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterActivity extends AppCompatActivity {

    CardStackAdapter cardStackAdapter;
    RecyclerView recyclerViewFilter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //RUNNING MENU2.XML OVER ACTIVITY
        inflater.inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.locationFilter:
                showFilterDialog();
                break;
        }
        return true;
    }

    private void showFilterDialog() {
        //Toast.makeText(FilterActivity.this, "test", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FilterActivity.this);
        alertDialog.setTitle("Select a County");

        LayoutInflater inflater = this.getLayoutInflater();
        View filterLayout = inflater.inflate(R.layout.dialog_options, null);

        final AutoCompleteTextView filterText = (AutoCompleteTextView) filterLayout.findViewById(R.id.editCounty);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.dialog_options);
        filterText.setAdapter(adapter);
        alertDialog.setView(filterLayout);
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        alertDialog.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<String> filter_key = new ArrayList<>();
                StringBuilder filter_query = new StringBuilder("");
                filter_key.add(filterText.getText().toString());
                Collections.sort(filter_key);
                for(String key: filter_key){
                    filter_query.append(key).append("");
                }
                filter_query.setLength(filter_query.length()-1);

                fetchFilterCounty(filter_query.toString());
            }
        });
    }

    private void fetchFilterCounty(String query) {

        List<Locations> locations = new ArrayList<>();
        for(Locations locations1: locations){
            if(locations1.getCounty().contains(query)){
                locations.add(locations1);
            }
        }
        recyclerViewFilter.setAdapter(new CardStackAdapter(locations));

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);


        recyclerViewFilter = findViewById(R.id.rv_filter);
        recyclerViewFilter.setHasFixedSize(true);
        recyclerViewFilter.setLayoutManager(new GridLayoutManager(this, 2));
    }
}