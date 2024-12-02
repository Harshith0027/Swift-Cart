package com.example.swiftcart;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CoursesGVAdapter extends ArrayAdapter<DataModal> {


	public CoursesGVAdapter(@NonNull Context context, ArrayList<DataModal> dataModalArrayList) {
		super(context, 0, dataModalArrayList);
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


		View listitemView = convertView;
		if (listitemView == null) {
			listitemView = LayoutInflater.from(getContext()).inflate(R.layout.image_gv_item, parent, false);
		}
		

		DataModal dataModal = getItem(position);
		

		TextView nameTV = listitemView.findViewById(R.id.itemname);
		ImageView courseIV = listitemView.findViewById(R.id.itemimg);
		

		nameTV.setText(dataModal.getName());
		

		Picasso.get().load(dataModal.getImgUrl()).into(courseIV);


		listitemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Toast.makeText(getContext(), "Item clicked is : " + dataModal.getName(), Toast.LENGTH_SHORT).show();

			}
		});
		return listitemView;
	}
}
