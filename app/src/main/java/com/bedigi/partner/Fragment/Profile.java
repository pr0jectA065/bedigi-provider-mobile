package com.bedigi.partner.Fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.TypedValue;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bedigi.partner.API.RetrofitAPI;
import com.bedigi.partner.Activities.AddService;
import com.bedigi.partner.Adapter.PackageAdapter;
import com.bedigi.partner.Model.Locality_Model;
import com.bedigi.partner.Model.PackageData;
import com.bedigi.partner.Model.StateCityListModel;
import com.bedigi.partner.Model.TimeSlotModel;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.Preferences.CustomRadioBt.PresetRadioGroup;
import com.bedigi.partner.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
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
    EditText et_dob, et_zipcode, et_expirience, et_address;
    Calendar c;
    int year, month, day;
    ImageButton uploadImage;
    String filename, picturePath, encodedImage;
    CircleImageView image;
    Spinner state, city, exp_type;
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
    CheckBox check_pcc, check_expirience;
    String is_pcc_check = "0", is_exp_check = "0";

    Button bt_profile, bt_visiting,bt_payment;
    ExpandableLayout exl_basic_info, exl_visiting,exl_payment;

    SwitchCompat is_available,all_days;
    LinearLayout timeLL;
    CheckBox early_morning, morning, afternoon, late_afternoon, evening;
    String is_available_str = "", early_morning_str = "", morning_str = "", afternoon_str = "", late_afternoon_str = "", evening_str = "";
    Button saveTimeSlot;

    PresetRadioGroup mSetDurationPresetRadioGroup;
    String daySel = "mon", timeslot_id;
    List<TimeSlotModel> timeList;
    JSONArray time_slot_arr;

    TextView spinnerTV, selected_localities, selected_localities_id;
    boolean[] bool_locality_checked;
    String[] str_locality;

    List<Locality_Model> list_localities;
    RelativeLayout rl_city, rl_locality;
    String[] temp;

    ChipGroup chipGroup;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();

        timeList = new ArrayList<>();

        appPreferences = new AppPreferences(context);
        signUp = (CircularProgressButton) view.findViewById(R.id.signUp);

        rl_city = view.findViewById(R.id.rl_city);
        rl_locality = view.findViewById(R.id.rl_locality);
        spinnerTV = view.findViewById(R.id.spinnerTV);
        selected_localities = view.findViewById(R.id.selected_localities);
        selected_localities_id = view.findViewById(R.id.selected_localities_id);
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
        exl_payment = view.findViewById(R.id.exl_payment);
        bt_profile = view.findViewById(R.id.bt_profile);
        bt_visiting = view.findViewById(R.id.bt_visiting);
        bt_payment = view.findViewById(R.id.bt_payment);

        first_name = (EditText) view.findViewById(R.id.first_name);
        email = (EditText) view.findViewById(R.id.email);
        et_dob = (EditText) view.findViewById(R.id.et_dob);
        et_zipcode = (EditText) view.findViewById(R.id.et_zipcode);
        et_expirience = (EditText) view.findViewById(R.id.et_expirience);
        et_address = (EditText) view.findViewById(R.id.et_address);

        segmented2 = (RadioGroup) view.findViewById(R.id.segmented2);

        check_pcc = view.findViewById(R.id.check_pcc);
        check_expirience = view.findViewById(R.id.check_expirience);

        all_days = view.findViewById(R.id.all_days);

        male = (RadioButton) view.findViewById(R.id.male);
        female = (RadioButton) view.findViewById(R.id.female);
        others = (RadioButton) view.findViewById(R.id.others);

        uploadImage = (ImageButton) view.findViewById(R.id.uploadImage);
        image = (CircleImageView) view.findViewById(R.id.image);

        main_layout = (LinearLayout) view.findViewById(R.id.main_layout);

        state = (Spinner) view.findViewById(R.id.state);
        city = (Spinner) view.findViewById(R.id.city);
        exp_type = (Spinner) view.findViewById(R.id.exp_type);

        list = new ArrayList<>();
        list1 = new ArrayList<>();
        list_localities = new ArrayList<>();

        chipGroup = view.findViewById(R.id.tag_group);

        check_pcc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    is_pcc_check = "1";
                } else {
                    is_pcc_check = "0";
                }
                check_pcc.setChecked(b);

            }
        });

        check_expirience.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    is_exp_check = "1";
                } else {
                    is_exp_check = "0";
                }
                check_expirience.setChecked(b);
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
                    rl_city.setVisibility(View.VISIBLE);
                    getCity(state_selected);
                } else {
                    rl_city.setVisibility(View.GONE);
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
                if (!(city_selected.matches("0"))) {
                    list_localities.clear();
                    rl_locality.setVisibility(View.VISIBLE);
                    getLocality(city_selected);
                } else {
                    rl_locality.setVisibility(View.GONE);
                }
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

        bt_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exl_payment.isExpanded()) {
                    exl_payment.collapse();
                    bt_payment.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0);
                } else {
                    exl_payment.expand();
                    bt_payment.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0);

                }
            }
        });

        is_available.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    timeLL.setVisibility(View.VISIBLE);
                    is_available_str = "1";
                } else {
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

        all_days.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    timeLL.setVisibility(View.VISIBLE);
                    is_available.setChecked(true);
                } else {

                }
            }
        });

        early_morning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    early_morning_str = "1";
                } else {
                    early_morning_str = "0";
                }
            }
        });

        morning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    morning_str = "1";
                } else {
                    morning_str = "0";
                }
            }
        });

        afternoon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    afternoon_str = "1";
                } else {
                    afternoon_str = "0";
                }
            }
        });

        late_afternoon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    late_afternoon_str = "1";
                } else {
                    late_afternoon_str = "0";
                }
            }
        });

        evening.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    evening_str = "1";
                } else {
                    evening_str = "0";
                }
            }
        });

        saveTimeSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveTimeSlot(daySel, timeslot_id);
            }
        });

        getState();
        getTimeSlot();

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp.startAnimation();
                saveInfo();
            }
        });

        email.setText(appPreferences.getEmail());

        mSetDurationPresetRadioGroup = (PresetRadioGroup) view.findViewById(R.id.preset_time_radio_group);
        mSetDurationPresetRadioGroup.setOnCheckedChangeListener(new PresetRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View radioGroup, View radioButton, boolean isChecked, int checkedId) {

                saveTimeSlot(daySel, timeslot_id);

                if (checkedId == R.id.mon) {
                    daySel = "mon";
                } else if (checkedId == R.id.tue) {
                    daySel = "tue";
                } else if (checkedId == R.id.wed) {
                    daySel = "wed";
                } else if (checkedId == R.id.thr) {
                    daySel = "thur";
                } else if (checkedId == R.id.fri) {
                    daySel = "fri";
                } else if (checkedId == R.id.sat) {
                    daySel = "sat";
                } else if (checkedId == R.id.sun) {
                    daySel = "sun";
                }

                if (time_slot_arr.length() > 0) {
                    for (int i = 0; i < time_slot_arr.length(); i++) {
                        try {
                            if (time_slot_arr.getJSONObject(i).getString("day").matches(daySel)) {

                                timeslot_id = time_slot_arr.getJSONObject(i).getString("timeslot_id");

                                if (time_slot_arr.getJSONObject(i).getString("is_available").matches("0")) {
                                    is_available.setChecked(false);
                                    is_available_str = "0";
                                } else {
                                    is_available.setChecked(true);
                                    is_available_str = "1";
                                }

                                if (is_available.isChecked()) {
                                    timeLL.setVisibility(View.VISIBLE);
                                    early_morning_str = time_slot_arr.getJSONObject(i).getString("early_morning");
                                    morning_str = time_slot_arr.getJSONObject(i).getString("morning");
                                    afternoon_str = time_slot_arr.getJSONObject(i).getString("afternoon");
                                    late_afternoon_str = time_slot_arr.getJSONObject(i).getString("late_afternoon");
                                    evening_str = time_slot_arr.getJSONObject(i).getString("evening");
                                } else {
                                    timeLL.setVisibility(View.GONE);
                                }

                                if (early_morning_str.matches("0")) {
                                    early_morning.setChecked(false);
                                } else {
                                    early_morning.setChecked(true);
                                }

                                if (morning_str.matches("0")) {
                                    morning.setChecked(false);
                                } else {
                                    morning.setChecked(true);
                                }

                                if (afternoon_str.matches("0")) {
                                    afternoon.setChecked(false);
                                } else {
                                    afternoon.setChecked(true);
                                }

                                if (late_afternoon_str.matches("0")) {
                                    late_afternoon.setChecked(false);
                                } else {
                                    late_afternoon.setChecked(true);
                                }

                                if (evening_str.matches("0")) {
                                    evening.setChecked(false);
                                } else {
                                    evening.setChecked(true);
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


            }
        });

        selected_localities.setVisibility(View.GONE);
        selected_localities_id.setVisibility(View.GONE);

        spinnerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (list_localities.size() > 0) {
                    rl_locality.setVisibility(View.VISIBLE);

                    temp = selected_localities_id.getText().toString().trim().split(",");

                    for(int i=0;i<list_localities.size();i++){
                        for(int j=0;j<temp.length;j++){
                            if(list_localities.get(i).Id.matches(temp[j])){
                                bool_locality_checked[i] = true;
                            }
                        }

                    }

                    Log.e("bool_locality_checked",bool_locality_checked.toString());

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    //final List<String> listt = new ArrayList<>();

                    for (int i = 0; i < list_localities.size(); i++) {
                        //bool_speciality_checked[i] = false;
                        str_locality[i] = list_localities.get(i).Name;
                    }

                    Log.e("bool_locality_checked",bool_locality_checked.toString());

                    builder.setMultiChoiceItems(str_locality, bool_locality_checked, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            bool_locality_checked[which] = isChecked;
                            String currentItem = list_localities.get(which).Id;
                            //Toast.makeText(context, currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setCancelable(true);
                    builder.setTitle("Select Locality");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selected_localities.setVisibility(View.VISIBLE);
                            selected_localities.setText("");
                            selected_localities_id.setText("");
                            for (int i = 0; i < bool_locality_checked.length; i++) {
                                boolean checked = bool_locality_checked[i];
                                if (checked) {
                                    selected_localities.setText(selected_localities.getText() +
                                            list_localities.get(i).Name + ",");
                                    selected_localities_id.setText(selected_localities_id.getText() +
                                            list_localities.get(i).Id + ",");
                                }
                            }
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();


                } else {
                    rl_locality.setVisibility(View.GONE);
                }
            }
        });

        List<String> exp_list = new ArrayList<>();
        exp_list.add("Years");
        exp_list.add("Months");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.spinner_item, R.id.item, exp_list);
        exp_type.setAdapter(adapter);


        List<String> spec_list = new ArrayList<>();
        spec_list.add("Electricity");
        spec_list.add("Plumber");
        spec_list.add("Carpenter");
        spec_list.add("Painter");

        for (int index = 0; index < spec_list.size(); index++) {
            final String tagName = spec_list.get(index);
            final Chip chip = new Chip(context);
            ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.Widget_MaterialComponents_Chip_Choice);
            chip.setChipDrawable(chipDrawable);
            int paddingDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics()
            );
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
            chip.setText(tagName);
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    Log.e("chekediDS", chipGroup.getCheckedChipIds().toString());
                    //[1, 2, 9, 13]
                }
            });
            /*chip.setCloseIconResource(R.drawable.ic_baseline_close_24);
            chip.setCloseIconEnabled(true);
            //Added click listener on close icon to remove tag from ChipGroup
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //tagList.remove(tagName);
                    chipGroup.removeView(chip);
                }
            });*/

            chipGroup.addView(chip);
        }

        return view;
    }

    private void getTimeSlot() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        //dialog.show();

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().addtimeslot(appPreferences.getId());
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        dialog.dismiss();
                        JSONObject obj = new JSONObject(response.body().toString());

                        time_slot_arr = obj.getJSONArray("data");

                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {

                            for (int i = 0; i < time_slot_arr.length(); i++) {

                                timeList.add(new TimeSlotModel(time_slot_arr.getJSONObject(i).getString("timeslot_id"),
                                        time_slot_arr.getJSONObject(i).getString("provider_id"),
                                        time_slot_arr.getJSONObject(i).getString("day"),
                                        time_slot_arr.getJSONObject(i).getString("is_available"),
                                        time_slot_arr.getJSONObject(i).getString("early_morning"),
                                        time_slot_arr.getJSONObject(i).getString("morning"),
                                        time_slot_arr.getJSONObject(i).getString("afternoon"),
                                        time_slot_arr.getJSONObject(i).getString("late_afternoon"),
                                        time_slot_arr.getJSONObject(i).getString("evening")));

                            }

                            timeslot_id = time_slot_arr.getJSONObject(0).getString("timeslot_id");

                            if (time_slot_arr.length() > 0) {
                                for (int i = 0; i < time_slot_arr.length(); i++) {
                                    try {
                                        if (time_slot_arr.getJSONObject(i).getString("day").matches(daySel)) {

                                            timeslot_id = time_slot_arr.getJSONObject(i).getString("timeslot_id");

                                            if (time_slot_arr.getJSONObject(i).getString("is_available").matches("0")) {
                                                is_available.setChecked(false);
                                                is_available_str = "0";
                                            } else {
                                                is_available.setChecked(true);
                                                is_available_str = "1";
                                            }

                                            if (is_available.isChecked()) {
                                                timeLL.setVisibility(View.VISIBLE);
                                            } else {
                                                timeLL.setVisibility(View.GONE);
                                                early_morning_str = time_slot_arr.getJSONObject(i).getString("early_morning");
                                                morning_str = time_slot_arr.getJSONObject(i).getString("morning");
                                                afternoon_str = time_slot_arr.getJSONObject(i).getString("afternoon");
                                                late_afternoon_str = time_slot_arr.getJSONObject(i).getString("late_afternoon");
                                                evening_str = time_slot_arr.getJSONObject(i).getString("evening");
                                            }

                                            if (early_morning_str.matches("0")) {
                                                early_morning.setChecked(false);
                                            } else {
                                                early_morning.setChecked(true);
                                            }

                                            if (morning_str.matches("0")) {
                                                morning.setChecked(false);
                                            } else {
                                                morning.setChecked(true);
                                            }

                                            if (afternoon_str.matches("0")) {
                                                afternoon.setChecked(false);
                                            } else {
                                                afternoon.setChecked(true);
                                            }

                                            if (late_afternoon_str.matches("0")) {
                                                late_afternoon.setChecked(false);
                                            } else {
                                                late_afternoon.setChecked(true);
                                            }

                                            if (evening_str.matches("0")) {
                                                evening.setChecked(false);
                                            } else {
                                                evening.setChecked(true);
                                            }


                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

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

    private void saveTimeSlot(String day, String timeslot_id) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        dialog.show();
        //signUp.startAnimation();

        try {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("timeslot_id", timeslot_id);
            jsonObject.addProperty("day", day);
            jsonObject.addProperty("is_available", is_available_str);
            jsonObject.addProperty("early_morning", early_morning_str);
            jsonObject.addProperty("morning", morning_str);
            jsonObject.addProperty("afternoon", afternoon_str);
            jsonObject.addProperty("late_afternoon", late_afternoon_str);
            jsonObject.addProperty("evening", evening_str);

            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().saveTimeSlotAPI(appPreferences.getId(), jsonObject);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {

                        //signUp.stopAnimation();
                        //signUp.revertAnimation();
                        dialog.dismiss();

                        JSONObject obj = new JSONObject(response.body().toString());
                        Log.e("API", response.body().toString());
                        if (obj.getString("status").matches("true")) {
                            //Toasty.success(context, obj.getString("message"), Toast.LENGTH_LONG).show();
                            getTimeSlot();
                        } else {
                            dialog.dismiss();
                            Toasty.error(context, obj.getString("message"),
                                    Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        dialog.dismiss();
                        //signUp.stopAnimation();
                        //signUp.revertAnimation();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    dialog.dismiss();
                    //signUp.stopAnimation();
                    //signUp.revertAnimation();
                    Log.e("re", "" + t.toString());
                }
            });

        } catch (Exception e) {
            dialog.dismiss();
            // signUp.stopAnimation();
            //signUp.revertAnimation();
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
            jsonObject.addProperty("locality_ids", selected_localities_id.getText().toString().trim().replaceAll(",$", ""));
            jsonObject.addProperty("is_PCC", is_pcc_check);
            jsonObject.addProperty("is_fulltime", is_exp_check);
            jsonObject.addProperty("zipcode", et_zipcode.getText().toString().trim());
            jsonObject.addProperty("experience_years", et_expirience.getText().toString().trim() + " "+ exp_type.getSelectedItem());

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
                            appPreferences.setName(first_name.getText().toString().trim());

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

    private void getLocality(String city_id) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_layout);
        dialog.setCancelable(false);
        AVLoadingIndicatorView progressView = (AVLoadingIndicatorView) dialog.findViewById(R.id.progressView);
        dialog.show();

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().getLocality(city_id);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        dialog.dismiss();
                        JSONObject obj = new JSONObject(response.body().toString());

                        JSONArray arr = obj.getJSONArray("data");
                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {

                            list_localities = new ArrayList<>();
                            //list_localities.add(new Locality_Model("0", "Select Locality", ""));
                            for (int i = 0; i < arr.length(); i++) {
                                list_localities.add(new Locality_Model(arr.getJSONObject(i).getString("locality_id"),
                                        arr.getJSONObject(i).getString("locality"),
                                        arr.getJSONObject(i).getString("status")));
                            }

                            bool_locality_checked = new boolean[list_localities.size()];
                            str_locality = new String[list_localities.size()];

                            if (list_localities.size() > 0) {
                                rl_locality.setVisibility(View.VISIBLE);
                            } else {
                                rl_locality.setVisibility(View.GONE);
                            }

                            /*if (!(city_selected.matches(""))) {
                                for (int j = 0; j < list1.size(); j++) {
                                    if (list1.get(j).Id.matches(city_selected)) {
                                        city.setSelection(j);
                                    }
                                }
                            }*/
                        } else {
                            rl_locality.setVisibility(View.GONE);
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

                            //str.split(" ", 1)[0]
                            et_expirience.setText(data.getString("experience_years").replaceAll(" .*", ""));

                            if (data.getString("is_fulltime").matches("1")) {
                                is_exp_check = "1";
                                check_expirience.setChecked(true);
                            } else {
                                is_exp_check = "0";
                                check_expirience.setChecked(false);
                            }

                            if (data.getString("is_PCC").matches("1")) {
                                is_pcc_check = "1";
                                check_pcc.setChecked(true);
                            } else {
                                is_pcc_check = "0";
                                check_pcc.setChecked(false);
                            }

                            JSONArray locality_arr = new JSONArray();
                            locality_arr = data.getJSONArray("locality");

                            if (locality_arr.length() > 0) {
                                String sel = "", sel_id = "";
                                selected_localities.setText("");
                                selected_localities.setVisibility(View.VISIBLE);
                                for (int i = 0; i < locality_arr.length(); i++) {
                                    sel += "," +
                                            locality_arr.getJSONObject(i).getString("locality");

                                    sel_id += "," +
                                            locality_arr.getJSONObject(i).getString("locality_id");
                                }
                                sel = sel.substring(1);
                                sel_id = sel_id.substring(1);

                                selected_localities.setText(sel);


                                for(int i = 0; i < list_localities.size(); ++i){
                                    bool_locality_checked[i] = false;
                                }

                                selected_localities_id.setText(sel_id);

                            } else {
                                selected_localities.setVisibility(View.GONE);
                            }

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
