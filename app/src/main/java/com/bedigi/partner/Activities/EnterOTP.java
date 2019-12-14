package com.bedigi.partner.Activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bedigi.partner.API.RetrofitAPI;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonObject;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;

public class EnterOTP extends AppCompatActivity {

    CircularProgressButton confirm;
    ImageButton back;
    OtpView otp_view;
    String where;
    AppPreferences appPreferences;
    Dialog dialog;
    //SmsVerifyCatcher smsVerifyCatcher;
    TextView tv;
    String android_id = "";

    TextView resendOTP,timeleft;
    String otp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);

        appPreferences = new AppPreferences(EnterOTP.this);

        otp = getIntent().getStringExtra("otp");

        tv = (TextView) findViewById(R.id.tv);
        timeleft = (TextView) findViewById(R.id.timeleft);

        tv.setText("Please enter verification code sent to " + getIntent().getStringExtra("phone"));
        where = getIntent().getStringExtra("where");
        otp_view = (OtpView) findViewById(R.id.otp_view);

        confirm = (CircularProgressButton) findViewById(R.id.confirm);
        back = (ImageButton) findViewById(R.id.back);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (otp_view.getText().toString().length() == 4 && otp_view.getText().toString().matches(otp)) {
                    login();
                } else {
                    Toasty.error(EnterOTP.this, "Please enter correct OTP", Toast.LENGTH_LONG).show();
                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        resendOTP = (TextView) findViewById(R.id.resendOTP);
        resendOTP.setTextColor(getResources().getColor(R.color.grey));
        resendOTP.setEnabled(false);

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp_view.setText("");
                requestHint();
                otp();

                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        timeleft.setText("00:" + millisUntilFinished / 1000);
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        //timeleft.setText("done!");
                        //otp="";
                        resendOTP.setTextColor(getResources().getColor(R.color.colorPrimary));
                        resendOTP.setEnabled(true);
                    }

                }.start();

            }
        });

        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeleft.setText("00:" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                //timeleft.setText("done!");
                //otp="";
                resendOTP.setTextColor(getResources().getColor(R.color.colorPrimary));
                resendOTP.setEnabled(true);
            }

        }.start();

        otp_view.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp1) {
                Log.d("onOtpCompleted=>", otp1);
                if (otp1.length() == 4 && otp1.matches(otp)) {
                    otp = "";
                    login();

                } else {
                    //Toasty.error(EnterOTP.this, "Please enter correct OTP", Toast.LENGTH_LONG).show();
                }

            }
        });

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                android_id = userId;
                Log.d("android_id", android_id);
                if (registrationId != null)
                    Log.d("debug", "registrationId:" + registrationId);
            }
        });

        requestHint();
    }

    private void otp() {
        confirm.startAnimation();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile_number", getIntent().getStringExtra("phone"));

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().process_otp(jsonObject);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        confirm.stopAnimation();
                        confirm.revertAnimation();

                        JSONObject obj = new JSONObject(response.body().toString());
                        //JSONArray arr = obj.getJSONArray("data");

                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {
                            otp = obj.getJSONObject("data").getString("otp");
                            Toasty.info(EnterOTP.this,otp,Toast.LENGTH_LONG).show();
                        } else {
                            Toasty.error(EnterOTP.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        confirm.stopAnimation();
                        confirm.revertAnimation();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    confirm.stopAnimation();
                    confirm.revertAnimation();
                    Log.e("re", "" + t.toString());
                }
            });

        } catch (Exception e) {
            confirm.stopAnimation();
            confirm.revertAnimation();
            e.printStackTrace();
        }
    }

    private void login() {

        confirm.startAnimation();

        try {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("mobile_number", getIntent().getStringExtra("phone"));
            jsonObject.addProperty("device_id", android_id);
            jsonObject.addProperty("user_type", "3");

            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().login(jsonObject);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {

                        confirm.stopAnimation();
                        confirm.revertAnimation();
                        JSONObject obj = new JSONObject(response.body().toString());
                        Log.e("API", response.body().toString());
                        if (obj.getString("status").matches("true")) {
                            Toasty.success(EnterOTP.this, obj.getString("message"), Toast.LENGTH_LONG).show();

                            appPreferences.setId(obj.getJSONObject("data").getString("user_id"));
                            appPreferences.setPhone(obj.getJSONObject("data").getString("phone"));
                            appPreferences.setReferral(obj.getJSONObject("data").getString("referral_code"));
                            appPreferences.setImage(obj.getJSONObject("data").getString("user_pic"));

                            Intent mainIntent = new Intent(EnterOTP.this, MainActivity.class);
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();

                        } else {
                            Toasty.error(EnterOTP.this, obj.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        confirm.stopAnimation();
                        confirm.revertAnimation();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    confirm.stopAnimation();
                    confirm.revertAnimation();
                    Log.e("re", "" + t.toString());
                }
            });

        } catch (Exception e) {
            confirm.stopAnimation();
            confirm.revertAnimation();
            e.printStackTrace();
        }


    }

    private void requestHint() {
        SmsRetrieverClient client = SmsRetriever.getClient(this /* context */);

        // Starts SmsRetriever, which waits for ONE matching SMS message until timeout
        // (5 minutes). The matching SMS message will be sent via a Broadcast Intent with
        // action SmsRetriever#SMS_RETRIEVED_ACTION.
        Task<Void> task = client.startSmsRetriever();

        // Listen for success/failure of the start Task. If in a background thread, this
        // can be made blocking using Tasks.await(task, [timeout]);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                // ...
                Log.e("start the = ", "retriever");
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...
                Log.e("fail the = ", "retriever");
            }
        });

    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                Log.e("final otp is = ", message);
                otp_view.setText(message);
                //otprogressbar.setVisibility(View.GONE);

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        confirm.dispose();
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}
