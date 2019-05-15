package com.example.smartbeds;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class BedChartsActivity extends AppCompatActivity {

    private Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_charts);

        Session session = Session.getInstance();
        if(session.getToken()==null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        Bundle b = getIntent().getExtras();
        String bedName = b.getString("bedName");

        TextView title = (TextView) findViewById(R.id.bed_charts_title);
        title.setText(bedName);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.bed_charts_tabs);
        //ViewPager viewPager = (ViewPager) findViewById(R.id.bed_charts_pager);
        //tabLayout.setupWithViewPager(viewPager);


    }
}
