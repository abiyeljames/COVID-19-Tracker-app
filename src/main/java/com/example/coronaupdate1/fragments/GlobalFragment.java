package com.example.coronaupdate1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coronaupdate1.DataModel.GlobalData;
import com.example.coronaupdate1.GraphGlobalCases;
import com.example.coronaupdate1.R;
import com.example.coronaupdate1.utility.StringNumber;

import org.jetbrains.annotations.NotNull;

public class GlobalFragment extends Fragment {

    private static final String TAG = "Global Fragment";
    private final GlobalData globalData;

    public GlobalFragment(GlobalData globalData){
        this.globalData = globalData;
    }


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @org.jetbrains.annotations.NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        // inflating the fragment with fragment_global layout
        View view = inflater.inflate(R.layout.fragment_global, null);

        // to make the options (graph button) appear in your Toolbar
        setHasOptionsMenu(true);

        StringNumber stringNumber = new StringNumber();

        // referencing the views
        TextView globalActiveCasesView = view.findViewById(R.id.global_active_cases);
        globalActiveCasesView.setText(stringNumber.bigNumberFormatting(Integer.toString(globalData.getActiveCases())));


        TextView globalTotalCasesView = view.findViewById(R.id.global_total_cases);
        globalTotalCasesView.setText(stringNumber.bigNumberFormatting(Integer.toString(globalData.getTotalCases())));


        TextView globalTotalDeathsView = view.findViewById(R.id.global_total_deaths);
        globalTotalDeathsView.setText(stringNumber.bigNumberFormatting(Integer.toString(globalData.getTotalDeaths())));


        TextView globalTotalRecooveredView = view.findViewById(R.id.global_total_recovered);
        globalTotalRecooveredView.setText(stringNumber.bigNumberFormatting(Integer.toString(globalData.getTotalRecovered())));


        TextView globalDailyNewCasesView = view.findViewById(R.id.global_daily_new_cases);
        globalDailyNewCasesView.setText(stringNumber.bigNumberFormatting(Integer.toString(globalData.getNewCases())));


        TextView globalDailyNewRecoveredView = view.findViewById(R.id.global_daily_new_recovered);
        globalDailyNewRecoveredView.setText(stringNumber.bigNumberFormatting(Integer.toString(globalData.getNewRecovered())));

        return view;
    }

    // create action bar button
    @Override
    public void onCreateOptionsMenu(@NonNull @NotNull Menu menu, @NonNull @NotNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_global_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    // handle action bar button activities
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {

        int id = item.getItemId();

        // if the graph button in the action bar is clicked the following will be done
        if(id == R.id.graph_button_global){
            Log.d(TAG, "onOptionsItemSelected: Graph button clicked");
            Toast.makeText(getContext(), "Graph" , Toast.LENGTH_SHORT).show();

            // passing data to the next activity
            Intent intent = new Intent(getContext(), GraphGlobalCases.class);
            intent.putExtra("total_cases", Integer.toString(globalData.getTotalCases()));
            intent.putExtra("active_cases", Integer.toString(globalData.getActiveCases()));
            intent.putExtra("total_deaths", Integer.toString(globalData.getTotalDeaths()));
            intent.putExtra("total_recovered", Integer.toString(globalData.getTotalRecovered()));

            startActivity(intent);

            return  true;
        }

        return super.onOptionsItemSelected(item);
    }
}
