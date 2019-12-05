package com.bedigi.partner.Model;

import androidx.annotation.NonNull;

public class HomeModel {

    public String id,parent_id,name,image,description;

    public HomeModel(String id, String parent_id, String name, String image, String description) {

        this.id = id;
        this.parent_id = parent_id;
        this.name = name;
        this.image = image;
        this.description = description;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
