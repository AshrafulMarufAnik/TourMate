package com.anik.example.tourmate.retrofit;

import com.anik.example.tourmate.NearbyPlacesModelClass.Example;
import com.anik.example.tourmate.PlaceAPI.Predictions;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("place/autocomplete/json")
    Call<Predictions> getPlacesAutoComplete(
            @Query("input") String input,
            @Query("types") String types,
            @Query("location") String location,
            @Query("radius") String radius,
            @Query("key") String key);

    @GET("place/nearbysearch/json?sensor=true&key=AIzaSyB9MxxJBmzLHdfsMEZSdV0vORR_MRwirPI")
    Call<Example> getNearbyPlaces(
            @Query("type") String type,
            @Query("location") String location,
            @Query("radius") int radius);

}
