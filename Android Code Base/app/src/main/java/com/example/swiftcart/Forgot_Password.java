package com.example.swiftcart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forgot_Password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Button forgot = findViewById(R.id.forgot);
        TextView mail = findViewById(R.id.email);


        if(getSupportActionBar() != null)
            getSupportActionBar().hide();
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(mail.getText());
                AlertDialog.Builder builder = new AlertDialog.Builder(Forgot_Password.this);
                builder.setCancelable(false);
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    builder.setMessage("Reset Mail is Sent !!!");
                                    builder.setTitle("Chilllll !");
                                    builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                    });
                                    builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                                        dialog.cancel();
                                    });

                                } else {
                                    builder.setMessage("Please Enter Vaild Registed Email !!!");
                                    builder.setTitle("ohh ohhh !");
                                    builder.setNegativeButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
                                        dialog.cancel();
                                    });

                                }
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
            }
        });
    }
}