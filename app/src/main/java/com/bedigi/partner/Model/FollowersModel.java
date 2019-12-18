package com.bedigi.partner.Model;

import androidx.annotation.NonNull;

public class FollowersModel {

    public String follow_id,customer_id,customer_name,phone,user_pic;

    public FollowersModel(String follow_id, String customer_id, String customer_name, String phone, String user_pic) {

        this.follow_id = follow_id;
        this.customer_id = customer_id;
        this.customer_name = customer_name;
        this.phone = phone;
        this.user_pic = user_pic;
    }

}
