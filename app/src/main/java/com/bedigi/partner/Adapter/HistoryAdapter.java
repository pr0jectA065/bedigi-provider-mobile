package com.bedigi.partner.Adapter;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bedigi.partner.Model.HistoryModel;
import com.bedigi.partner.R;

import java.util.Collections;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    List<HistoryModel> verticalList = Collections.emptyList();
    Context context;
    EventListener listener;
    String type;

    public void filterList(List<HistoryModel> filteredlist) {
        verticalList = filteredlist;
        notifyDataSetChanged();
    }

    public interface EventListener {
        void onEvent(String type, String id, String Doctor_Id, String Time, String date);
    }

    public HistoryAdapter(List<HistoryModel> horizontalList, Context context, String type, EventListener listener) {
        this.verticalList = horizontalList;
        this.context = context;
        this.listener = listener;
        this.type = type;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView status;
        TextView package_name, datetime, location, patient_name, tvStatus;
        ImageButton dropdown;
        View view;
        RelativeLayout action_view;
        TextView start,hold,stop,download_invoice;

        public MyViewHolder(View root) {
            super(root);

            package_name = (TextView) root.findViewById(R.id.package_name);
            datetime = (TextView) root.findViewById(R.id.datetime);
            location = (TextView) root.findViewById(R.id.location);
            tvStatus = (TextView) root.findViewById(R.id.tvStatus);
            patient_name = (TextView) root.findViewById(R.id.patient_name);

            dropdown = (ImageButton) root.findViewById(R.id.dropdown);

            //cancel=(TextView)root.findViewById(R.id.cancel);
            //reschdule=(TextView)root.findViewById(R.id.reschdule);

            status = (ImageView) root.findViewById(R.id.status);

            view = root.findViewById(R.id.view);
            action_view = root.findViewById(R.id.action_view);
            start = root.findViewById(R.id.start);
            hold = root.findViewById(R.id.hold);
            stop = root.findViewById(R.id.stop);
            download_invoice = root.findViewById(R.id.download_invoice);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.package_name.setText(verticalList.get(position).package_name);
        //holder.datetime.setText(verticalList.get(position).date + " at "+ verticalList.get(position).time);

        SpannableString content1 = new SpannableString(verticalList.get(position).address1);
        content1.setSpan(new UnderlineSpan(), 0, content1.length(), 0);
        holder.location.setText(content1);

        SpannableString content = new SpannableString(verticalList.get(position).date);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        holder.datetime.setText(content);

        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String myLatitude = verticalList.get(position).latitude;
                String myLongitude = verticalList.get(position).longitude;
                String labelLocation = verticalList.get(position).user_name;

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + myLatitude  + ">,<" + myLongitude + ">?q=<" + myLatitude  + ">,<" + myLongitude + ">(" + labelLocation + ")"));
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }
            }
        });

        holder.datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEvent("datetime", verticalList.get(position).appointment_id, String.valueOf(position),
                        "", verticalList.get(position).date);
            }
        });

        if (verticalList.get(position).user_name.matches("")) {
            holder.patient_name.setText("N.A.");
        } else {
            holder.patient_name.setText(verticalList.get(position).user_name);
        }

        if (verticalList.get(position).status.matches("1")) {
            holder.status.setImageResource(R.drawable.ic_verified);
            holder.tvStatus.setText("Accepted");
            holder.dropdown.setVisibility(View.INVISIBLE);
        } else if (verticalList.get(position).status.matches("2")) {
            holder.status.setImageResource(R.drawable.ic_warning);
            holder.tvStatus.setText("Pending");
            if (verticalList.get(position).is_past_date.matches("1")) {
                holder.dropdown.setVisibility(View.INVISIBLE);
            } else {
                holder.dropdown.setVisibility(View.VISIBLE);
            }
        } else if (verticalList.get(position).status.matches("3")) {
            holder.status.setImageResource(R.drawable.ic_cancelled);
            holder.tvStatus.setText("Cancelled");
            holder.dropdown.setVisibility(View.INVISIBLE);
        }

        if(type.matches("today")){
           holder.view.setVisibility(View.VISIBLE);
           holder.action_view.setVisibility(View.VISIBLE);
        } else {
            holder.view.setVisibility(View.GONE);
            holder.action_view.setVisibility(View.GONE);
        }

        if(verticalList.get(position).service_status.matches("1")){
            holder.start.setBackgroundResource(R.drawable.grey_roundedview);
            holder.start.setEnabled(false);
        } else if(verticalList.get(position).service_status.matches("3")){
            holder.start.setBackgroundResource(R.drawable.grey_roundedview);
            holder.start.setEnabled(false);
            holder.stop.setBackgroundResource(R.drawable.grey_roundedview);
            holder.stop.setEnabled(false);
        } else if(verticalList.get(position).service_status.matches("2")){
            holder.start.setBackgroundResource(R.drawable.grey_roundedview);
            holder.start.setEnabled(false);
        }

        holder.start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.start.setBackgroundResource(R.drawable.grey_roundedview);
                holder.start.setEnabled(false);
                listener.onEvent("100", verticalList.get(position).appointment_id, String.valueOf(position),
                        "", verticalList.get(position).date);
            }
        });

        holder.hold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEvent("101", verticalList.get(position).appointment_id, String.valueOf(position),
                        "", verticalList.get(position).date);
            }
        });

        holder.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.stop.setBackgroundResource(R.drawable.grey_roundedview);
                holder.stop.setEnabled(false);
                listener.onEvent("102", verticalList.get(position).appointment_id, String.valueOf(position),
                        "", verticalList.get(position).date);
            }
        });

        holder.dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(context, view);
                popup.getMenuInflater().inflate(R.menu.pop_up_today, popup.getMenu());
                Menu popupMenu = popup.getMenu();

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.accept:
                                listener.onEvent("1", verticalList.get(position).appointment_id, String.valueOf(position),
                                        "", verticalList.get(position).date);
                                break;

                            case R.id.reject:
                                listener.onEvent("3", verticalList.get(position).appointment_id, String.valueOf(position),
                                        "", verticalList.get(position).date);
                                break;

                        }

                        return true;
                    }
                });
                popup.show();

            }
        });

        holder.download_invoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEvent("3001", verticalList.get(position).appointment_id, String.valueOf(position),
                        "", verticalList.get(position).date);
            }
        });

    }

    @Override
    public int getItemCount() {
        return verticalList.size();
    }
}
