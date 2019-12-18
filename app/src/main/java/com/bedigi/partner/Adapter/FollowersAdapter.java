package com.bedigi.partner.Adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bedigi.partner.Model.ChatModel;
import com.bedigi.partner.Model.FollowersModel;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.MyViewHolder> {

    List<FollowersModel> verticalList = Collections.emptyList();
    Context context;

    public FollowersAdapter(List<FollowersModel> horizontalList, Context context) {
        this.verticalList = horizontalList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.name);
            imageView = (ImageView) view.findViewById(R.id.image);

        }
    }

    @Override
    public FollowersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_item, parent, false);

        return new FollowersAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FollowersAdapter.MyViewHolder holder, final int position) {

        holder.name.setText(verticalList.get(position).customer_name);

        if(!(verticalList.get(position).user_pic.matches(""))){
            Picasso.with(context).load(verticalList.get(position).user_pic).into(holder.imageView);
        }


    }

    @Override
    public int getItemCount() {
        return verticalList.size();
    }
}