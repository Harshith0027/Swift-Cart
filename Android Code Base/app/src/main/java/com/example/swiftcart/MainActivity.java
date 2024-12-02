package com.example.swiftcart;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    String email1,password1;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(getApplicationContext(),dashboard.class);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(getSupportActionBar()!=null)
            getSupportActionBar().hide();

        
        mAuth = FirebaseAuth.getInstance();
        Button login = findViewById(R.id.login);
        TextView email = (TextView) findViewById(R.id.email);
        TextView password = (TextView) findViewById(R.id.password);
        TextView logintosignup = findViewById(R.id.logintosignup);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        TextView forgot_password = findViewById(R.id.forgotpassword);
        String text = (String) forgot_password.getText();
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new UnderlineSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgot_password.setText(spannableString);
        forgot_password.setVisibility(View.VISIBLE);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                email1 = String.valueOf(email.getText());
                password1 = String.valueOf(password.getText());
                mAuth.signInWithEmailAndPassword(email1, password1)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    progressBar.setVisibility(View.GONE);
                                    Intent i = new Intent(getApplicationContext(),dashboard.class);
                                    startActivity(i);
                                    Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setCancelable(false);
                                    builder.setMessage("Please Enter Vaild Registed Email and Password !!!");
                                    builder.setTitle("ohh ohhh !");
                                    builder.setNegativeButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                                        dialog.cancel();
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }
        });

        logintosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),signup.class));
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Forgot_Password.class));
            }
        });

    }
}