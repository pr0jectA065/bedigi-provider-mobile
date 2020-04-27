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
import com.bedigi.partner.Adapter.ChatAdapter;
import com.bedigi.partner.Adapter.ChatListAdapter;
import com.bedigi.partner.Model.ChatListModel;
import com.bedigi.partner.Model.ChatModel;
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

public class ChatList extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    LinearLayoutManager LayoutManager;
    List<ChatListModel> list;
    ChatListAdapter verticalAdapter;
    AppPreferences appPrefs;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Chats");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        appPrefs = new AppPreferences(ChatList.this);

        recyclerView = findViewById(R.id.recyclerView);
        LayoutManager = new LinearLayoutManager(ChatList.this, LinearLayoutManager.VERTICAL, false);
        list = new ArrayList<>();

        getList();

    }

    private void getList() {
        dialog = new Dialog(ChatList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        dialog.show();

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().getChatList(appPrefs.getId());
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        dialog.dismiss();
                        JSONObject obj = new JSONObject(response.body().toString());
                        final JSONArray arr = obj.getJSONArray("data");

                        verticalAdapter = new ChatListAdapter(list, ChatList.this);

                        Log.e("API", response.body().toString());

                            list.clear();
                            if (obj.getString("status").matches("true")) {

                                for (int i = 0; i < arr.length(); i++) {
                                    list.add(new ChatListModel(arr.getJSONObject(i).getString("id"),
                                            arr.getJSONObject(i).getString("customer_id"),
                                            arr.getJSONObject(i).getString("provider_id"),
                                            arr.getJSONObject(i).getString("text"),
                                            arr.getJSONObject(i).getString("created_date"),
                                            arr.getJSONObject(i).getString("user_name"),
                                            arr.getJSONObject(i).getString("provider_name"),
                                            arr.getJSONObject(i).getString("service_name"),
                                            arr.getJSONObject(i).getString("service_provider_id")));
                                }

                                verticalAdapter.notifyDataSetChanged();
                                recyclerView.setLayoutManager(LayoutManager);
                                recyclerView.setAdapter(verticalAdapter);

                            } else {
                                Toasty.error(ChatList.this, "No Chats found",
                                        Toast.LENGTH_LONG).show();
                                finish();
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

    public void delete_chat(String customer_id, String provider_id, String service_provider_id) {
        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().chatdelete(customer_id,provider_id,service_provider_id);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        JSONObject obj = new JSONObject(response.body().toString());
                        //JSONArray arr = obj.getJSONArray("data");

                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {
                            getList();
                        } else {
                            Toasty.error(ChatList.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("re", "" + t.toString());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

}
