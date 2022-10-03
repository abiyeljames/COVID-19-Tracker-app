package com.example.coronaupdate1.DataModel;

public class DbCountryDataInfection {

    private String infectionRate, date;

    public DbCountryDataInfection(){

    }

    public DbCountryDataInfection(String infectionRate, String date) {
        this.infectionRate = infectionRate;
        this.date = date;
    }

    public String getInfectionRate() {
        return infectionRate;
    }

    public void setInfectionRate(String infectionRate) {
        this.infectionRate = infectionRate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
