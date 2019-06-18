package com.example.smartbeds;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class BedChartsActivity extends AppCompatActivity {

    private Context context = this;

    private Session session;

    private TextView stateView;
    TabLayout.Tab tab1;
    TabLayout.Tab tab2;
    TabLayout.Tab tab3;

    private int state = -1;
    private double prob = 0.0;

    private int p1;
    private int p2;
    private int p3;
    private int p4;
    private int p5;
    private int p6;
    private int hr;
    private int rr;
    private int sv;
    private int hrv;
    private double b2b;

    private String stringDate;
    private Map<Float, String> mapFormatter = new HashMap<>();

    private BedStreaming bedStreaming;

    private LineDataSet dataSetProb;
    private LineData lineDataProb;
    private List<Entry> listProb = Collections.synchronizedList(new ArrayList<Entry>());
    private LineChart chartProb;

    private LineDataSet dataSetPressures;
    private LineData lineDataPressures;
    private List<Entry> listP1 = new ArrayList<Entry>();
    private List<Entry> listP2 = new ArrayList<Entry>();
    private List<Entry> listP3 = new ArrayList<Entry>();
    private List<Entry> listP4 = new ArrayList<Entry>();
    private List<Entry> listP5 = new ArrayList<Entry>();
    private List<Entry> listP6 = new ArrayList<Entry>();
    private LineChart chartPressures;

    private LineDataSet dataSetVital;
    private LineData lineDataVital;
    private List<Entry> listHR = new ArrayList<Entry>();
    private List<Entry> listRR = new ArrayList<Entry>();
    private List<Entry> listSV = new ArrayList<Entry>();
    private List<Entry> listHRV = new ArrayList<Entry>();
    private List<Entry> listB2B = new ArrayList<Entry>();
    private LineChart chartHR;
    private LineChart chartRR;
    private LineChart chartSV;
    private LineChart chartHRV;
    private LineChart chartB2B;

    private int counter = 0;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message inputMessage) {
            JSONObject resultado = (JSONObject) inputMessage.obj;
            refresh(resultado);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bedStreaming.stop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_charts);

        session = Session.getInstance();
        if (session.getToken() == null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        Bundle b = getIntent().getExtras();
        String bedName = b.getString("bedName");

        TextView title = (TextView) findViewById(R.id.bed_charts_title);
        title.setText(bedName);

        stateView = (TextView) findViewById(R.id.bed_charts_state);

        TabLayout tabs = (TabLayout) findViewById(R.id.bed_charts_tabs);
        tab1 = tabs.newTab().setText("PROBABILIDAD");
        tab2 = tabs.newTab().setText("PRESIONES");
        tab3 = tabs.newTab().setText("CONSTANTES VITALES");
        tabs.addTab(tab1, true);
        tabs.addTab(tab2, false);
        tabs.addTab(tab3, false);

        tabs.setOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab1.isSelected()) {
                    chartProb.setVisibility(View.VISIBLE);
                    chartPressures.setVisibility(View.GONE);
                    chartHR.setVisibility(View.GONE);
                    chartRR.setVisibility(View.GONE);
                    chartSV.setVisibility(View.GONE);
                    chartHRV.setVisibility(View.GONE);
                    chartB2B.setVisibility(View.GONE);
                } else if (tab2.isSelected()) {
                    chartProb.setVisibility(View.GONE);
                    chartPressures.setVisibility(View.VISIBLE);
                    chartHR.setVisibility(View.GONE);
                    chartRR.setVisibility(View.GONE);
                    chartSV.setVisibility(View.GONE);
                    chartHRV.setVisibility(View.GONE);
                    chartB2B.setVisibility(View.GONE);
                } else {
                    chartProb.setVisibility(View.GONE);
                    chartPressures.setVisibility(View.GONE);
                    chartHR.setVisibility(View.VISIBLE);
                    chartRR.setVisibility(View.VISIBLE);
                    chartSV.setVisibility(View.VISIBLE);
                    chartHRV.setVisibility(View.VISIBLE);
                    chartB2B.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        String namespace = null;
        try {
            String urlParameters = "token=" + session.getToken() + "&bedname=" + bedName;
            JSONObject resultado = APIUtil.petitionAPI("/api/bed", urlParameters, context);
            namespace = (String) resultado.get("namespace");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        bedStreaming = new BedStreaming(0, bedName, namespace, handler);

        //crear gráfica de probabilidad
        chartProb = (LineChart) findViewById(R.id.bed_charts_prob);
        generateChart(chartProb, 5, 0, 1);

        //crear gráfica p1
        chartPressures = (LineChart) findViewById(R.id.bed_charts_p1);
        generateChart(chartPressures, 5, 0, 100);

        //crear gráfica HR
        chartHR = (LineChart) findViewById(R.id.bed_charts_hr);
        generateChart(chartHR, -1, 0, -1);

        //crear gráfica RR
        chartRR = (LineChart) findViewById(R.id.bed_charts_rr);
        generateChart(chartRR, -1, 0, -1);

        //crear gráfica SV
        chartSV = (LineChart) findViewById(R.id.bed_charts_sv);
        generateChart(chartSV, -1, 0, -1);

        //crear gráfica HRV
        chartHRV = (LineChart) findViewById(R.id.bed_charts_hrv);
        generateChart(chartHRV, -1, 0, -1);

        //crear gráfica B2B
        chartB2B = (LineChart) findViewById(R.id.bed_charts_b2b);
        generateChart(chartB2B, -1, 0, -1);

    }

    public void refresh(JSONObject resultado) {
        state = 0;
        JSONArray array=null;
        JSONArray pressures=null;
        JSONArray vitals=null;

        try {
            array = (JSONArray) resultado.get("result");
            state = (int) array.get(0);

            stringDate = (String) resultado.get("instance");
            mapFormatter.put((float) counter, stringDate.substring(11,19));

            pressures = (JSONArray) resultado.get("pressure");
            p1 = (int) pressures.get(0);
            p2 = (int) pressures.get(1);
            p3 = (int) pressures.get(2);
            p4 = (int) pressures.get(3);
            p5 = (int) pressures.get(4);
            p6 = (int) pressures.get(5);

            vitals = (JSONArray) resultado.get("vital");
            hr = (int) vitals.get(0);
            rr = (int) vitals.get(1);
            sv = (int) vitals.get(2);
            hrv = (int) vitals.get(3);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try{
            prob = (double) array.get(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (ClassCastException e){
            prob = 0.0;
        }

        try{
            b2b = (double) array.get(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (ClassCastException e){
            b2b = 0.0;
        }

        if (counter > 180) {
            listProb.remove(0);
            listP1.remove(0);
            listP2.remove(0);
            listP3.remove(0);
            listP4.remove(0);
            listP5.remove(0);
            listP6.remove(0);
            listHR.remove(0);
            listRR.remove(0);
            listSV.remove(0);
            listHRV.remove(0);
            listB2B.remove(0);
        }

        listProb.add(new Entry(counter, (float) prob));

        listP1.add(new Entry(counter, p1));
        listP2.add(new Entry(counter, p2));
        listP3.add(new Entry(counter, p3));
        listP4.add(new Entry(counter, p4));
        listP5.add(new Entry(counter, p5));
        listP6.add(new Entry(counter, p6));
        listHR.add(new Entry(counter, hr));
        listRR.add(new Entry(counter, rr));
        listSV.add(new Entry(counter, sv));
        listHRV.add(new Entry(counter, hrv));
        listB2B.add(new Entry(counter, (float) b2b));
        counter++;

        try {
            //modificar etiqueta de estado
            switch (state) {
                case 0:
                    stateView.setText("Estado: dormido");
                    stateView.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryLight));
                    break;
                case 1:
                    stateView.setText("Estado: crisis epiléptica");
                    stateView.setBackground(ContextCompat.getDrawable(context, R.color.colorAccentLight));
                    break;
                case 2:
                    stateView.setText("Estado: cama vacía");
                    stateView.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryLight));
                    break;
                case 3:
                    stateView.setText("Estado: datos insuficientes");
                    stateView.setBackground(ContextCompat.getDrawable(context, R.color.colorPrimaryLight));
            }

            //actualizar gráficas
            updateChart(chartProb, listProb, dataSetProb, lineDataProb, ContextCompat.getColor(context, R.color.chart_1), "Probabilidad de crisis");
            updateChart(chartHR, listHR, dataSetVital, lineDataVital, ContextCompat.getColor(context, R.color.chart_1), "Frec. cardíaca (puls/min)");
            updateChart(chartRR, listRR, dataSetVital, lineDataVital, ContextCompat.getColor(context, R.color.chart_2), "Frec. respiratoria (resp/min)");
            updateChart(chartSV, listSV, dataSetVital, lineDataVital, ContextCompat.getColor(context, R.color.chart_3), "Volumen sitólico (ml)");
            updateChart(chartHRV, listHRV, dataSetVital, lineDataVital, ContextCompat.getColor(context, R.color.chart_4), "Variabilidad de HR (ms)");
            updateChart(chartB2B, listB2B, dataSetVital, lineDataVital, ContextCompat.getColor(context, R.color.chart_5), "Tiempo entre pulaciones (ms)");

            //actualizar grafica Pressures
            updatePressuresChart();

        } catch (Exception e) {
            e.printStackTrace();
            bedStreaming.stop();
        }
    }


    private void updateChart(LineChart chart, List<Entry> list, LineDataSet dataSet, LineData lineData, int color, String label) {
        dataSet = new LineDataSet(list, label);

        dataSet.setColor(color);
        dataSet.setLineWidth(3.0f);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        lineData = new LineData(dataSet);

        chart.setData(lineData);

        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void updatePressuresChart(){
        lineDataPressures = new LineData();

        updatePressureLine(listP1, ContextCompat.getColor(context, R.color.chart_1), "P1");
        updatePressureLine(listP2, ContextCompat.getColor(context, R.color.chart_2), "P2");
        updatePressureLine(listP3, ContextCompat.getColor(context, R.color.chart_3), "P3");
        updatePressureLine(listP4, ContextCompat.getColor(context, R.color.chart_4), "P4");
        updatePressureLine(listP5, ContextCompat.getColor(context, R.color.chart_5), "P5");
        updatePressureLine(listP6, ContextCompat.getColor(context, R.color.chart_6), "P6");

        chartPressures.notifyDataSetChanged();
        chartPressures.invalidate();

    }

    private void updatePressureLine(List<Entry> list, int color, String label){

        dataSetPressures = new LineDataSet(list, label);
        dataSetPressures.setColor(color);
        dataSetPressures.setLineWidth(3.0f);
        dataSetPressures.setDrawValues(false);
        dataSetPressures.setDrawCircles(false);
        lineDataPressures.addDataSet(dataSetPressures);

        chartPressures.setData(lineDataPressures);

    }

    private void generateChart(LineChart chart, int count, int min, int max) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextSize(20);
        chart.getLegend().setFormSize(20);
        YAxis right = chart.getAxisRight();
        YAxis left = chart.getAxisLeft();
        right.setEnabled(false);
        if (count != -1) {
            left.setLabelCount(count);
            left.setAxisMaximum(max);
        }
        left.setAxisMinimum(min);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if(mapFormatter.containsKey(value)) {
                    return mapFormatter.get(value);
                }else{
                    return "";
                }
            }
        });
        chart.setNoDataText("Esperando datos");
        chart.setNoDataTextColor(ContextCompat.getColor(context, R.color.colorAccent));
    }

    public void back(View view){
        finish();
    }

}