package com.example.coronaupdate1.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coronaupdate1.CountryDetailActivity;
import com.example.coronaupdate1.CustomCountryAdapter;
import com.example.coronaupdate1.DataModel.CountryData;
import com.example.coronaupdate1.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CountryFragment extends Fragment {

    private static final String TAG = "CountryFragment";
    private final Context context;
    private List<CountryData> countryDataList;

    RecyclerView recyclerView;
    CustomCountryAdapter customCountryAdapter;

    public CountryFragment(Context context, List<CountryData> countryDataList){
        this.context = context;
        this.countryDataList = countryDataList;
    }


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        // inflating (filling) the fragment with fragment country layout
        View view = inflater.inflate(R.layout.fragment_country, null);

        // so that the actionBar widgets are displayed
        setHasOptionsMenu(true);

        // get the reference of the recyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        // set linear layout manager for vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        // call the constructor of adapter class to send reference and data to adapter
        // context was passed from main activity via constructor
        // set the Adapter to RecyclerView
        customCountryAdapter = new CustomCountryAdapter(context, countryDataList);
        recyclerView.setAdapter(customCountryAdapter);

        return view;
    }

    // creating the action bar search box
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {

        // inflating with the menu that is fragment country menu
        inflater.inflate(R.menu.fragment_country_menu, menu);

        // referencing the actionBar search view and setting up the on query listener
        // this method will trigger when the user enters a query in the search box
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // to eliminate all the whitespace in query
                query = query.replaceAll("\\s+","");
                Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onQueryTextSubmit: query after removing whitespace : " + query);

                queryProcessing(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // eliminating whitespace and converting to all lowercase
                newText = newText.replaceAll("\\s+", "");
                newText = newText.toLowerCase();
                int newTextSize = newText.length();
                Log.d(TAG, "onQueryTextChange: new text - " + newText);

                // we take a subString of the country names available from beginning index to the size of newText and compare
                // those who match we make a new list and use a constructor of customAdapter to create a new object and
                // then set that new adapter as the adapter of the recylerview
                List<CountryData> countryDataListNew = new ArrayList<>();
                for (int i=0; i<countryDataList.size(); i++){

                    String lowCaseCountryName = countryDataList.get(i).getCountryName().toLowerCase();
                    String subStringCountryName = "";

                    // the subString will be from the beginning index to size of the new text
                    // newTextSize maybe be longer than a country name
                    if(lowCaseCountryName.length() >= newTextSize )
                        subStringCountryName = lowCaseCountryName.substring(0, newTextSize);

                    if(newText.compareTo(subStringCountryName) == 0){
                        Log.d(TAG, "onQueryTextChange: matched country name - " + lowCaseCountryName + " subString - " + subStringCountryName + " newText - " + newText);
                        CountryData countryData = countryDataList.get(i);
                        countryDataListNew.add(countryData);
                    }
                }
                // use constructor and set the adapter
                customCountryAdapter = new CustomCountryAdapter(context ,countryDataListNew);
                recyclerView.setAdapter(customCountryAdapter);

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    // what to do with query
    // we open the countryDetailActivity of the queried country (countryName)
    private void queryProcessing(String query){
        Log.d(TAG, "queryProcessing: before matching query - " + query);

        String queryAllLowerCase = query.toLowerCase();

        for (int i=0; i<countryDataList.size(); i++){

            String countryNameAllLowerCase = countryDataList.get(i).getCountryName().toLowerCase();

            if(countryNameAllLowerCase.equals( queryAllLowerCase )){
                Log.d(TAG, "queryProcessing: matched detail activity starting");
                // intent switching to another activity (CountryDetailActivity)
                Intent intent = new Intent(getContext(), CountryDetailActivity.class);

                intent.putExtra("country_name", countryDataList.get(i).getCountryName());
                intent.putExtra("flag_image", countryDataList.get(i).getCountryInfo().getFlag());
                intent.putExtra("active_cases", Integer.toString(countryDataList.get(i).getActiveCases()));
                intent.putExtra("total_cases", Integer.toString(countryDataList.get(i).getTotalCases()));
                intent.putExtra("new_cases", Integer.toString(countryDataList.get(i).getNewCases()));
                intent.putExtra("total_deaths", Integer.toString(countryDataList.get(i).getTotalDeaths()));
                intent.putExtra("new_deaths", Integer.toString(countryDataList.get(i).getNewDeaths()));
                intent.putExtra("total_recovered", Integer.toString(countryDataList.get(i).getTotalRecovered()));
                intent.putExtra("new_recovered", Integer.toString(countryDataList.get(i).getNewRecovered()));
                intent.putExtra("total_tests", Integer.toString(countryDataList.get(i).getTotalTests()));

                context.startActivity(intent);

                break;
            }

        }
    }

}
