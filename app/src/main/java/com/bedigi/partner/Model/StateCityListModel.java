package com.bedigi.partner.Model;

/**
 * Created by Admin on 4/1/2019.
 */

public class StateCityListModel {

    public String Id, Name, Status;

    public StateCityListModel(String Id, String Name, String Status) {

        this.Id =Id;
        this.Name = Name;
        this.Status = Status;
    }

    @Override
    public String toString() {
        return Name;
    }
}
