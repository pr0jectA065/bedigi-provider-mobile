package com.bedigi.partner.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bedigi.partner.API.RetrofitAPI;
import com.bedigi.partner.Model.HomeModel;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;

import static com.bedigi.partner.Preferences.Utilities.getImageUrlWithAuthority;

public class AddService extends AppCompatActivity {

    Spinner service;
    List<HomeModel> list;
    String service_id = "";
    EditText price, sellprice, description, no_of_hours, service_name;
    TextView booking_amount;
    CircularProgressButton save;
    AppPreferences appPreferences;
    ImageView image;
    String filename, picturePath, encodedImage;

    String type = "", service_provider_id = "", booking_val = "", old_booking_amount = "";
    String booking_type = "";
    JSONObject obj;

    EditText start_date, end_date, booking_mat_value, booking_amt_in_percent;
    int mYear;
    int mMonth;
    int mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add Service");

        appPreferences = new AppPreferences(AddService.this);

        service = findViewById(R.id.service);
        price = findViewById(R.id.price);
        sellprice = findViewById(R.id.sellprice);
        description = findViewById(R.id.description);
        no_of_hours = findViewById(R.id.no_of_hours);
        service_name = findViewById(R.id.service_name);
        image = findViewById(R.id.image);
        start_date = findViewById(R.id.start_date);
        end_date = findViewById(R.id.end_date);
        booking_mat_value = findViewById(R.id.booking_mat_value);
        booking_amt_in_percent = findViewById(R.id.booking_amt_in_percent);
        booking_amount = findViewById(R.id.booking_amount);

        save = findViewById(R.id.save);

        booking_mat_value.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    booking_amt_in_percent.setText("");
                    booking_val = editable.toString();
                    booking_type = "amt";
                } else {
                    booking_val = "";
                    booking_type = "";
                }
            }
        });

        booking_amt_in_percent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    booking_mat_value.setText("");
                    booking_val = editable.toString();
                    booking_type = "percent";
                } else {
                    booking_val = "";
                    booking_type = "";
                }
            }
        });

        service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                service_id = list.get(service.getSelectedItemPosition()).id;
                Log.e("service_id", service_id);
                //service_name = list.get(service.getSelectedItemPosition()).name;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.startAnimation();
                if (!price.getText().toString().matches("")) {
                    if (booking_type.matches("amt")) {

                        if (type.matches("update")) {
                            update_service();
                        } else {
                            add_service();
                        }

                    } else if (booking_type.matches("percent")) {

                        Double val = (Double.parseDouble(booking_val) / 100) * Double.parseDouble(price.getText().toString().trim());
                        booking_val = String.valueOf(val);

                        if (type.matches("update")) {
                            update_service();
                        } else {
                            add_service();
                        }

                    } else {
                        save.stopAnimation();
                        save.revertAnimation();
                        Toasty.error(AddService.this, "Booking amount is required!", Toast.LENGTH_LONG).show();
                    }

                    Log.e("booking_val",booking_val);

                } else {
                    save.stopAnimation();
                    save.revertAnimation();
                    Toasty.error(AddService.this, "Service price is required!", Toast.LENGTH_LONG).show();
                }

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!(ActivityCompat.checkSelfPermission(AddService.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED)) {

                        ActivityCompat.requestPermissions(AddService.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);

                    } else {
                        uploadProfilepic();
                    }
                } else {
                    uploadProfilepic();
                }

            }
        });

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddService.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                String monthConverted = "" + (monthOfYear + 1);
                                if (Integer.parseInt(monthConverted) < 10) {
                                    monthConverted = "0" + monthConverted;
                                }

                                String dayConverted = "" + (dayOfMonth);
                                if (dayOfMonth < 10) {
                                    dayConverted = "0" + dayConverted;
                                }

                                String Seldate = year + "-" + monthConverted + "-" + dayConverted;
                                start_date.setText(Seldate);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddService.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                String monthConverted = "" + (monthOfYear + 1);
                                if (Integer.parseInt(monthConverted) < 10) {
                                    monthConverted = "0" + monthConverted;
                                }

                                String dayConverted = "" + (dayOfMonth);
                                if (dayOfMonth < 10) {
                                    dayConverted = "0" + dayConverted;
                                }

                                String Seldate = year + "-" + monthConverted + "-" + dayConverted;
                                end_date.setText(Seldate);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();

            }
        });

        type = getIntent().getStringExtra("type");

        gethomeslider();
    }

    private void update_service() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("service_provider_id", service_provider_id);
        jsonObject.addProperty("service_id", service_id);
        jsonObject.addProperty("service_name", service_name.getText().toString().trim());
        jsonObject.addProperty("price", price.getText().toString().trim());
        jsonObject.addProperty("sellprice", sellprice.getText().toString().trim());
        jsonObject.addProperty("description", description.getText().toString().trim());
        jsonObject.addProperty("no_of_hours", no_of_hours.getText().toString().trim());
        jsonObject.addProperty("start_date", start_date.getText().toString().trim());
        jsonObject.addProperty("end_date", end_date.getText().toString().trim());
        jsonObject.addProperty("service_image", encodedImage);
        jsonObject.addProperty("status", "1");
        jsonObject.addProperty("booking_amount", booking_val);

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().update_service(appPreferences.getId(), jsonObject);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        save.stopAnimation();
                        save.revertAnimation();
                        JSONObject obj = new JSONObject(response.body().toString());
                        //JSONArray arr = obj.getJSONArray("data");

                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {
                            finish();

                        } else {
                            Toasty.error(AddService.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        save.stopAnimation();
                        save.revertAnimation();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("re", "" + t.toString());
                    save.stopAnimation();
                    save.revertAnimation();
                }
            });

        } catch (Exception e) {
            save.stopAnimation();
            save.revertAnimation();
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

                Cursor cursor = getContentResolver().query(selectedImage,
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
                //saveImage(encodedImage);

            }

        } catch (Exception e) {
            e.printStackTrace();
            try {

                selectedImage = Uri.parse(getImageUrlWithAuthority(AddService.this, selectedImage));

                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
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
                //saveImage(encodedImage);

            } catch (Exception e1) {
                e1.printStackTrace();
                Toasty.error(AddService.this, "Some error occured!", Toast.LENGTH_LONG).show();
            }


        }


    }

    private void add_service() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("service_id", service_id);
        jsonObject.addProperty("service_name", service_name.getText().toString().trim());
        jsonObject.addProperty("price", price.getText().toString().trim());
        jsonObject.addProperty("sellprice", sellprice.getText().toString().trim());
        jsonObject.addProperty("description", description.getText().toString().trim());
        jsonObject.addProperty("no_of_hours", no_of_hours.getText().toString().trim());
        jsonObject.addProperty("start_date", start_date.getText().toString().trim());
        jsonObject.addProperty("end_date", end_date.getText().toString().trim());
        jsonObject.addProperty("service_image", encodedImage);
        jsonObject.addProperty("status", "1");
        jsonObject.addProperty("booking_amount", booking_val);

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().add_service(appPreferences.getId(), jsonObject);
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        save.stopAnimation();
                        save.revertAnimation();
                        JSONObject obj = new JSONObject(response.body().toString());
                        //JSONArray arr = obj.getJSONArray("data");

                        Log.e("API", response.body().toString());

                        if (obj.getString("status").matches("true")) {

                            finish();

                        } else {
                            Toasty.error(AddService.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        save.stopAnimation();
                        save.revertAnimation();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Log.e("re", "" + t.toString());
                    save.stopAnimation();
                    save.revertAnimation();
                }
            });

        } catch (Exception e) {
            save.stopAnimation();
            save.revertAnimation();
            e.printStackTrace();
        }

    }

    private void gethomeslider() {
        try {

            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().serviceList();
            d.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    try {
                        JSONObject obj = new JSONObject(response.body().toString());
                        JSONArray arr1 = obj.getJSONArray("data");

                        list = new ArrayList<>();

                        list.add(new HomeModel("0", "0", "Select Service", "", ""));

                        for (int i = 0; i < arr1.length(); i++) {

                            list.add(new HomeModel(arr1.getJSONObject(i).getString("id"),
                                    arr1.getJSONObject(i).getString("parent_id"),
                                    arr1.getJSONObject(i).getString("name"),
                                    arr1.getJSONObject(i).getString("image"),
                                    arr1.getJSONObject(i).getString("description")));

                        }

                        ArrayAdapter<HomeModel> adapter = new ArrayAdapter<HomeModel>(AddService.this,
                                R.layout.spinner_item, R.id.item, list);
                        service.setAdapter(adapter);

                        if (type.matches("update")) {

                            try {

                                obj = new JSONObject(getIntent().getStringExtra("json"));
                                Log.e("json", obj.toString());

                                price.setText(obj.getString("price"));
                                sellprice.setText(obj.getString("sellprice"));
                                description.setText(obj.getString("description"));
                                no_of_hours.setText(obj.getString("no_of_hours"));
                                service_name.setText(obj.getString("service_name"));

                                service_provider_id = obj.getString("service_provider_id");

                                service_id = obj.getString("service_id");

                                if (!(service_id.matches("0"))) {
                                    for (int j = 0; j < list.size(); j++) {
                                        if (list.get(j).id.matches(service_id)) {
                                            service.setSelection(j);
                                        }
                                    }
                                }

                                if (!(obj.getString("image").matches(""))) {
                                    Picasso.with(AddService.this).load(obj.getString("image")).placeholder(R.drawable.package_name).into(image);
                                }

                                start_date.setText(obj.getString("start_date"));
                                end_date.setText(obj.getString("end_date"));
                                booking_amount.setText("Booking amount: \u20B9" + obj.getString("booking_amount"));
                                old_booking_amount = obj.getString("booking_amount");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

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
    protected void onDestroy() {
        super.onDestroy();
        save.dispose();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
