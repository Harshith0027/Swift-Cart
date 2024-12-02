package com.example.swiftcart;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.*;



public class cart extends AppCompatActivity  {


    ListView cartlistitems;
    FirebaseFirestore db;
    ArrayList<DataModal> dataModalArrayList;
    int size = 0;
    int temp = 0;
    cartitems adapter;
    int totalPrice;
    static Button total;
    String cartid;
    HashMap<String,String> prices;
    HashMap<String,Integer> qnty;
    int items_in_firebase;
    Button qr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        cartid = dashboard.getCartId();

        if(getSupportActionBar() != null)
            getSupportActionBar().setTitle(cartid);


        total = findViewById(R.id.total);
        cartlistitems = findViewById(R.id.cartlistitems);
        db = FirebaseFirestore.getInstance();
        qr = findViewById(R.id.qr);
        items_in_firebase = 0;
        qnty = new HashMap<>();


        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(cart.this);
                builder.setCancelable(false);
                builder.setMessage("are you sure to checkout !!!");
                builder.setTitle("Ohh Ohhh !");
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                    finish();
                });
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(),checkout.class));
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        context();




    }
    public void context(){
        loadcartitems();
        refresh(1000);
    }

    private void refresh(int ms){
        final Handler handler = new Handler();

        final Runnable runnable = () -> context();

        handler.postDelayed(runnable,ms);
    }

    private void loadcartitems()
    {
        dataModalArrayList = new ArrayList<>();
        items_in_firebase = 0;
        qnty.clear();


        db.collection(dashboard.getCartId()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                DataModal dataModal = d.toObject(DataModal.class);
                                items_in_firebase ++;
                                if(!qnty.containsKey(dataModal.getName()))
                                    dataModalArrayList.add(dataModal);
                                qnty.put(dataModal.getName(),qnty.getOrDefault(dataModal.getName(),0)+1);
                            }

                            adapter = new cartitems(cart.this, dataModalArrayList,qnty);
                            size = items_in_firebase;
                            if (size == 0) {
                                cartlistitems.setAdapter(adapter);
                                String temp1 = Integer.toString(calculateTotal(dataModalArrayList));
                                total.setText(temp1);
                            }
                            if (temp != size) {
                                cartlistitems.setAdapter(null);
                                cartlistitems.setAdapter(adapter);
                                String temp1 = Integer.toString(calculateTotal(dataModalArrayList));
                                total.setText(temp1);
                                temp = size;
                            }

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(cart.this, "Fail to load data..", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private int calculateTotal(ArrayList<DataModal> dataModalArrayList){
        totalPrice = 0;
        prices = cartitems.prices;
        for(DataModal dataModal:dataModalArrayList)
        {
            if(prices.containsKey(dataModal.getName()) && prices.get(dataModal.getName()) != null)
                totalPrice += Integer.parseInt(prices.get(dataModal.getName())) * qnty.get(dataModal.getName());
        }
        return totalPrice;
    }


    public static String getTotal() {
        return total.getText().toString();
    }

    public HashMap<String, Integer> getQnty() {
        return qnty;
    }

    public void setQnty(HashMap<String, Integer> qnty) {
        this.qnty = qnty;
    }
}

