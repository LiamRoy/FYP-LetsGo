package com.example.fyp_staycation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AdminLogin extends AppCompatActivity implements View.OnClickListener {

    private EditText editEmail;
    private EditText editPassword;
    private Button signIn;
    private TextView attempt;
    private int counter = 5;

    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        auth = FirebaseAuth.getInstance();

        editEmail = (EditText) findViewById(R.id.etName);
        editPassword = (EditText) findViewById(R.id.etPassword);

        signIn = (Button) findViewById(R.id.btnLogin);
        signIn.setOnClickListener(this);

        attempt = (TextView)findViewById(R.id.attempts);
        attempt.setText("No of attempts remaining: 5");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                userAdminLogin();
                break;
        }
    }



    private void userAdminLogin() {

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if(email.isEmpty()){
            editEmail.setError("Email is required");
            editEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Please enter Valid Email");
            editEmail.requestFocus();
            return;
        }

        if(password.isEmpty()&& password.length()<6){
            editPassword.setError("Password is required and must be longer than 6 characters");
            editPassword.requestFocus();

            return;
        }

        //progressBar.setVisibility(View.VISIBLE);
        //admin is: admin@gmail.com password: password
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(AdminLogin.this, AdminActivity.class));
                }else{
                    Toast.makeText(AdminLogin.this, "Failed to login, please try again", Toast.LENGTH_LONG).show();
                   // progressBar.setVisibility(View.GONE);
                    counter--;
                    attempt.setText("No of attempts remaining: " + counter);
                    if (counter == 0) {
                        signIn.setEnabled(false);
                    }
                }
            }
        });

    }
}