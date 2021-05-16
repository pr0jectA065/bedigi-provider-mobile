package com.bedigi.partner.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bedigi.partner.Adapter.ImageAdapter;
import com.bedigi.partner.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ServiceGallery extends AppCompatActivity {

    List<String> imagesEncodedList;
    GridView gridview;
    ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Service Gallery");

        gridview = (GridView) findViewById(R.id.gridview);
        imagesEncodedList = new ArrayList<>();
        adapter = new ImageAdapter(this, imagesEncodedList);
        gridview.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_image_from_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_images:
                openGallery();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == RESULT_OK) {
                if (requestCode == 1) {
                    Log.e("++data", "" + data.getClipData().getItemCount());

                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        imagesEncodedList.add(String.valueOf(data.getClipData().getItemAt(i).getUri()));
                        Log.e("image", String.valueOf(data.getClipData().getItemAt(i).getUri()));
                    }
                    Log.e("SIZE", imagesEncodedList.size() + "");
                    adapter.notifyDataSetChanged();
                }
            }
        }

    }
}