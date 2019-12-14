package com.bedigi.partner.Model;

public class ChatModel {

    public String id, customer_id, provider_id,text,created_date,sent_by;

    public ChatModel(String id, String customer_id, String provider_id, String text, String created_date, String sent_by) {

        this.id = id;
        this.customer_id = customer_id;
        this.provider_id = provider_id;
        this.text = text;
        this.created_date = created_date;
        this.sent_by = sent_by;
    }
}
