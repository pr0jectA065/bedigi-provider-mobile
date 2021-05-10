package com.bedigi.partner.Model;

public class CommonModel {

    public String Id, Name;
    public boolean isSelected;

    public CommonModel(String Id, String Name, Boolean isSelected) {

        this.Id =Id;
        this.Name = Name;
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return Name;
    }
}
