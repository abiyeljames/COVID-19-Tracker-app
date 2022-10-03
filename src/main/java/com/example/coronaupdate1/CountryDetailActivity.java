package com.example.coronaupdate1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coronaupdate1.utility.StringNumber;
import com.squareup.picasso.Picasso;


public class CountryDetailActivity extends AppCompatActivity {

    private static final String TAG = "CountryDetailActivity";

    private String countryName;
    private String flagImage;
    private String activeCases;
    private String totalCases;
    private String newCases;
    private String totalDeaths;
    private String newDeaths;
    private String totalRecovered;
    private String newRecovered;
    private String totalTests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_detail);
        Log.d(TAG, "onCreate: started");

        // creating the up button or back button on the top action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get intent extras which were passed when this activity was started
        getIncomingIntent();

        // setting the data received via intent extra to their respective views
        setDataToViews();
    }

    private void getIncomingIntent(){

        if(getIntent().hasExtra("country_name") && getIntent().hasExtra("flag_image")
                && getIntent().hasExtra("active_cases") && getIntent().hasExtra("total_cases")
                && getIntent().hasExtra("new_cases") && getIntent().hasExtra("total_deaths")
                && getIntent().hasExtra("new_deaths") && getIntent().hasExtra("total_recovered")
                && getIntent().hasExtra("new_recovered") && getIntent().hasExtra("total_tests")){

            // getting the data which was passed into the intent from the previous activity
            countryName = getIntent().getStringExtra("country_name");
            flagImage = getIntent().getStringExtra("flag_image");
            activeCases = getIntent().getStringExtra("active_cases");
            totalCases = getIntent().getStringExtra("total_cases");
            newCases = getIntent().getStringExtra("new_cases");
            totalDeaths = getIntent().getStringExtra("total_deaths");
            newDeaths = getIntent().getStringExtra("new_deaths");
            totalRecovered = getIntent().getStringExtra("total_recovered");
            newRecovered = getIntent().getStringExtra("new_recovered");
            totalTests = getIntent().getStringExtra("total_tests");

            StringNumber stringNumber = new StringNumber();

            // numbers are formatted to have commas in between digits
            activeCases = stringNumber.bigNumberFormatting(activeCases);
            totalCases = stringNumber.bigNumberFormatting(totalCases);
            newCases = stringNumber.bigNumberFormatting(newCases);
            totalDeaths = stringNumber.bigNumberFormatting(totalDeaths);
            newDeaths = stringNumber.bigNumberFormatting(newDeaths);
            totalRecovered = stringNumber.bigNumberFormatting(totalRecovered);
            newRecovered = stringNumber.bigNumberFormatting(newRecovered);
            totalTests = stringNumber.bigNumberFormatting(totalTests);

        }
    }

    private void setDataToViews(){

        // setting the title of the actionBar on top to the countryName which was clicked
        getSupportActionBar().setTitle(countryName);

        // referencing the respective views
        TextView nameCountryView = findViewById(R.id.c_name_country);
        ImageView imageFlag = findViewById(R.id.c_flag_image);
        TextView casesActiveView = findViewById(R.id.c_active_cases_num);
        TextView casesTotalView = findViewById(R.id.c_total_cases_num);
        TextView casesNewView = findViewById(R.id.c_new_cases_num);
        TextView deathsTotalView = findViewById(R.id.c_total_deaths_num);
        TextView deathsNewView = findViewById(R.id.c_new_deaths_num);
        TextView recoveredTotalView = findViewById(R.id.c_total_recovered_num);
        TextView recoveredNewView = findViewById(R.id.c_new_recovered_num);
        TextView testsTotalView = findViewById(R.id.c_total_tests_num);

        // assigning data to the respective referenced views
        Picasso.with(CountryDetailActivity.this).load(flagImage).into(imageFlag);
        nameCountryView.setText(countryName);
        casesActiveView.setText(activeCases);
        casesTotalView.setText(totalCases);
        casesNewView.setText(newCases);
        deathsTotalView.setText(totalDeaths);
        deathsNewView.setText(newDeaths);
        recoveredTotalView.setText(totalRecovered);
        recoveredNewView.setText(newRecovered);
        testsTotalView.setText(totalTests);

    }

    // create action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.country_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle action bar button activities
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.graph_button) {

            Toast.makeText(this, "Graph" , Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getApplicationContext(), GraphModellingActivity.class);

            // putting data in intent to pass to next activity
            intent.putExtra("country_name", countryName);

            startActivity(intent);

        }

        // making the up button do the same as back button
        if(id == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}