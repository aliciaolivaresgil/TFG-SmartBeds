package com.example.smartbeds.bedMonitoring;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartbeds.R;

import java.util.ArrayList;

/**
 * Adaptador para mostrar la lista de camas.
 */
public class BedAdapter extends ArrayAdapter<Bed> {

    Context context;

    /**
     * Constructor del adaptador que almacena la lista de camas.
     * @param context Contexto del activity actual.
     * @param beds Lista de camas.
     */
    public BedAdapter(Context context, ArrayList<Bed> beds){
        super(context, 0, beds);
        this.context = context;
    }

    /**
     * Devuelve la vista adaptada a la lista de camas.
     * @param position Posición del item.
     * @param convertView Vista actual.
     * @param parent Padre de la vista.
     * @return Vista modificada.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Bed bed = getItem(position);

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bed, parent, false);
        }

        //Se asocia un nombre, un estado y un color a cada cama de la lista.
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

    /**
     * Devuelve un item de la lista de camas.
     * @param position Posición del item.
     * @return Cama
     */
    @Override
    public Bed getItem(int position){
        return super.getItem(position);
    }
}
