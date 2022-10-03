package com.example.coronaupdate1.api;

import com.example.coronaupdate1.DataModel.CountryData;
import com.example.coronaupdate1.DataModel.GlobalData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CovidApiInterface {

    String BASE_URL = "https://corona.lmao.ninja";

    @GET("/v3/covid-19/all")
    Call<GlobalData> getGlobalData();

    @GET("/v3/covid-19/countries/?sort=todayCases")
    Call <List<CountryData>> getAllCountryData();

    @GET("/v3/covid-19/countries/{country}")
    Call <CountryData> getCountryData(
            @Path("country") String country
    );

}
