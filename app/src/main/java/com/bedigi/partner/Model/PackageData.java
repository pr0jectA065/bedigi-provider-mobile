package com.bedigi.partner.Model;

import org.json.JSONArray;

public class PackageData {

    public String service_provider_id, service_id, service_name,provider_id,price,sellprice,image,description,rating,experience_years,No_of_times_hire,
            availability,is_certified,is_fulltime,gender,user_pic,city_id,discount,first_name,phone,no_of_hours;
    public JSONArray timeslots;

    public PackageData(String service_provider_id, String service_id, String service_name, String provider_id, String price, String sellprice, String image, String description,
                       String rating, String experience_years, String No_of_times_hire, String availability, String is_certified, String is_fulltime, String gender,
                       String user_pic,String city_id,String discount,String first_name,String phone,JSONArray timeslots,String no_of_hours) {

        this.service_provider_id = service_provider_id;
        this.service_id = service_id;
        this.service_name = service_name;
        this.provider_id = provider_id;
        this.price = price;
        this.sellprice = sellprice;
        this.image = image;
        this.description = description;
        this.rating = rating;
        this.experience_years = experience_years;
        this.No_of_times_hire = No_of_times_hire;
        this.availability = availability;
        this.is_certified = is_certified;
        this.is_fulltime = is_fulltime;
        this.gender = gender;
        this.user_pic = user_pic;
        this.city_id = city_id;
        this.discount = discount;
        this.first_name = first_name;
        this.phone = phone;
        this.timeslots = timeslots;
        this.no_of_hours = no_of_hours;
    }
}
