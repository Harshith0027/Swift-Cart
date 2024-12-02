package com.example.swiftcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class index extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        int idx=0;
        while(idx<5000)
            idx++;

        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }
}