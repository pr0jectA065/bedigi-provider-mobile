package com.bedigi.partner.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.ArrayList;
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
    EditText price,sellprice,description,no_of_hours,service_name;
    CircularProgressButton save;
    AppPreferences appPreferences;
    ImageButton image;
    String filename, picturePath, encodedImage;

    String type = "",service_provider_id="";
    JSONObject obj;

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

        save = findViewById(R.id.save);

        service.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                service_id = list.get(service.getSelectedItemPosition()).id;
                Log.e("service_id",service_id);
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
                if(type.matches("update")){
                    update_service();
                }else{
                    add_service();
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

        type = getIntent().getStringExtra("type");

        gethomeslider();
    }

    private void update_service() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("service_provider_id", service_provider_id);
        jsonObject.addProperty("service_id", service_id);
        jsonObject.addProperty("service_name",service_name.getText().toString().trim());
        jsonObject.addProperty("price", price.getText().toString().trim());
        jsonObject.addProperty("sellprice", sellprice.getText().toString().trim());
        jsonObject.addProperty("description", description.getText().toString().trim());
        jsonObject.addProperty("no_of_hours", no_of_hours.getText().toString().trim());
        jsonObject.addProperty("image", encodedImage);
        jsonObject.addProperty("status", "1");

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().update_service(appPreferences.getId(),jsonObject);
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
            try{

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

            }catch (Exception e1){
                e1.printStackTrace();
                Toasty.error(AddService.this,"Some error occured!",Toast.LENGTH_LONG).show();
            }


        }


    }

    private void add_service() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("service_id", service_id);
        jsonObject.addProperty("service_name",service_name.getText().toString().trim());
        jsonObject.addProperty("price", price.getText().toString().trim());
        jsonObject.addProperty("sellprice", sellprice.getText().toString().trim());
        jsonObject.addProperty("description", description.getText().toString().trim());
        jsonObject.addProperty("no_of_hours", no_of_hours.getText().toString().trim());
        jsonObject.addProperty("image", encodedImage);
        jsonObject.addProperty("status", "1");

        try {
            Call<JsonObject> d = RetrofitAPI.getInstance().getApi().add_service(appPreferences.getId(),jsonObject);
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

                        list.add(new HomeModel("0","0","Select Service","",""));

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

                        if(type.matches("update")){

                            try{

                                obj = new JSONObject(getIntent().getStringExtra("json"));
                                Log.e("json",obj.toString());

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

                                if(!(obj.getString("image").matches(""))){
                                    Picasso.with(AddService.this).load(obj.getString("image")).placeholder(R.drawable.package_name).into(image);
                                }

                            }catch (Exception e){
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
