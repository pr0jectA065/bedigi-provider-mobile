package com.bedigi.partner.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bedigi.partner.API.RetrofitAPI;
import com.bedigi.partner.Activities.Appointments;
import com.bedigi.partner.Adapter.FollowersAdapter;
import com.bedigi.partner.Adapter.HistoryAdapter;
import com.bedigi.partner.Model.FollowersModel;
import com.bedigi.partner.Model.HistoryModel;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;
import com.google.gson.JsonObject;

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
public class FollowersList extends Fragment {

    RecyclerView recycler_view;
    LinearLayoutManager layout;
    List<FollowersModel> list;
    Context context;
    FollowersAdapter verticalAdapter;
    Dialog dialog;
    AppPreferences appPreferences;

    public FollowersList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_followers_list, container, false);

        context = getActivity();
        recycler_view = (RecyclerView) view.findViewById(R.id.recycler_view);
        layout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        appPreferences=new AppPreferences(context);

        getList();

        return view;
    }

    private void getList() {
        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().followList(appPreferences.getId());
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        JSONObject obj = new JSONObject(response.body().toString());
                        final JSONArray arr = obj.getJSONArray("data");
                        list = new ArrayList<>();
                        verticalAdapter = new FollowersAdapter(list, context);

                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {

                            for (int i = 0; i < arr.length(); i++) {
                                list.add(new FollowersModel(arr.getJSONObject(i).getString("follow_id"),
                                        arr.getJSONObject(i).getString("customer_id"),
                                        arr.getJSONObject(i).getString("customer_name"),
                                        arr.getJSONObject(i).getString("phone"),
                                        arr.getJSONObject(i).getString("user_pic")));
                            }

                            verticalAdapter.notifyDataSetChanged();
                            recycler_view.setLayoutManager(layout);
                            recycler_view.setAdapter(verticalAdapter);

                        } else {
                            Toasty.error(context, "No Followers!",
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

}
