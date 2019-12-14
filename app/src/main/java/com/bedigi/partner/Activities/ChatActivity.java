package com.bedigi.partner.Activities;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bedigi.partner.API.RetrofitAPI;
import com.bedigi.partner.Adapter.ChatAdapter;
import com.bedigi.partner.Model.ChatModel;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;
import com.google.gson.JsonObject;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    Toolbar toolbar;
    RecyclerView recyclerView;
    EditText comments;
    ImageButton send;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Dialog dialog;
    LinearLayoutManager LayoutManager;
    ChatAdapter verticalAdapter;
    protected AppPreferences appPrefs;
    String question_id, student_id, teacher_id;
    String comments_string;
    List<ChatModel> list;

    private final int FIVE_SECONDS = 1000;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chat");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        appPrefs = new AppPreferences(ChatActivity.this);

        recyclerView = (RecyclerView) findViewById(R.id.list);
        comments = (EditText) findViewById(R.id.comments);
        send = (ImageButton) findViewById(R.id.send);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        LayoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        list = new ArrayList<>();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comments.getText().toString().trim().isEmpty()) {

                } else {
                    comments_string=comments.getText().toString().trim();
                    comments.setText("");
                    sendComment();
                }
            }
        });

        getCommentList();

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                getCommentList();
                handler.postDelayed(this, FIVE_SECONDS);
            }
        }, FIVE_SECONDS);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    private void getCommentList() {
        dialog = new Dialog(ChatActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        //dialog.show();
        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().getChatMessage(getIntent().getStringExtra("customer_id"),appPrefs.getId(),
                    getIntent().getStringExtra("service_provider_id"));
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        dialog.dismiss();
                        mSwipeRefreshLayout.setRefreshing(false);
                        JSONObject obj = new JSONObject(response.body().toString());
                        final JSONArray arr = obj.getJSONArray("data");

                        verticalAdapter = new ChatAdapter(list, ChatActivity.this,getIntent().getStringExtra("user_name"),
                                getIntent().getStringExtra("provider_name"));

                        Log.e("API", response.body().toString());

                        if(arr.length() == list.size()){

                        } else {

                            list.clear();
                            if (obj.getString("status").matches("true")) {

                                for (int i = 0; i < arr.length(); i++) {
                                    list.add(new ChatModel(arr.getJSONObject(i).getString("id"),
                                            arr.getJSONObject(i).getString("customer_id"),
                                            arr.getJSONObject(i).getString("provider_id"),
                                            arr.getJSONObject(i).getString("text"),
                                            arr.getJSONObject(i).getString("created_date"),
                                            arr.getJSONObject(i).getString("sent_by")));
                                }

                                verticalAdapter.notifyDataSetChanged();
                                recyclerView.setLayoutManager(LayoutManager);
                                recyclerView.setAdapter(verticalAdapter);
                                recyclerView.smoothScrollToPosition(list.size()-1);

                            } else {
                                Toasty.error(ChatActivity.this, "No Appointments found",
                                        Toast.LENGTH_LONG).show();
                            }

                        }



                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("re", "" + t.toString());
                }
            });

        } catch (Exception e) {
            dialog.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            e.printStackTrace();
        }
    }

    private void sendComment() {
        dialog = new Dialog(ChatActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        //dialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("customer_id", getIntent().getStringExtra("customer_id"));
        jsonObject.addProperty("provider_id", appPrefs.getId());
        jsonObject.addProperty("text", comments_string);
        jsonObject.addProperty("sent_by", "2");

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().sendChatMessage(getIntent().getStringExtra("service_provider_id"),jsonObject);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        dialog.dismiss();
                        JSONObject obj = new JSONObject(response.body().toString());
                        //JSONArray arr = obj.getJSONArray("data");

                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {
                            getCommentList();
                        } else {
                            Toasty.error(ChatActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
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
    public void onRefresh() {
        getCommentList();
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
