package com.example.swiftcart;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class cartitems extends ArrayAdapter<DataModal>
{

    FirebaseFirestore db;
    public static HashMap<String,String> prices = new HashMap<>();
    HashMap<String,Integer> qnty;



    public cartitems(@NonNull Context context, ArrayList<DataModal> dataModalArrayList,HashMap<String,Integer> qnty) {
        super(context, 0, dataModalArrayList);
        this.qnty = qnty;

    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.dummy, parent, false);
        }


        DataModal dataModal = getItem(position);


        TextView itemname = listitemView.findViewById(R.id.itemname);
        ImageView img = listitemView.findViewById(R.id.img);
        TextView org_price = listitemView.findViewById(R.id.org_price);
        TextView qnt = listitemView.findViewById(R.id.qnt);
        TextView dis_price = listitemView.findViewById(R.id.dist_price);


        assert dataModal != null;
        itemname.setText(dataModal.getName());

        db = FirebaseFirestore.getInstance();
        db.collection("prices")
                .whereEqualTo("name", dataModal.getName())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String,Object> map= document.getData();
                                org_price.setText((String)map.get("price"));
                                dis_price.setText((String)map.get("discount_price"));
                                prices.put(dataModal.getName(),(String)dis_price.getText());
                                org_price.setPaintFlags(org_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                        }
                    }
                });
        if(qnty.containsKey(dataModal.getName()) && qnty.get(dataModal.getName()) != null)
            qnt.setText("Qnty:   " + qnty.get(dataModal.getName()));

        Picasso.get().load(dataModal.getImgUrl()).into(img);

        


        return listitemView;
    }
}
