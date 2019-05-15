package com.example.smartbeds;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BedAdapter extends ArrayAdapter<Bed> {

    public BedAdapter(Context context, ArrayList<Bed> beds){
        super(context, 0, beds);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Bed bed = getItem(position);

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bed, parent, false);
        }
        TextView bedName = (TextView) convertView.findViewById(R.id.beds_bed_name);
        TextView bedState = (TextView) convertView.findViewById(R.id.beds_bed_state);

        bedName.setText(bed.getBedName());
        bedState.setText(bed.getBedState());

        return convertView;
    }

    @Override
    public Bed getItem(int position){
        return super.getItem(position);
    }
}
