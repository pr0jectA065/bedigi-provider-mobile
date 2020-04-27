package com.bedigi.partner.API;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @GET("invoice/pdf/appointment_id/{appointment_id}")
    Call<JsonObject> downlaod_invoice(@Path("appointment_id") String appointment_id);

    @POST("user/updateimage/user_id/{user_id}")
    Call<JsonObject> updateimage(@Path("user_id") String user_id,@Body JsonObject loginDetails);

    @POST("user/profile/user_id/{user_id}")
    Call<JsonObject> updateprofile(@Path("user_id") String user_id, @Body JsonObject loginDetails);

    @GET("common/state")
    Call<JsonObject> getState();

    @GET("common/city/state_id/{state_id}/")
    Call<JsonObject> getCity(@Path("state_id") String state_id);

    @GET("common/locality/city_id/{city_id}")
    Call<JsonObject> getLocality(@Path("city_id") String city_id);

    @GET("provider/services/provider_id/{provider_id}")
    Call<JsonObject> services(@Path("provider_id") String provider_id);

    @GET("provider/addtimeslot/provider_id/{provider_id}")
    Call<JsonObject> addtimeslot(@Path("provider_id") String provider_id);

    @GET("provider/follow/provider_id/{provider_id}")
    Call<JsonObject> followList(@Path("provider_id") String provider_id);

    @GET("services/list")
    Call<JsonObject> serviceList();

    @GET("Order/applist/provider_id/{provider_id}/today/1")
    Call<JsonObject> todayhistoryList(@Path("provider_id") String provider_id);

    @GET("Order/applist/provider_id/{provider_id}/upcoming/1")
    Call<JsonObject> upcominghistoryList(@Path("provider_id") String provider_id);

    @GET("Order/applist/provider_id/{provider_id}")
    Call<JsonObject> historyList(@Path("provider_id") String provider_id);

    @GET("services/chatlist/provider_id/{provider_id}")
    Call<JsonObject> getChatList(@Path("provider_id") String provider_id);

    @POST("provider/services/provider_id/{provider_id}")
    Call<JsonObject> add_service(@Path("provider_id") String provider_id, @Body JsonObject loginDetails);

    @POST("Provider/addtimeslot/provider_id/{provider_id}/")
    Call<JsonObject> saveTimeSlotAPI(@Path("provider_id") String provider_id, @Body JsonObject loginDetails);

    @POST("provider/services/provider_id/{provider_id}/")
    Call<JsonObject> update_service(@Path("provider_id") String provider_id, @Body JsonObject loginDetails);

    @POST("Order/appointmentstatus/appointment_id/{appointment_id}")
    Call<JsonObject> appointmentstatus(@Path("appointment_id") String appointment_id, @Body JsonObject loginDetails);

    @POST("order/start_end_service/appointment_id/{appointment_id}")
    Call<JsonObject> start_end_service(@Path("appointment_id") String appointment_id, @Body JsonObject loginDetails);

    @POST("services/chat/service_provider_id/{service_provider_id}")
    Call<JsonObject> sendChatMessage(@Path("service_provider_id") String service_provider_id,@Body JsonObject obj);

    @GET("services/chat/customer_id/{customer_id}/provider_id/{provider_id}/service_provider_id/{service_provider_id}")
    Call<JsonObject> getChatMessage(@Path("customer_id") String customer_id,@Path("provider_id") String provider_id,@Path("service_provider_id") String service_provider_id);

    @DELETE("services/chatlist/{customer_id}/{provider_id}/{service_provider_id}")
    Call<JsonObject> chatdelete(@Path("customer_id") String customer_id,@Path("provider_id") String provider_id,@Path("service_provider_id") String service_provider_id);

}
