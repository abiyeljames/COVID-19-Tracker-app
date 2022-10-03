package com.example.coronaupdate1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.Event;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Cartesian;
import com.anychart.charts.Pie;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.coronaupdate1.DataModel.DbGlobalData;
import com.example.coronaupdate1.utility.StringNumber;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphGlobalCases extends AppCompatActivity {

    private static final String TAG = "GraphGlobalCases";
    private int totalCases;
    private int activeCases;
    private int totalDeaths;
    private int totalRecovered;

    private String totalCasesFormatted;
    private String formattedDate;

    private final DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference("GlobalData");
    private List<DbGlobalData> dbGlobalDataList = new ArrayList<DbGlobalData>();    // data by dates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_global_cases);

        // setting the title in the actionBar
        getSupportActionBar().setTitle("Graphs");

        // enabling the up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the data which passed into the intent from the previous activity
        getIncomingIntentExtras();

        // reading data from the firebase database
        setEventListenerOnGlobalDataBranchReference();
    }

    private void getIncomingIntentExtras(){

        // checking if the appropriate intent extras were received properly or not
        if(getIntent().hasExtra("total_cases") && getIntent().hasExtra("active_cases")
                && getIntent().hasExtra("total_deaths") && getIntent().hasExtra("total_recovered")){

            // getting the data which was passed from the previous activity
            totalCases = Integer.parseInt(getIntent().getStringExtra("total_cases"));
            activeCases = Integer.parseInt(getIntent().getStringExtra("active_cases"));
            totalDeaths = Integer.parseInt(getIntent().getStringExtra("total_deaths"));
            totalRecovered = Integer.parseInt(getIntent().getStringExtra("total_recovered"));

            // formatting (giving commas)
            StringNumber stringNumber = new StringNumber();
            totalCasesFormatted = stringNumber.bigNumberFormatting(Integer.toString(totalCases));

            // getting the current date
            // date is formatted as Ex : 28-Dec-2020
            Date date = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
            formattedDate = simpleDateFormat.format(date);

        }

    }


    private void setEventListenerOnGlobalDataBranchReference(){

        // retrieve data from firebase
        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                // sorted in day wise from the database (1-Jan-2021, 1-Feb-2021 .....)
                // iterating through all dates and adding the data to the List
                for (DataSnapshot dateDataSnapShot : snapshot.getChildren()){
                    DbGlobalData dbGlobalData = dateDataSnapShot.getValue(DbGlobalData.class);

                    dbGlobalDataList.add(dbGlobalData);
                }

                // sort by dates (correctly) by using comparator
                Collections.sort(dbGlobalDataList, new Comparator<DbGlobalData>() {
                    @Override
                    public int compare(DbGlobalData o1, DbGlobalData o2) {
                        Date date1 = null;
                        Date date2 = null;
                        // compare two instances of DbGlobalData
                        // we compare their dates
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                        try {
                            date1 = simpleDateFormat.parse(o1.getDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.d(TAG, "compare: errorMessage : " + e.getMessage());
                        }
                        try {
                            date2 = simpleDateFormat.parse(o2.getDate());
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.d(TAG, "compare: errorMessage : " + e.getMessage());
                        }

                        return date1.compareTo(date2);
                    }
                });

                // drawing the the charts
                // usually the onDataChange method gets called around last of the activity lifecycle (found by using logs)
                setPieChart();
                setNewCasesColumnChart();

                Log.d(TAG, "onDataChange: the charts hab been created");
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }

        });

    }

    private void setPieChart(){

        // pie chart using anyChart library

        AnyChartView anyChartView = findViewById(R.id.any_chart_view_global);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);   // very important line for multiple charts
        anyChartView.setProgressBar(findViewById(R.id.progress_bar_global));

        Pie pie = AnyChart.pie();

        // setting a click listener to trigger a toast when the pie chart is clicked
        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(GraphGlobalCases.this, event.getData().get("x") + ":" +
                        event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        // adding data
        List<DataEntry> globalCasesData = new ArrayList<>();
        globalCasesData.add(new ValueDataEntry("Active", activeCases));
        globalCasesData.add(new ValueDataEntry("Death", totalDeaths));
        globalCasesData.add(new ValueDataEntry("Recovered", totalRecovered));

        pie.data(globalCasesData);

        // prettifying the pie chart title
        pie.title("Distribution of Total Cases : " + totalCasesFormatted +  " Date-" + formattedDate);
        pie.title().fontColor("#000000");
        pie.title().fontOpacity(10);
        pie.title().fontStyle("bold");

        // prettifying the labels outside the pie chart the numbers in %
        pie.labels().position("outside");
        pie.labels().fontColor("#000000");
        pie.labels().fontOpacity(10);
        pie.labels().fontStyle("bold");

        // prettifying the Active, Death, Recovered legend text
        pie.legend().fontColor("#000000");
        pie.legend().fontOpacity(10);
        pie.legend().fontStyle("bold");

        // prettifying the legend title text Possible Outcomes
        pie.legend().title().enabled(true);
        pie.legend().title()
                .text("Possible Outcomes")
                .padding(0d, 0d, 10d, 0d);
        pie.legend().title().fontOpacity(10);
        pie.legend().title().fontStyle("bold");
        pie.legend().title().fontColor("#000000");

        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);

    }

    private void setNewCasesColumnChart(){

        // Column Chart using anyChart
        AnyChartView anyChartView1 = findViewById(R.id.any_chart_view_global1);
        APIlib.getInstance().setActiveAnyChartView(anyChartView1);
        anyChartView1.setProgressBar(findViewById(R.id.progress_bar_global1));

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> casesData = new ArrayList<>();

        // X and Y axis data assigned
        // (Experimental thought) sometime column chart fails to render if cases are below 20 (assumption) or near 0 or 0
        for (int i=0; i<dbGlobalDataList.size(); i++){

            int cases = Integer.parseInt(dbGlobalDataList.get(i).getNewCases());

            casesData.add(new ValueDataEntry(dbGlobalDataList.get(i).getDate(), cases));

        }

        Column column = cartesian.column(casesData);
        column.color("#01294b");    // setting column bar color

        // hover handling
        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        // prettifying the chart title
        cartesian.animation(true);
        cartesian.title("Daily New Cases - " + "Global");
        cartesian.title().fontOpacity(10);
        cartesian.title().fontColor("#000000");
        cartesian.title().fontStyle("bold");

        cartesian.yScale().minimum(0d);

        // prettifying the xAxis labels, individual values of x
        cartesian.xAxis(0).labels().fontOpacity(10);
        cartesian.xAxis(0).labels().fontColor("#000000");
        cartesian.xAxis(0).labels().fontStyle("bold");

        // prettifying the yAxis labels, individual values of y
        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");
        cartesian.yAxis(0).labels().fontOpacity(10);
        cartesian.yAxis(0).labels().fontColor("#000000");
        cartesian.yAxis(0).labels().fontStyle("bold");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        // prettifying the xAxis title
        cartesian.xAxis(0).title("Dates");
        cartesian.xAxis(0).title().fontOpacity(10);
        cartesian.xAxis(0).title().fontColor("#000000");
        cartesian.xAxis(0).title().fontStyle("bold");

        // prettifying the yAxis title
        cartesian.yAxis(0).title("Cases");
        cartesian.yAxis(0).title().fontOpacity(10); // prettifying
        cartesian.yAxis(0).title().fontColor("#000000");
        cartesian.yAxis(0).title().fontStyle("bold");

        anyChartView1.setChart(cartesian);

    }

    // handles menu items
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // making the up button do the same as back button (home) is the id of the up button
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}