package com.bedigi.partner.Model;

public class HistoryModel {

    public String appointment_id, user_name, date, early_morning, morning, afternoon, late_afternoon, evening,status,is_past_date,package_name,
            provider_name,provider_email,provider_phone;

    public HistoryModel(String appointment_id, String user_name, String date, String early_morning, String morning, String afternoon,
                        String late_afternoon, String evening, String status, String is_past_date, String package_name, String provider_name, String provider_email, String provider_phone) {

        this.appointment_id = appointment_id;
        this.date = date;
        this.early_morning = early_morning;
        this.morning = morning;
        this.afternoon = afternoon;
        this.late_afternoon = late_afternoon;
        this.evening = evening;
        this.status = status;
        this.is_past_date = is_past_date;
        this.user_name = user_name;
        this.package_name = package_name;
        this.provider_name = provider_name;
        this.provider_email = provider_email;
        this.provider_phone = provider_phone;
    }
}
