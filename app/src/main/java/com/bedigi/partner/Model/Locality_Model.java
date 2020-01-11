package com.bedigi.partner.Model;

public class Locality_Model {

    public String Id, Name, Status;

    public Locality_Model(String Id, String Name, String Status) {

        this.Id =Id;
        this.Name = Name;
        this.Status = Status;
    }

    @Override
    public String toString() {
        return Name;
    }
}
