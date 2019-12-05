package com.bedigi.partner.Fragment;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.bedigi.partner.API.RetrofitAPI;
import com.bedigi.partner.Model.StateCityListModel;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;
import static com.bedigi.partner.Preferences.Utilities.getImageUrlWithAuthority;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment {

    Context context;
    EditText et_dob, et_zipcode,et_expirience;
    Calendar c;
    int year, month, day;
    ImageButton uploadImage;
    String filename, picturePath, encodedImage;
    CircleImageView image;
    Spinner state, city;
    Dialog dialog;
    CircularProgressButton signUp;
    List<StateCityListModel> list, list1;
    String state_selected = "";
    String city_selected = "";
    private boolean isFragmentLoaded = false;
    AppPreferences appPreferences;

    LinearLayout main_layout;

    EditText first_name, email;
    RadioButton male, female, others;
    RadioGroup segmented2;
    String sex_sel = "";

    ArrayAdapter<StateCityListModel> stateadapter;
    CheckBox check_pcc,check_expirience;
    String is_pcc_check="0",is_exp_check="0";

    Button bt_profile,bt_visiting;
    ExpandableLayout exl_basic_info,exl_visiting;

    SwitchCompat is_available;
    LinearLayout timeLL;
    CheckBox early_morning,morning,afternoon,late_afternoon,evening;
    String is_available_str ="",early_morning_str="",morning_str="",afternoon_str="",late_afternoon_str="",evening_str="";
    Button saveTimeSlot;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();

        appPreferences = new AppPreferences(context);
        signUp = (CircularProgressButton) view.findViewById(R.id.signUp);

        saveTimeSlot = view.findViewById(R.id.saveTimeSlot);
        is_available = view.findViewById(R.id.is_available);
        timeLL = view.findViewById(R.id.timeLL);
        early_morning = view.findViewById(R.id.early_morning);
        morning = view.findViewById(R.id.morning);
        afternoon = view.findViewById(R.id.afternoon);
        late_afternoon = view.findViewById(R.id.late_afternoon);
        evening = view.findViewById(R.id.evening);

        exl_basic_info = view.findViewById(R.id.exl_basic_info);
        exl_visiting = view.findViewById(R.id.exl_visiting);
        bt_profile = view.findViewById(R.id.bt_profile);
        bt_visiting = view.findViewById(R.id.bt_visiting);

        first_name = (EditText) view.findViewById(R.id.first_name);
        email = (EditText) view.findViewById(R.id.email);
        et_dob = (EditText) view.findViewById(R.id.et_dob);
        et_zipcode = (EditText) view.findViewById(R.id.et_zipcode);
        et_expirience = (EditText) view.findViewById(R.id.et_expirience);

        segmented2 = (RadioGroup) view.findViewById(R.id.segmented2);

        check_pcc = view.findViewById(R.id.check_pcc);
        check_expirience = view.findViewById(R.id.check_expirience);

        male = (RadioButton) view.findViewById(R.id.male);
        female = (RadioButton) view.findViewById(R.id.female);
        others = (RadioButton) view.findViewById(R.id.others);

        uploadImage = (ImageButton) view.findViewById(R.id.uploadImage);
        image = (CircleImageView) view.findViewById(R.id.image);

        main_layout = (LinearLayout) view.findViewById(R.id.main_layout);

        state = (Spinner) view.findViewById(R.id.state);
        city = (Spinner) view.findViewById(R.id.city);
        list = new ArrayList<>();
        list1 = new ArrayList<>();

        check_pcc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    is_pcc_check = "1";
                } else{
                    is_pcc_check = "0";
                }

            }
        });

        check_expirience.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    is_exp_check = "1";
                } else{
                    is_exp_check = "0";
                }
            }
        });

        segmented2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) view.findViewById(i);
                if (rb.getId() == R.id.male) {
                    male.setChecked(true);
                    sex_sel = "m";
                } else if (rb.getId() == R.id.female) {
                    female.setChecked(true);
                    sex_sel = "f";
                } else if (rb.getId() == R.id.others) {
                    others.setChecked(true);
                    sex_sel = "o";
                }
            }
        });

        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //state_selected = String.valueOf((StateCityListModel) arg0.getAdapter().getItem(arg2));
                state_selected = list.get(state.getSelectedItemPosition()).Id;
                Log.e("state_selected", state_selected);
                if (!(state_selected.matches("0"))) {
                    list1.clear();
                    city.setVisibility(View.VISIBLE);
                    getCity(state_selected);
                } else {
                    city.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //city_selected = String.valueOf((StateCityListModel) arg0.getAdapter().getItem(arg2));
                city_selected = list1.get(city.getSelectedItemPosition()).Id;
                Log.e("city_selected", city_selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        et_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dpd = new DatePickerDialog(context, dob, year, month, day);
                dpd.getDatePicker().setMaxDate(c.getTimeInMillis());
                dpd.setTitle("Select DOB");
                dpd.show();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!(ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED)) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

                    } else {
                        uploadProfilepic();
                    }
                } else {
                    uploadProfilepic();
                }

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!(ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED)) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

                    } else {
                        uploadProfilepic();
                    }
                } else {
                    uploadProfilepic();
                }
            }
        });

        bt_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exl_basic_info.isExpanded()) {
                    exl_basic_info.collapse();
                    bt_profile.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                } else {
                    exl_basic_info.expand();
                    bt_profile.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);

                }
            }
        });

        bt_visiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exl_visiting.isExpanded()) {
                    exl_visiting.collapse();
                    bt_visiting.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                } else {
                    exl_visiting.expand();
                    bt_visiting.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);

                }
            }
        });

        is_available.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    timeLL.setVisibility(View.VISIBLE);
                    is_available_str = "1";
                }else{
                    timeLL.setVisibility(View.GONE);
                    //is_available_str ="",early_morning_str="",morning_str="",late_afternoon_str="",evening_str=""
                    is_available_str = "0";
                    early_morning_str = "0";
                    morning_str = "0";
                    afternoon_str = "0";
                    late_afternoon_str = "0";
                    evening_str = "0";
                }
            }
        });

        early_morning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    early_morning_str="1";
                }else{
                    early_morning_str="0";
                }
            }
        });

        morning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    morning_str="1";
                }else{
                    morning_str="0";
                }
            }
        });

        afternoon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    afternoon_str="1";
                }else{
                    afternoon_str="0";
                }
            }
        });

        late_afternoon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    late_afternoon_str="1";
                }else{
                    late_afternoon_str="0";
                }
            }
        });

        evening.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    evening_str="1";
                }else{
                    evening_str="0";
                }
            }
        });

        saveTimeSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimeSlot();
            }
        });

        getState();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp.startAnimation();
                saveInfo();
            }
        });

        email.setText(appPreferences.getEmail());

        return view;
    }

    private void saveTimeSlot() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        dialog.show();
        signUp.startAnimation();

        try {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("day", day);
            jsonObject.addProperty("is_available", is_available_str);
            jsonObject.addProperty("early_morning",early_morning_str);
            jsonObject.addProperty("morning", morning_str);
            jsonObject.addProperty("afternoon", afternoon_str);
            jsonObject.addProperty("late_afternoon", late_afternoon_str);
            jsonObject.addProperty("evening", evening_str);

            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().saveTimeSlotAPI(appPreferences.getId(), jsonObject);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {

                        signUp.stopAnimation();
                        signUp.revertAnimation();
                        dialog.dismiss();

                        JSONObject obj = new JSONObject(response.body().toString());
                        Log.e("API", response.body().toString());
                        if (obj.getString("status").matches("true")) {
                            Toasty.success(context, obj.getString("message"), Toast.LENGTH_LONG).show();

                        } else {
                            dialog.dismiss();
                            Toasty.error(context, obj.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        dialog.dismiss();
                        signUp.stopAnimation();
                        signUp.revertAnimation();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dialog.dismiss();
                    signUp.stopAnimation();
                    signUp.revertAnimation();
                    Log.e("re", "" + t.toString());
                }
            });

        } catch (Exception e) {
            dialog.dismiss();
            signUp.stopAnimation();
            signUp.revertAnimation();
            e.printStackTrace();
        }
    }

    private void saveInfo() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        dialog.show();
        signUp.startAnimation();

        try {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", appPreferences.getId());
            jsonObject.addProperty("first_name", first_name.getText().toString().trim());
            jsonObject.addProperty("email", email.getText().toString().trim());
            jsonObject.addProperty("gender", sex_sel);
            jsonObject.addProperty("dob", et_dob.getText().toString().trim());
            jsonObject.addProperty("city_id", city_selected);
            jsonObject.addProperty("state_id", state_selected);
            jsonObject.addProperty("zipcode", et_zipcode.getText().toString().trim());
            jsonObject.addProperty("experience_years", et_expirience.getText().toString().trim());

            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().updateprofile(appPreferences.getId(), jsonObject);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {

                        signUp.stopAnimation();
                        signUp.revertAnimation();
                        dialog.dismiss();

                        JSONObject obj = new JSONObject(response.body().toString());
                        Log.e("API", response.body().toString());
                        if (obj.getString("status").matches("true")) {
                            Toasty.success(context, obj.getString("message"), Toast.LENGTH_LONG).show();

                        } else {
                            dialog.dismiss();
                            Toasty.error(context, obj.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        dialog.dismiss();
                        signUp.stopAnimation();
                        signUp.revertAnimation();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dialog.dismiss();
                    signUp.stopAnimation();
                    signUp.revertAnimation();
                    Log.e("re", "" + t.toString());
                }
            });

        } catch (Exception e) {
            dialog.dismiss();
            signUp.stopAnimation();
            signUp.revertAnimation();
            e.printStackTrace();
        }
    }

    private void getCity(String state_selected) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        dialog.show();

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().getCity(state_selected);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        dialog.dismiss();
                        JSONObject obj = new JSONObject(response.body().toString());

                        JSONArray arr = obj.getJSONArray("data");
                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {

                            list1 = new ArrayList<>();
                            list1.add(new StateCityListModel("0", "Select City", ""));
                            for (int i = 0; i < arr.length(); i++) {
                                list1.add(new StateCityListModel(arr.getJSONObject(i).getString("city_id"),
                                        arr.getJSONObject(i).getString("name"),
                                        arr.getJSONObject(i).getString("status")));
                            }

                            ArrayAdapter<StateCityListModel> adapter = new ArrayAdapter<StateCityListModel>(context,
                                    R.layout.spinner_item, R.id.item, list1);
                            city.setAdapter(adapter);

                            if (!(city_selected.matches(""))) {
                                for (int j = 0; j < list1.size(); j++) {
                                    if (list1.get(j).Id.matches(city_selected)) {
                                        city.setSelection(j);
                                    }
                                }
                            }
                        } else {
                            dialog.dismiss();
                            Toasty.error(context, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
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

    private void getState() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        //dialog.show();

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().getState();
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        //dialog.dismiss();
                        JSONObject obj = new JSONObject(response.body().toString());

                        JSONArray arr = obj.getJSONArray("data");
                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {

                            list = new ArrayList<>();
                            list.add(new StateCityListModel("0", "Select State", ""));
                            for (int i = 0; i < arr.length(); i++) {
                                list.add(new StateCityListModel(arr.getJSONObject(i).getString("state_id"),
                                        arr.getJSONObject(i).getString("name"),
                                        arr.getJSONObject(i).getString("status")));
                            }

                            stateadapter = new ArrayAdapter<StateCityListModel>(context,
                                    R.layout.spinner_item, R.id.item, list);
                            state.setAdapter(stateadapter);

                            getProfile();

                        } else {
                            Toasty.error(context, obj.getString("message"), Toast.LENGTH_LONG).show();
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

    private void getProfile() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        //dialog.show();

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().Profile(appPreferences.getId());
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        dialog.dismiss();
                        JSONObject obj = new JSONObject(response.body().toString());

                        //JSONArray arr = obj.getJSONArray("data");

                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {

                            JSONObject data = obj.getJSONObject("data");

                            if (!(data.getString("user_pic").matches(""))) {
                                Picasso.with(context).load(data.getString("user_pic")).placeholder(R.drawable.user_image).into(image);
                                appPreferences.setImage(data.getString("user_pic"));
                            } else {
                                image.setImageResource(R.drawable.user_image);
                            }

                            first_name.setText(data.getString("First_Name"));
                            et_dob.setText(data.getString("dob"));

                            if (data.getString("Gender").matches("m")) {
                                male.setChecked(true);
                            } else if (data.getString("Gender").matches("f")) {
                                female.setChecked(true);
                            } else if (data.getString("Gender").matches("o")) {
                                others.setChecked(true);
                            }

                            //et_age.setText(data.getString("Age"));

                            city_selected = data.getString("city_id");

                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).Id.matches(data.getString("state_id"))) {
                                    state.setSelection(i);
                                }
                            }

                            et_zipcode.setText(data.getString("zipcode"));

                            email.setText(data.getString("Email"));
                            et_expirience.setText(data.getString("experience_years"));

                            /*for (int j = 0; j < list1.size(); j++) {
                                if (list1.get(j).Id.matches(data.getString("City_Id"))) {
                                    city.setSelection(j);
                                }
                            }*/
                            dialog.dismiss();

                        } else {
                            dialog.dismiss();
                            Toasty.error(context, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("re", "" + t.toString());
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
            dialog.dismiss();
            e.printStackTrace();
        }
    }

    private void uploadProfilepic() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri selectedImage = null;
        try {

            if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
                selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);

                Bitmap bm = BitmapFactory.decodeFile(picturePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();

                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                //editHmDestImgInt = 1;
                cursor.close();

                int size = 10;
                Bitmap bitmapOriginal = BitmapFactory.decodeFile(picturePath);
                Bitmap bitmapsimplesize = Bitmap.createScaledBitmap(bitmapOriginal, bitmapOriginal.getWidth() / size, bitmapOriginal.getHeight() / size, true);
                bitmapOriginal.recycle();
                image.setImageBitmap(bitmapsimplesize);
                saveImage(encodedImage);

            }

        } catch (Exception e) {
            e.printStackTrace();
            try {

                selectedImage = Uri.parse(getImageUrlWithAuthority(context, selectedImage));

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);

                Bitmap bm = BitmapFactory.decodeFile(picturePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();

                encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                //editHmDestImgInt = 1;
                cursor.close();

                int size = 10;
                Bitmap bitmapOriginal = BitmapFactory.decodeFile(picturePath);
                Bitmap bitmapsimplesize = Bitmap.createScaledBitmap(bitmapOriginal, bitmapOriginal.getWidth() / size, bitmapOriginal.getHeight() / size, true);
                bitmapOriginal.recycle();
                image.setImageBitmap(bitmapsimplesize);
                saveImage(encodedImage);

            } catch (Exception e1) {
                e1.printStackTrace();
                Toasty.error(context, "Some error occured!", Toast.LENGTH_LONG).show();
            }


        }

    }

    private void saveImage(String encodedImage) {
        try {

            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_layout);
            dialog.setCancelable(false);
            AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
            dialog.show();

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("user_id", appPreferences.getId());
            jsonObject.addProperty("profile_image", encodedImage);

            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().updateimage(appPreferences.getId(), jsonObject);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        dialog.dismiss();

                        JSONObject obj = new JSONObject(response.body().toString());
                        Log.e("API", response.body().toString());
                        if (obj.getString("status").matches("true")) {
                            Toasty.success(context, obj.getString("message"), Toast.LENGTH_LONG).show();

                        } else {
                            Toasty.error(context, obj.getString("message"),
                                    Toast.LENGTH_LONG).show();
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
            e.printStackTrace();
        }

    }

    private DatePickerDialog.OnDateSetListener dob = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            year = arg1;
            month = arg2 + 1;
            day = arg3;

            String date = year + "-" + month + "-" + day;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            try {
                Date d = dateFormat.parse(date);
                System.out.println("DATE" + d);
                System.out.println("Formated" + dateFormat.format(d));
                //dob_selected = dateFormat.format(d);
                et_dob.setText(dateFormat.format(d));
                //et_age.setText(getAge(year, month, day));
            } catch (Exception e) {
                //java.text.ParseException: Unparseable date: Geting error
                System.out.println("Excep" + e);
            }
            //start = day + "/" + month + "/" + year;
        }
    };

}
