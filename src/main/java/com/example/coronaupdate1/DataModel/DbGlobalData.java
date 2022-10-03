package com.example.coronaupdate1.DataModel;

public class DbGlobalData {

    private String newCases, date;

    public DbGlobalData(){

    }

    public DbGlobalData(String newCases, String date) {
        this.newCases = newCases;
        this.date = date;
    }

    public String getNewCases() {
        return newCases;
    }

    public void setNewCases(String newCases) {
        this.newCases = newCases;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
