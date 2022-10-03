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
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Align;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.LegendLayout;
import com.anychart.enums.MarkerType;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.example.coronaupdate1.DataModel.DbCountryData;
import com.example.coronaupdate1.DataModel.DbCountryDataInfection;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class GraphModellingActivity extends AppCompatActivity {

    private static final String TAG = "GraphModellingActivity";
    private String countryName;

    private DatabaseReference mRootRef;
    private DatabaseReference mRootRef1;

    private List<DbCountryData> selectedCountryData = new ArrayList<>();              // selected country data by dates
    private List<DbCountryDataInfection> infectionRateData = new ArrayList<>();       // infection data of the selected country by dates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_country_modelling);
        Log.d(TAG, "onCreate: ");

        // setting the title in the actionBar
        getSupportActionBar().setTitle("Graphs");

        // enabling the up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // checking if the appropriate intent extras were received properly or not
        if(getIntent().hasExtra("country_name")){

            // getting the data which was passed from the previous activity
            countryName = getIntent().getStringExtra("country_name");
            Log.d(TAG, "onCreate: got intent");

            // referencing country data branch in the json tree in firebase
            setReferenceToCountryDataBranch();

            // referencing country data infection branch in the json tree in firebase
            setReferenceToCountryDataInfectionBranch();

            // reading data from the country data branch from firebase realtime database
            setEventListenerOnCountryDataBranchReference();

            // reading data from the country data Infection branch from firebase realtime database
            setEventListenerOnCountryDataInfectionBranchReference();

        }
    }


    private void setEventListenerOnCountryDataBranchReference(){

        // retrieve data from the database using the reference
        mRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                // declaring instance objects
                DbCountryData dbCountryData;

                // iterating through all the dates and adding the data at each date to the list
                for (DataSnapshot dateDataSnapshot : snapshot.getChildren()){

                    // fetching data
                    dbCountryData = dateDataSnapshot.getValue(DbCountryData.class);

                    // adding to the list
                    selectedCountryData.add(dbCountryData);

                }

                // sort by date correctly by using comparator
                Collections.sort(selectedCountryData, new Comparator<DbCountryData>() {
                    @Override
                    public int compare(DbCountryData o1, DbCountryData o2) {
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

                Log.d(TAG, "onCreate: CountryData retrieved successfully");

                // after data is fetched several charts are drawn
                setNewCasesColumnChart();
                setNewDeathsColumnChart();
                setPieChart();
                setLineChartDailyCasesDeathsRecovered();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void setEventListenerOnCountryDataInfectionBranchReference(){

        // retrieve data from the database using the reference
        mRootRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                // declaring instance objects
                DbCountryDataInfection dbCountryDataInfection;

                // iterating through all the dates and adding the data at each date to the list
                for (DataSnapshot dateDataSnapshot : snapshot.getChildren()){

                    // fetching data
                    dbCountryDataInfection = dateDataSnapshot.getValue(DbCountryDataInfection.class);

                    // adding to the list
                    infectionRateData.add(dbCountryDataInfection);

                }

                // sort by date correctly by using comparator
                Collections.sort(infectionRateData, new Comparator<DbCountryDataInfection>() {
                    @Override
                    public int compare(DbCountryDataInfection o1, DbCountryDataInfection o2) {
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

                Log.d(TAG, "onCreate: infection data retrieved successfully");

                setInfectionRate();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void setReferenceToCountryDataBranch(){

        // referencing the correct branch CountryData in the database. I.e on the basis of which country's detail screen was clicked
        if(countryName.equals("S. Korea")){
            mRootRef = FirebaseDatabase.getInstance().getReference().child("CountryData").child("South Korea");
        }
        else if (countryName.equals("St. Barth")){
            mRootRef = FirebaseDatabase.getInstance().getReference().child("CountryData").child("Saint Barth");
        }
        else{
            mRootRef = FirebaseDatabase.getInstance().getReference().child("CountryData").child(countryName);
        }
        Log.d(TAG, "onCreate: referenced CountryData branch correctly");

    }

    private void setReferenceToCountryDataInfectionBranch(){

        // referencing the correct branch CountryDataInfection in the database. I.e on the basis of which country's detail screen was clicked
        if(countryName.equals("S. Korea")){
            mRootRef1 = FirebaseDatabase.getInstance().getReference().child("CountryDataInfection").child("South Korea");
        }
        else if (countryName.equals("St. Barth")){
            mRootRef1 = FirebaseDatabase.getInstance().getReference().child("CountryDataInfection").child("Saint Barth");
        }
        else{
            mRootRef1 = FirebaseDatabase.getInstance().getReference().child("CountryDataInfection").child(countryName);
        }
        Log.d(TAG, "onCreate: referenced CountryDataInfection branch correctly");
    }

    private void setNewCasesColumnChart(){

        // Column Chart using anyChart
        AnyChartView anyChartView = findViewById(R.id.any_chart_view_c_new_cases);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);   // very important line of code for multiple charts
        anyChartView.setProgressBar(findViewById(R.id.progress_bar_c_new_cases));

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> casesData = new ArrayList<>();

        // X and Y axis data assigned
        // (Experimental thought) sometime column chart fails to render if cases are below 20 (assumption) or near 0 or 0
        for (int i=0; i<selectedCountryData.size(); i++){

            // ignoring the data on 27-Jun-2021
            if(selectedCountryData.get(i).getDate().equals("27-Jun-2021"))
                continue;

            int cases = Integer.parseInt(selectedCountryData.get(i).getNewCases());

            casesData.add(new ValueDataEntry(selectedCountryData.get(i).getDate(), cases));

        }

        Column column = cartesian.column(casesData);
        column.color("#b2beb5");    // setting column bar color

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        // prettifying the chart title
        cartesian.animation(true);

        // these country names causes problem in render their graphs as they are too big or uses weird symbols
        if(countryName.equals("Lao People's Democratic Republic"))
            cartesian.title("Daily New Cases - " + "Laos");
        else if(countryName.equals("Côte d'Ivoire"))
            cartesian.title("Daily New Cases - " + "Cote d.Ivoire");
        else
            cartesian.title("Daily New Cases - " + countryName);

        cartesian.title().fontColor("#000000");
        cartesian.title().fontOpacity(10);
        cartesian.title().fontStyle("bold");
        cartesian.title().padding(0,0,20,30);    // top, right , bottom, left

        cartesian.yScale().minimum(0d);

        // prettifying the xAxis labels, individual values of x
        cartesian.xAxis(0).labels().fontOpacity(10);
        cartesian.xAxis(0).labels().fontStyle("bold");
        cartesian.xAxis(0).labels().fontColor("#000000");

        // prettifying the yAxis labels, individual values of y
        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");
        cartesian.yAxis(0).labels().fontOpacity(10);
        cartesian.yAxis(0).labels().fontStyle("bold"); // prettifying
        cartesian.yAxis(0).labels().fontColor("#000000");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        // prettifying the xAxis title
        cartesian.xAxis(0).title("Dates");
        cartesian.xAxis(0).title().fontOpacity(10);
        cartesian.xAxis(0).title().fontColor("#000000");
        cartesian.xAxis(0).title().fontStyle("bold");

        // prettifying the yAxis title
        cartesian.yAxis(0).title("Cases");
        cartesian.yAxis(0).title().fontOpacity(10);
        cartesian.yAxis(0).title().fontColor("#000000");
        cartesian.yAxis(0).title().fontStyle("bold");

        anyChartView.setChart(cartesian);
    }

    private void setNewDeathsColumnChart(){

        // Column Chart using anyChart
        AnyChartView anyChartView = findViewById(R.id.any_chart_view_c_new_deaths);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);   // very important line of code for multiple charts
        anyChartView.setProgressBar(findViewById(R.id.progress_bar_c_new_deaths));

        Cartesian cartesian = AnyChart.column();

        List<DataEntry> deathsData = new ArrayList<>();

        // X and Y axis data assigned
        // (Experimental thought) sometime column chart fails to render if cases are below 20 (assumption) or near 0 or 0
        for (int i=0; i<selectedCountryData.size(); i++){

            // ignoring the data on 27-Jun-2021
            if(selectedCountryData.get(i).getDate().equals("27-Jun-2021"))
                continue;

            int deaths = Integer.parseInt(selectedCountryData.get(i).getNewDeaths());

            deathsData.add(new ValueDataEntry(selectedCountryData.get(i).getDate(), deaths));
        }

        Column column = cartesian.column(deathsData);
        column.color("#ae0700");    // setting column bar color

        column.tooltip()
                .titleFormat("{%X}")
                .position(Position.CENTER_BOTTOM)
                .anchor(Anchor.CENTER_BOTTOM)
                .offsetX(0d)
                .offsetY(5d)
                .format("{%Value}{groupsSeparator: }");

        // prettifying the chart title
        cartesian.animation(true);

        // these country names causes problem in render their graphs as they are too big or uses weird symbols
        if(countryName.equals("Lao People's Democratic Republic"))
            cartesian.title("Daily New Deaths - " + "Laos");
        else if(countryName.equals("Côte d'Ivoire"))
            cartesian.title("Daily New Deaths - " + "Cote d.Ivoire");
        else
            cartesian.title("Daily New Deaths - " + countryName);

        cartesian.title().fontColor("#000000");
        cartesian.title().fontOpacity(10);
        cartesian.title().fontStyle("bold");
        cartesian.title().padding(0,0,20,30);    // top, right , bottom, left

        cartesian.yScale().minimum(0d);

        // prettifying the xAxis labels, individual values of x
        cartesian.xAxis(0).labels().fontOpacity(10);
        cartesian.xAxis(0).labels().fontStyle("bold");
        cartesian.xAxis(0).labels().fontColor("#000000");

        // prettifying the yAxis labels, individual values of y
        cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");
        cartesian.yAxis(0).labels().fontOpacity(10);
        cartesian.yAxis(0).labels().fontStyle("bold"); // prettifying
        cartesian.yAxis(0).labels().fontColor("#000000");

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);

        // prettifying the xAxis title
        cartesian.xAxis(0).title("Dates");
        cartesian.xAxis(0).title().fontOpacity(10);
        cartesian.xAxis(0).title().fontColor("#000000");
        cartesian.xAxis(0).title().fontStyle("bold");

        // prettifying the yAxis title
        cartesian.yAxis(0).title("Deaths");
        cartesian.yAxis(0).title().fontOpacity(10);
        cartesian.yAxis(0).title().fontColor("#000000");
        cartesian.yAxis(0).title().fontStyle("bold");

        anyChartView.setChart(cartesian);
    }

    private void setPieChart(){

        // pie chart using anyChart library
        AnyChartView anyChartView = findViewById(R.id.any_chart_view_c_pie_chart);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);   // very important line for multiple charts
        anyChartView.setProgressBar(findViewById(R.id.progress_bar_c_pie_chart));

        Pie pie = AnyChart.pie();

        // setting a click listener to trigger a toast when the pie chart is clicked
        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(Event event) {
                Toast.makeText(GraphModellingActivity.this, event.getData().get("x") + ":" +
                        event.getData().get("value"), Toast.LENGTH_SHORT).show();
            }
        });

        // last index is the latest data
        int listSize = selectedCountryData.size();

        // adding data
        List<DataEntry> lastestCountryCasesData = new ArrayList<>();
        lastestCountryCasesData.add(new ValueDataEntry("Active", Integer.parseInt(selectedCountryData.get(listSize-1).getActiveCases())));
        lastestCountryCasesData.add(new ValueDataEntry("Death", Integer.parseInt(selectedCountryData.get(listSize-1).getTotalDeaths())));
        lastestCountryCasesData.add(new ValueDataEntry("Recovered", Integer.parseInt(selectedCountryData.get(listSize-1).getTotalRecovered())));

        pie.data(lastestCountryCasesData);

        // formatting total cases
        StringNumber stringNumber = new StringNumber();
        String totalCasesFormatted = stringNumber.bigNumberFormatting(selectedCountryData.get(listSize-1).getTotalCases());

        // these country names causes problem in render their graphs as they are too big or uses weird symbols
        if(countryName.equals("Lao People's Democratic Republic"))
            pie.title("Case Analysis : " + "Laos" + " : " + totalCasesFormatted +
                    " Date-" + selectedCountryData.get(listSize-1).getDate());
        else if(countryName.equals("Côte d'Ivoire"))
            pie.title("Case Analysis : " + "Cote d.Ivoire" + " : " + totalCasesFormatted +
                    " Date-" + selectedCountryData.get(listSize-1).getDate());
        else
            pie.title("Case Analysis : " + countryName + " : " + totalCasesFormatted +
                    " Date-" + selectedCountryData.get(listSize-1).getDate());

        // prettifying the pie chart title
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

    private void setLineChartDailyCasesDeathsRecovered(){

        // Line Chart using anyChart
        AnyChartView anyChartView = findViewById(R.id.any_chart_view_c_case_death_recovered_curve);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);   // very important line of code for multiple charts
        anyChartView.setProgressBar(findViewById(R.id.progress_bar_c_case_death_recov_curve));

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        // these country names causes problem in render their graphs as they are too big or uses weird symbols
        if(countryName.equals("Lao People's Democratic Republic"))
            cartesian.title("Trends of Daily Cases, Deaths and Recovered - " + "Laos");
        else if(countryName.equals("Côte d'Ivoire"))
            cartesian.title("Trends of Daily Cases, Deaths and Recovered - " + "Cote d.Ivoire");
        else
            cartesian.title("Trends of Daily Cases, Deaths and Recovered - " + countryName);

        // prettifying chart tile
        cartesian.title().fontOpacity(10);
        cartesian.title().fontStyle("bold");
        cartesian.title().fontColor("#000000");

        // prettifying yAxis tile
        cartesian.yAxis(0).title("Frequency (Tally)");
        cartesian.yAxis(0).title().fontOpacity(10);
        cartesian.yAxis(0).title().fontStyle("bold");
        cartesian.yAxis(0).title().fontColor("#000000");

        // prettifying yAxis labels
        cartesian.yAxis(0).labels().fontOpacity(10);
        cartesian.yAxis(0).labels().fontStyle("bold");
        cartesian.yAxis(0).labels().fontColor("#000000");

        // prettifying xAxis tile
        cartesian.xAxis(0).title("Date");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        cartesian.xAxis(0).title().fontOpacity(10);
        cartesian.xAxis(0).title().fontStyle("bold");
        cartesian.xAxis(0).title().fontColor("#000000");

        // prettifying xAxis labels
        cartesian.xAxis(0).labels().fontOpacity(10);
        cartesian.xAxis(0).labels().fontStyle("bold");
        cartesian.xAxis(0).labels().fontColor("#000000");

        List<DataEntry> seriesData = new ArrayList<>();

        // assigning data
        for (int i=0; i<selectedCountryData.size(); i++){

            // ignoring the data on 27-Jun-2021
            if(selectedCountryData.get(i).getDate().equals("27-Jun-2021"))
                continue;

            String date = selectedCountryData.get(i).getDate();
            int newCases = Integer.parseInt(selectedCountryData.get(i).getNewCases());
            int newDeaths = Integer.parseInt(selectedCountryData.get(i).getNewDeaths());
            int newRecovered = Integer.parseInt(selectedCountryData.get(i).getNewRecovered());

            seriesData.add(new CustomDataEntry(date, newCases, newDeaths, newRecovered));
        }

        Set set = Set.instantiate();
        set.data(seriesData);

        Mapping series1CasesMapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2DeathsMapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3RecoveredMapping = set.mapAs("{ x: 'x', value: 'value3' }");

        Line series1 = cartesian.line(series1CasesMapping);
        series1.name("New Infected Case");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        // setting color (YELLOW) for the line and corresponding Check box
        series1.stroke("#C68E17");

        Line series2 = cartesian.line(series2DeathsMapping);
        series2.name("Death");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        // setting color (RED) for the line and corresponding Check box
        series2.stroke("#C11B17");

        Line series3 = cartesian.line(series3RecoveredMapping);
        series3.name("Recovered");
        series3.hovered().markers().enabled(true);
        series3.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series3.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        // setting color (GREEN) for the line and corresponding Check box
        series3.stroke("#347C2C");

        // prettifying the legend texts New Infected Case, Death, Recovered
        cartesian.legend().enabled(true);
        cartesian.legend().fontColor("#000000");
        cartesian.legend().fontOpacity(10);
        cartesian.legend().fontStyle("bold");
        cartesian.legend().padding(0d, 0d, 30d, 0d);

        anyChartView.setChart(cartesian);

    }

    private void setInfectionRate(){

        // Line Chart using anyChart
        AnyChartView anyChartView = findViewById(R.id.any_chart_view_c_infection_rate_curve);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);   // very important line of code for multiple charts
        anyChartView.setProgressBar(findViewById(R.id.progress_bar_c_infection_rate_curve));

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        // these country names causes problem in render their graphs as they are too big or uses weird symbols
        if(countryName.equals("Lao People's Democratic Republic"))
            cartesian.title("Infection/Positivity Rate Trend line - " + "Laos");
        else if(countryName.equals("Côte d'Ivoire"))
            cartesian.title("Infection/Positivity Rate Trend line - " + "Cote d.Ivoire");
        else
            cartesian.title("Infection/Positivity Rate Trend line - " + countryName);

        // prettifying chart tile
        cartesian.title().fontOpacity(10);
        cartesian.title().fontStyle("bold");
        cartesian.title().fontColor("#000000");

        // prettifying yAxis tile
        cartesian.yAxis(0).title("Infection Rate (%)");
        cartesian.yAxis(0).title().fontOpacity(10);
        cartesian.yAxis(0).title().fontStyle("bold");
        cartesian.yAxis(0).title().fontColor("#000000");

        // prettifying yAxis labels
        cartesian.yAxis(0).labels().fontOpacity(10);
        cartesian.yAxis(0).labels().fontStyle("bold");
        cartesian.yAxis(0).labels().fontColor("#000000");

        // prettifying xAxis tile
        cartesian.xAxis(0).title("Date");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);
        cartesian.xAxis(0).title().fontOpacity(10);
        cartesian.xAxis(0).title().fontStyle("bold");
        cartesian.xAxis(0).title().fontColor("#000000");

        // prettifying xAxis labels
        cartesian.xAxis(0).labels().fontOpacity(10);
        cartesian.xAxis(0).labels().fontStyle("bold");
        cartesian.xAxis(0).labels().fontColor("#000000");

        List<DataEntry> seriesData = new ArrayList<>();

        // assigning data
        for (int i=0; i<infectionRateData.size(); i++){

            // ignoring the data on 27-Jun-2021
            if(infectionRateData.get(i).getDate().equals("27-Jun-2021"))
                continue;

            String date = infectionRateData.get(i).getDate();
            double infectionRate = Double.parseDouble(infectionRateData.get(i).getInfectionRate());

            seriesData.add(new CustomDataEntryTc(date, infectionRate));
        }

        Set set = Set.instantiate();
        set.data(seriesData);

        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name("Infection Rate");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);

        // setting color (YELLOW) for the line and corresponding Check box
        series1.stroke("#BAB86C");

        // prettifying the legend texts New Infected Case, Death, Recovered
        cartesian.legend().enabled(true);
        cartesian.legend().fontColor("#000000");
        cartesian.legend().fontOpacity(10);
        cartesian.legend().fontStyle("bold");
        cartesian.legend().padding(0d, 0d, 30d, 0d);

        anyChartView.setChart(cartesian);

    }

    private static class CustomDataEntryTc extends ValueDataEntry{

        CustomDataEntryTc(String x, Double value){
            super(x, value);
        }
    }

    private static class CustomDataEntry extends ValueDataEntry{

        CustomDataEntry(String x, Number value, Number value2, Number value3){
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

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