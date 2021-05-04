package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp_staycation.adapters.ChatAdapter;
import com.example.fyp_staycation.adapters.GroupChatAdapter;
import com.example.fyp_staycation.classes.Connections;
import com.example.fyp_staycation.classes.Locations;
import com.example.fyp_staycation.classes.Messages;
import com.example.fyp_staycation.classes.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Locations locations;
    private FirebaseUser user;
    private ImageView imageView, homeImg;
    private static final String TAG = "Home Activity";
    private CardStackLayoutManager manager;
    private DatabaseReference databaseReference;
    private FirebaseUser dbref;
    private DatabaseReference condb;
    FirebaseRecyclerAdapter<Locations, CardStackAdapter.ViewHolder> adapter;
    private CardStackAdapter adapter1, adapter2;
    RecyclerView recyclerView;
    private List<Locations> items;
    private String location = "";
    private TextView title,city,county;
    private CardStackView cardStackView;
    public void CardStackAdapter(List<Locations> items) {
        this.items = items;
    }
    private SearchView searchView;
    String user1;
    private String Title, locationRandomKey,lid;
    private ArrayList<Locations> filterLocation;
    private View itemView;
    private FloatingActionButton searchBtn, catBtn;
    private Toolbar toolbar;
    private TextView homeTitle;

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
                Intent intentMap = new Intent(HomeActivity.this, NearMeActivity.class);
                startActivity(intentMap);
                break;
            case R.id.home:
                Intent intentHome = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intentHome);
                break;
            case R.id.View:
                Intent intentProfile = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                break;
            case R.id.Trips:
                Intent intentTrip = new Intent(HomeActivity.this, TripsActivity.class);
                startActivity(intentTrip);
                break;
            case R.id.Connections:
                Intent intentGroup = new Intent(HomeActivity.this, GroupChatActivity.class);
                intentGroup.putExtra("lid",locations.getLid());
                startActivity(intentGroup);
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
        }

        return true;
    }

    private void showFilterDialog() {
        //Toast.makeText(FilterActivity.this, "test", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Select a County");
        alertDialog.setCancelable(true);

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
                    filter_query.append(key);
                }


                fetchFilterCounty(filter_query.toString());
            }
        });
        alertDialog.show();
    }

    private void fetchFilterCounty(String query) {
    //#####NEEDS TO BE FIXED######
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Locations locations = ds.getValue(Locations.class);

                    if(locations.getCounty().equalsIgnoreCase(query)){
                        items.add(locations);
                        Log.e(locations.getCounty(), locations.getTitle());
                    }
                }
                Log.e("test", "test");
                Log.e("test", String.valueOf(items.size()));
                adapter1.notifyDataSetChanged();
                cardStackView.setAdapter(adapter1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("trip is null", "");
        } else {
            lid = extras.getString("lid");
        }
        locations = new Locations();
        toolbar = findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);
        filterLocation = new ArrayList<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        title = findViewById(R.id.item_title);
        city = findViewById(R.id.item_city);
        county = findViewById(R.id.item_county);
        homeTitle = findViewById(R.id.homeTitle);
        imageView = findViewById(R.id.profile_image_details);
        homeImg = (ImageView) findViewById(R.id.homeImg);
        homeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentHome = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intentHome);
            }
        });
        getUserDetails();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProfile = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Locations");
        dbref = FirebaseAuth.getInstance().getCurrentUser();
        condb = FirebaseDatabase.getInstance().getReference().child("Connections");
        cardStackView = findViewById(R.id.card_stack_view);

        catBtn = (FloatingActionButton)findViewById(R.id.catBtn);
        catBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCatFilterDialog();
            }
        });

        searchBtn = (FloatingActionButton) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        manager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                if (direction.equals(Direction.Right)){

                }

                // Paginating
                if (manager.getTopPosition() == adapter1.getItemCount() - 5) {
                    paginate();
                }

            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_title);
                Log.d(TAG, "onCardAppeared: " + position + ", title: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_title);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + tv.getText());
            }
        });

        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter1 = new CardStackAdapter(HomeActivity.this,addList());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter1);
        cardStackView.setItemAnimator(new DefaultItemAnimator());

    }

    private void showCatFilterDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Select a Category");
        alertDialog.setCancelable(true);

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
                    filter_query.append(key);
                }


                fetchFilterCategory(filter_query.toString());
            }
        });
        alertDialog.show();
    }

    private void fetchFilterCategory(String query) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Locations locations = ds.getValue(Locations.class);

                    if(locations.getCategory().equalsIgnoreCase(query)){
                        items.add(locations);
                        Log.e(locations.getCategory(), locations.getTitle());
                    }
                }
                Log.e("test", "test");
                Log.e("test", String.valueOf(items.size()));
                adapter1.notifyDataSetChanged();
                cardStackView.setAdapter(adapter1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getUserDetails() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User");
        databaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user!=null){
                    String name = user.getUsername();
                    if(user.getImage()!=null) {
                        String image = user.getImage();
                        Log.e("testing123", image);
                        homeTitle.setText("Hi " + name);
                        Picasso.get().load(user.getImage()).into(imageView);
                    }else{
                        Picasso.get().load(R.drawable.profile_icon_foreground).into(imageView);
                    }
                }
                Log.e("test", "test");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addingToTrip() {
        user1 = user.getUid();

        final HashMap<String, Object> tripMap = new HashMap<>();

        tripMap.put("groupId", location);

        condb.child(location).updateChildren(tripMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        HashMap<String, Object> members = new HashMap<>();
                        members.put("User", user.getUid());
                        Connections connections = new Connections();
                        connections.setGroupId(location);



                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Connections").child(location);
                        db.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(members)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(HomeActivity.this, "Groups Created", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                    }
                });
    }


    private void paginate() {
        List<Locations> old = adapter1.getItem();
        List<Locations> new1 = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(old, new1);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);
        adapter1.setItem(new1);
        diffResult.dispatchUpdatesTo(adapter1);
    }

    private List<Locations> addList() {

        items = new ArrayList<>();

        items.add(new Locations());

        return items;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Locations> options =
                new FirebaseRecyclerOptions.Builder<Locations>()
                        .setQuery(databaseReference, Locations.class)
                        .build();

        FirebaseRecyclerAdapter<Locations, CardStackAdapter.ViewHolder> adapter =
                new FirebaseRecyclerAdapter<Locations, CardStackAdapter.ViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CardStackAdapter.ViewHolder holder, int position, @NonNull Locations model) {
                        holder.title.setText(model.getTitle());
                        holder.category.setText(model.getCategory());
                        holder.city.setText(model.getCity());
                        holder.county.setText(model.getCounty());
                        Picasso.get().load(model.getImage()).into(holder.image);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HomeActivity.this,LocationDetailsActivity.class);
                                intent.putExtra("lid",model.getLid());

                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public CardStackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
                        return new CardStackAdapter.ViewHolder(view);
                    }
                };

        adapter1 = new CardStackAdapter(HomeActivity.this,addList());
        cardStackView.setAdapter(adapter);
        adapter.startListening();
    }
}