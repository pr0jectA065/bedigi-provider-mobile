package com.bedigi.partner.Adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.bedigi.partner.Model.ChatModel;
import com.bedigi.partner.Preferences.AppPreferences;
import com.bedigi.partner.R;

import java.util.Collections;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    List<ChatModel> verticalList = Collections.emptyList();
    Context context;
    AppPreferences appPreferences;
    String user_name,provider_name;

    public ChatAdapter(List<ChatModel> horizontalList, Context context,String user_name,String provider_name) {
        this.verticalList = horizontalList;
        this.context = context;
        this.provider_name = provider_name;
        this.user_name = user_name;
        appPreferences = new AppPreferences(this.context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView comment;
        LinearLayout singleMessageContainer;

        public MyViewHolder(View view) {
            super(view);

            comment = (TextView) view.findViewById(R.id.comment);
            singleMessageContainer = (LinearLayout) view.findViewById(R.id.singleMessageContainer);

        }
    }

    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_item, parent, false);

        return new ChatAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChatAdapter.MyViewHolder holder, final int position) {

        if(verticalList.get(position).sent_by.matches("2")){
            holder.comment.setBackgroundResource(R.drawable.bubble_b);
            holder.singleMessageContainer.setGravity(Gravity.END);
            String name = provider_name;
            SpannableString s = new SpannableString(name);
            s.setSpan(new RelativeSizeSpan(0.7f), 0, name.length(), 0);

            String comm = verticalList.get(position).text;
            CharSequence charr = TextUtils.concat(s, "\n", comm);
            holder.comment.setText(charr);
        }else {
            holder.comment.setBackgroundResource(R.drawable.bubble_a);
            holder.singleMessageContainer.setGravity(Gravity.START);
            String name = user_name;
            SpannableString s = new SpannableString(name);
            s.setSpan(new RelativeSizeSpan(0.7f), 0, name.length(), 0);

            String comm = verticalList.get(position).text;
            CharSequence charr = TextUtils.concat(s, "\n", comm);
            holder.comment.setText(charr);
        }

    }

    @Override
    public int getItemCount() {
        return verticalList.size();
    }
}