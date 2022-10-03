package com.example.coronaupdate1.DataModel;

import com.google.gson.annotations.SerializedName;

public class GlobalData {

    @SerializedName("active")
    private int activeCases;
    @SerializedName("cases")
    private int totalCases;
    @SerializedName("deaths")
    private int totalDeaths;
    @SerializedName("recovered")
    private int totalRecovered;
    @SerializedName("todayCases")
    private int newCases;
    @SerializedName("todayRecovered")
    private int newRecovered;

    public GlobalData(int activeCases, int totalCases, int totalDeaths, int totalRecovered, int newCases, int newRecovered){
        this.activeCases = activeCases;
        this.totalCases = totalCases;
        this.totalDeaths = totalDeaths;
        this.totalRecovered = totalRecovered;
        this.newCases = newCases;
        this.newRecovered = newRecovered;
    }

    public int getActiveCases() {
        return activeCases;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public int getTotalRecovered() {
        return totalRecovered;
    }

    public int getNewCases() {
        return newCases;
    }

    public int getNewRecovered() {
        return newRecovered;
    }

    public void setActiveCases(int activeCases) {
        this.activeCases = activeCases;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }

    public void setTotalDeaths(int totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public void setTotalRecovered(int totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

    public void setNewCases(int newCases) {
        this.newCases = newCases;
    }

    public void setNewRecovered(int newRecovered) {
        this.newRecovered = newRecovered;
    }
}
