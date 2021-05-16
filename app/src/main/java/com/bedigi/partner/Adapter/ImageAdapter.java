package com.bedigi.partner.Adapter;

import android.content.Context;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bedigi.partner.R;

import java.io.IOException;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    List<String> imagesEncodedList;

    public ImageAdapter(Context c, List<String> imagesEncodedList) {
        mContext = c;
        this.imagesEncodedList = imagesEncodedList;
    }

    public int getCount() {
        return imagesEncodedList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        LayoutInflater inflater;

        if (convertView == null) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_item, null);

        }

        imageView = convertView.findViewById(R.id.imageView);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(imagesEncodedList.get(position)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(bitmap);

        return convertView;
    }
}