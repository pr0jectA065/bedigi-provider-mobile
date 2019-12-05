package com.bedigi.partner.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bedigi.partner.API.RetrofitAPI;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;

public class Login extends AppCompatActivity {

    CircularProgressButton login;
    ImageButton back;
    AppPreferences appPreferences;
    String android_id="";
    EditText username_et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        appPreferences=new AppPreferences(Login.this);
        login=(CircularProgressButton)findViewById(R.id.login);
        username_et=(EditText)findViewById(R.id.username_et) ;

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        username_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length() == 10){
                    login.startAnimation();
                    //login();
                    otp();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(username_et.getText().toString().length() == 10){
                    login.startAnimation();
                    //login();
                    otp();
                }
            }
        });

    }

    private void otp() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mobile_number", username_et.getText().toString().trim());

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().process_otp(jsonObject);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        login.stopAnimation();
                        login.revertAnimation();
                        JSONObject obj = new JSONObject(response.body().toString());
                        //JSONArray arr = obj.getJSONArray("data");

                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {

                            Intent mainIntent = new Intent(Login.this, EnterOTP.class);
                            Toasty.info(Login.this,obj.getJSONObject("data").getString("otp"), Toast.LENGTH_LONG).show();
                            mainIntent.putExtra("otp",obj.getJSONObject("data").getString("otp"));
                            mainIntent.putExtra("phone",username_et.getText().toString().trim());
                            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            finish();

                        } else {
                            Toasty.error(Login.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        login.stopAnimation();
                        login.revertAnimation();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("re", "" + t.toString());
                    login.stopAnimation();
                    login.revertAnimation();
                }
            });

        } catch (Exception e) {
            login.stopAnimation();
            login.revertAnimation();
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        login.dispose();
    }

    @Override
    public void onBackPressed() {
        finish();
    }


}