package com.bedigi.partner.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bedigi.partner.Model.PackageData;
import com.bedigi.partner.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.MyViewHolder> {

    List<PackageData> verticalList = Collections.emptyList();
    Context context;
    EventListener listener;

    public PackageAdapter(List<PackageData> horizontalList, Context context, EventListener listener) {
        this.verticalList = horizontalList;
        this.context = context;
        this.listener = listener;
    }

    public void filterList(ArrayList<PackageData> filterdNames) {
        verticalList = filterdNames;
        notifyDataSetChanged();
    }

    public interface EventListener {
        void onEvent(String type, int id);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView package_image;
        CardView card_view;
        TextView heading, location, description, likes, actual_price, discount, discount_price;

        public MyViewHolder(View root) {
            super(root);

            heading = (TextView) root.findViewById(R.id.heading);
            location = (TextView) root.findViewById(R.id.location);
            description = (TextView) root.findViewById(R.id.description);
            actual_price = (TextView) root.findViewById(R.id.actual_price);
            discount = (TextView) root.findViewById(R.id.discount);
            discount_price = (TextView) root.findViewById(R.id.discount_price);

            package_image=(ImageView)root.findViewById(R.id.package_image);

            card_view=(CardView)root.findViewById(R.id.card_view);

        }
    }

    @Override
    public PackageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_item, parent, false);

        return new PackageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PackageAdapter.MyViewHolder holder, final int position) {

        if(verticalList.get(position).no_of_hours.matches("")){
            holder.heading.setText(verticalList.get(position).service_name);
        } else {
            holder.heading.setText(verticalList.get(position).service_name + " ( " + verticalList.get(position).no_of_hours + " hours ) ");
        }

        //holder.location.setText(verticalList.get(position).Package_Location);
        holder.description.setText(Html.fromHtml(verticalList.get(position).description));
        holder.actual_price.setText("\u20B9" + verticalList.get(position).price);
        holder.discount.setText(String.format("%.0f", Float.parseFloat(verticalList.get(position).discount)) + "% Discount");
        holder.discount_price.setText("\u20B9" + verticalList.get(position).sellprice);

        holder.actual_price.setPaintFlags(holder.actual_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if(!(verticalList.get(position).image).matches("")){
            Picasso.with(context).load(verticalList.get(position).image).
                    placeholder(R.drawable.package_name).into(holder.package_image);
        }

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEvent("1", position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return verticalList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
