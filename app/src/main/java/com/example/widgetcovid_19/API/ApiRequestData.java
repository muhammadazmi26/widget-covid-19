package com.example.widgetcovid_19.API;

import com.example.widgetcovid_19.Model.ResponseModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiRequestData {

    @GET("recovered")
    Call<List<ResponseModel>> ardGetDataCovid();

//    @GET("api-covid19.php")
//    Call<List<ResponseModel>> ardGetDataCovid();

}
