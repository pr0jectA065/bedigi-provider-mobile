package com.bedigi.partner.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.bedigi.partner.API.RetrofitAPI;
import com.bedigi.partner.Adapter.HistoryAdapter;
import com.bedigi.partner.Model.HistoryModel;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;

public class Appointments extends AppCompatActivity {

    RecyclerView recycler_view;
    LinearLayoutManager layout;
    List<HistoryModel> list;
    HistoryAdapter verticalAdapter;
    Dialog dialog;
    AppPreferences appPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("All Appointmements");

        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        layout = new LinearLayoutManager(Appointments.this, LinearLayoutManager.VERTICAL, false);

        appPreferences=new AppPreferences(Appointments.this);

    }

    @Override
    public void onResume() {
        super.onResume();
        getAppointmenst();

    }

    private void getAppointmenst() {

        dialog = new Dialog(Appointments.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        dialog.show();

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().historyList(appPreferences.getId());
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        dialog.dismiss();
                        JSONObject obj = new JSONObject(response.body().toString());
                        final JSONArray arr = obj.getJSONArray("data");
                        list = new ArrayList<>();
                        verticalAdapter = new HistoryAdapter(list, Appointments.this,"all", new HistoryAdapter.EventListener() {
                            @Override
                            public void onEvent(final String type, final String id, final String pos,
                                                final String time, final String date) {

                                if(type.matches("1")){
                                    appointmentstatus("1",id);
                                } else if(type.matches("3")){
                                    appointmentstatus("3",id);
                                }

                            }
                        });

                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {

                            for (int i = 0; i < arr.length(); i++) {
                                list.add(new HistoryModel(arr.getJSONObject(i).getString("appointment_id"),
                                        arr.getJSONObject(i).getString("user_name"),
                                        arr.getJSONObject(i).getString("date"),
                                        arr.getJSONObject(i).getString("early_morning"),
                                        arr.getJSONObject(i).getString("morning"),
                                        arr.getJSONObject(i).getString("afternoon"),
                                        arr.getJSONObject(i).getString("late_afternoon"),
                                        arr.getJSONObject(i).getString("evening"),
                                        arr.getJSONObject(i).getString("status"),
                                        arr.getJSONObject(i).getString("is_past_date"),
                                        arr.getJSONObject(i).getString("service_name"),
                                        arr.getJSONObject(i).getString("provider_name"),
                                        arr.getJSONObject(i).getString("provider_email"),
                                        arr.getJSONObject(i).getString("provider_phone")));
                            }

                            verticalAdapter.notifyDataSetChanged();
                            recycler_view.setLayoutManager(layout);
                            recycler_view.setAdapter(verticalAdapter);

                        } else {
                            Toasty.error(Appointments.this, "No Appointments found",
                                    Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("re", "" + t.toString());
                }
            });

        } catch (Exception e) {
            dialog.dismiss();
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private  void appointmentstatus(String status,String appointment_id){

        dialog = new Dialog(Appointments.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        dialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("status", status);

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().appointmentstatus(appointment_id,jsonObject);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        dialog.dismiss();
                        JSONObject obj = new JSONObject(response.body().toString());
                        //JSONArray arr = obj.getJSONArray("data");

                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {
                            Toasty.success(Appointments.this,obj.getString("message"),Toast.LENGTH_LONG).show();
                            getAppointmenst();
                        } else {
                            Toasty.error(Appointments.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        dialog.dismiss();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dialog.dismiss();
                    Log.e("re", "" + t.toString());
                }
            });

        } catch (Exception e) {
            dialog.dismiss();
            e.printStackTrace();
        }
    }


}
