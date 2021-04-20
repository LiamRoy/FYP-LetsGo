package com.example.fyp_staycation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp_staycation.adapters.ChatAdapter;
import com.example.fyp_staycation.classes.Locations;
import com.example.fyp_staycation.classes.Messages;
import com.example.fyp_staycation.classes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private String groupID;
    private TextView groupTitle;
    private EditText etMessage;
    private ImageButton sendBtn;
    private Toolbar toolbar;
    private RecyclerView chatRv;
    private ArrayList<Messages> messagesArrayList;
    private ChatAdapter chatAdapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        //RUNNING MENU2.XML OVER ACTIVITY
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.createTrip:
                //startActivity(new Intent(ChatActivity.this,CreateTripActivity.class));
                break;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this,MainActivity.class));
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.chatToolbar);
        groupTitle = findViewById(R.id.groupTitle);
        etMessage = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);
        chatRv = findViewById(R.id.chatRv);

        firebaseAuth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupId");

        loadGroupInfo();
        loadMessages();

        groupTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ChatActivity.this,CreateTripActivity.class);
                intent.putExtra("groupId",groupID);

                startActivity(intent);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "Can't send Empty Text", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessage(message);
                }
            }

        });
    }

    private void loadMessages() {

        messagesArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Connections");
        reference.child(groupID).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messagesArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            Messages messages = ds.getValue(Messages.class);
                            messagesArrayList.add(messages);
                        }

                        chatAdapter = new ChatAdapter(ChatActivity.this, messagesArrayList);
                        chatRv.setAdapter(chatAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadGroupInfo() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Connections");
        databaseReference.orderByChild("groupId").equalTo(groupID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds: snapshot.getChildren()){
                            String Title = "" + ds.child("groupId").getValue();
                            groupTitle.setText(Title);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void sendMessage(String message) {

        String timestamp = ""+System.currentTimeMillis();
        User user = new User();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("sender", firebaseAuth.getCurrentUser().getEmail());
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("type","text");
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Connections");
                dbRef.child(groupID).child("Messages").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        etMessage.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }
}