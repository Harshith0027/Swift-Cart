package com.example.swiftcart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class checkout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        ImageView qr = findViewById(R.id.qr);
        Button exit = findViewById(R.id.exit);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(getSupportActionBar() != null)getSupportActionBar().hide();


        
        String myText = dashboard.getCartId()+"&"+cart.getTotal();
        String cartId = dashboard.getCartId();

        DatabaseReference dbref= FirebaseDatabase.getInstance().getReference().child("customers");
        
        Query query=dbref.child(cartId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                
                dataSnapshot.getRef().removeValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        dataSnapshot.getRef().removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                db.collection(cartId).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    WriteBatch batch = db.batch();
                                    QuerySnapshot querySnapshot = task.getResult();
                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                        batch.delete(document.getReference());
                                    }
                                    batch.commit()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                    } else {

                                                    }
                                                }
                                            });
                                }
                            }
                        });

                startActivity(new Intent(getApplicationContext(), dashboard.class));
            }
        });





        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            
            BitMatrix mMatrix = mWriter.encode(myText, BarcodeFormat.QR_CODE, 400,400);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
            qr.setImageBitmap(mBitmap);//Setting generated QR code to imageView
            

        } catch (WriterException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Intent i = new Intent(this, dashboard.class);
        startActivity(i);
    }
}