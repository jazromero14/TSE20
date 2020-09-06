package com.udbstudents.tseapp;

import com.udbstudents.tseapp.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RetrofitService {

    @GET("api/user") //anotacion del api
    Call<List<User>> getUsers();
}
