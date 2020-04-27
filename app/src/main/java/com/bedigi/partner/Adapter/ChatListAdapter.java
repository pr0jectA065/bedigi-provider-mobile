package com.bedigi.partner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bedigi.partner.Activities.ChatActivity;
import com.bedigi.partner.Activities.ChatList;
import com.bedigi.partner.Model.ChatListModel;
import com.bedigi.partner.Model.ChatModel;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;

import java.util.Collections;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {

    List<ChatListModel> verticalList = Collections.emptyList();
    Context context;
    AppPreferences appPreferences;

    public ChatListAdapter(List<ChatListModel> horizontalList, Context context) {
        this.verticalList = horizontalList;
        this.context = context;
        appPreferences = new AppPreferences(this.context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView package_name, user_name;
        LinearLayout parent;
        ImageButton delete;

        public MyViewHolder(View view) {
            super(view);

            package_name = (TextView) view.findViewById(R.id.package_name);
            user_name = (TextView) view.findViewById(R.id.user_name);
            delete = view.findViewById(R.id.delete);
            parent = (LinearLayout) view.findViewById(R.id.parent);

        }
    }

    @Override
    public ChatListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);

        return new ChatListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChatListAdapter.MyViewHolder holder, final int position) {

        holder.package_name.setText(verticalList.get(position).service_name);
        holder.user_name.setText(verticalList.get(position).user_name);

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("user_name", verticalList.get(position).user_name);
                intent.putExtra("provider_name", verticalList.get(position).provider_name);
                intent.putExtra("service_name", verticalList.get(position).service_name);
                intent.putExtra("customer_id", verticalList.get(position).customer_id);
                intent.putExtra("provider_id", verticalList.get(position).provider_id);
                intent.putExtra("service_provider_id", verticalList.get(position).service_provider_id);
                intent.putExtra("from", "chat");
                context.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (context instanceof ChatList) {
                    ((ChatList) context).delete_chat(verticalList.get(position).customer_id, verticalList.get(position).provider_id, verticalList.get(position).service_provider_id);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return verticalList.size();
    }
}