package com.bedigi.partner.Fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bedigi.partner.API.RetrofitAPI;
import com.bedigi.partner.Activities.AddService;
import com.bedigi.partner.Adapter.PackageAdapter;
import com.bedigi.partner.Model.PackageData;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.tuann.floatingactionbuttonexpandable.FloatingActionButtonExpandable;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyServices extends Fragment {

    RecyclerView recyclerView;
    PackageAdapter verticalAdapter;
    LinearLayoutManager layout;
    List<PackageData> data;
    AppPreferences appPreferences;
    Dialog dialog;
    Context context;
    FloatingActionButton add_service;
    FloatingActionButtonExpandable fab;

    public MyServices() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_services, container, false);

        context = getActivity();

        appPreferences = new AppPreferences(context);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        layout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        /*add_service = view.findViewById(R.id.add_service);
        add_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_service();
            }
        });
*/
        fab = view.findViewById(R.id.fab);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab.collapse(true);
                } else {
                    fab.expand(true);
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_service();
            }
        });

        return view;
    }

    private void add_service() {
        Intent intent = new Intent(context, AddService.class);
        intent.putExtra("type","add");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        getInfo();
    }

    private void getInfo() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        dialog.show();

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().services(appPreferences.getId());
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        dialog.dismiss();
                        JSONObject obj = new JSONObject(response.body().toString());

                        final JSONArray arr = obj.getJSONArray("data");

                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {

                            data = new ArrayList<>();
                            verticalAdapter = new PackageAdapter(data, context, new PackageAdapter.EventListener() {
                                @Override
                                public void onEvent(String type, int pos) {

                                    if(type.matches("1")){
                                        try {
                                            Intent intent = new Intent(context,AddService.class);
                                            intent.putExtra("type","update");
                                            intent.putExtra("json",arr.getJSONObject(pos).toString());
                                            startActivity(intent);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    } else if(type.matches("2")){
                                        try{

                                            if(!(arr.getJSONObject(pos).getString("image").matches(""))){


                                            }

                                        } catch (Exception e){
                                            e.printStackTrace();
                                        }

                                    }

                                }
                            });

                            for (int i = 0; i < arr.length(); i++) {

                                data.add(new PackageData("",
                                        arr.getJSONObject(i).getString("service_id"),
                                        arr.getJSONObject(i).getString("service_name"),
                                        "",
                                        arr.getJSONObject(i).getString("price"),
                                        arr.getJSONObject(i).getString("sellprice"),
                                        arr.getJSONObject(i).getString("image"),
                                        arr.getJSONObject(i).getString("description"),
                                        "",
                                        "",
                                        "",
                                       "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        "",
                                        arr.getJSONObject(i).getString("discount"),
                                        "",
                                       "",
                                        new JSONArray(),
                                        arr.getJSONObject(i).getString("no_of_hours")));

                            }

                            verticalAdapter.notifyDataSetChanged();
                            recyclerView.setLayoutManager(layout);
                            recyclerView.setAdapter(verticalAdapter);

                        } else {
                            Toasty.error(context, obj.getString("msg"), Toast.LENGTH_LONG).show();
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

}
