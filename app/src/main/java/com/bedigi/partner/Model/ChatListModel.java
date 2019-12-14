package com.bedigi.partner.Model;

public class ChatListModel {

    public String id, customer_id, provider_id,text,created_date,user_name,provider_name,service_name,service_provider_id;

    public ChatListModel(String id, String customer_id, String provider_id, String text, String created_date,String user_name,String provider_name,String service_name,
                         String service_provider_id) {

        this.id = id;
        this.customer_id = customer_id;
        this.provider_id = provider_id;
        this.text = text;
        this.created_date = created_date;
        this.user_name = user_name;
        this.provider_name = provider_name;
        this.service_name = service_name;
        this.service_provider_id = service_provider_id;
    }
}
