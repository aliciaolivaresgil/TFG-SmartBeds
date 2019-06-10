package com.example.smartbeds;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class BedAdapter extends ArrayAdapter<Bed> {

    Context context;

    public BedAdapter(Context context, ArrayList<Bed> beds){
        super(context, 0, beds);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Bed bed = getItem(position);

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bed, parent, false);
        }
        TextView bedName = (TextView) convertView.findViewById(R.id.beds_bed_name);
        TextView bedState = (TextView) convertView.findViewById(R.id.beds_bed_state);
        LinearLayout background = (LinearLayout) convertView.findViewById(R.id.beds_color);

        bedName.setText(bed.getBedName());
        bedState.setText(bed.getBedState());

        if(bed.getColor()) {
            background.setBackground(ContextCompat.getDrawable(context, R.color.colorAccentLight));
        }else{
            background.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryLight));
        }
        return convertView;
    }

    @Override
    public Bed getItem(int position){
        return super.getItem(position);
    }
}
