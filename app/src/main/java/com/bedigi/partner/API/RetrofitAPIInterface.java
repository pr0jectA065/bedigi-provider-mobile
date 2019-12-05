package com.bedigi.partner.API;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface RetrofitAPIInterface {

    @POST("user/login")
    Call<JsonObject> login(@Body JsonObject loginDetails);

    @POST("user/changepassword")
    Call<JsonObject> changepassword(@Body JsonObject loginDetails);

    @POST("user/process_otp")
    Call<JsonObject> process_otp(@Body JsonObject loginDetails);

    @GET("user/profile/user_id/{user_id}/")
    Call<JsonObject> Profile(@Path("user_id") String user_id);

    @POST("user/updateimage/user_id/{user_id}")
    Call<JsonObject> updateimage(@Path("user_id") String user_id,@Body JsonObject loginDetails);

    @POST("user/profile/user_id/{user_id}")
    Call<JsonObject> updateprofile(@Path("user_id") String user_id, @Body JsonObject loginDetails);

    @GET("common/state")
    Call<JsonObject> getState();

    @GET("common/city/state_id/{state_id}/")
    Call<JsonObject> getCity(@Path("state_id") String state_id);

    @GET("provider/services/provider_id/{provider_id}")
    Call<JsonObject> services(@Path("provider_id") String provider_id);

    @GET("services/list")
    Call<JsonObject> serviceList();

    @POST("provider/services/provider_id/{provider_id}")
    Call<JsonObject> add_service(@Path("provider_id") String provider_id, @Body JsonObject loginDetails);

    @POST("Provider/addtimeslot/provider_id/{provider_id}/")
    Call<JsonObject> saveTimeSlotAPI(@Path("provider_id") String provider_id, @Body JsonObject loginDetails);

    @POST("provider/services/provider_id/{provider_id}/")
    Call<JsonObject> update_service(@Path("provider_id") String provider_id, @Body JsonObject loginDetails);

}
