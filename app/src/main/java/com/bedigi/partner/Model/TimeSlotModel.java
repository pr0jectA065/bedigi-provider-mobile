package com.bedigi.partner.Model;

public class TimeSlotModel {

    public String timeslot_id, provider_id, day,is_available,early_morning,morning,afternoon,late_afternoon,evening;

    public TimeSlotModel(String timeslot_id, String provider_id, String day, String is_available, String early_morning, String morning, String afternoon
            , String late_afternoon, String evening) {

        this.timeslot_id =timeslot_id;
        this.provider_id = provider_id;
        this.day = day;
        this.is_available = is_available;
        this.early_morning = early_morning;
        this.morning = morning;
        this.afternoon = afternoon;
        this.late_afternoon = late_afternoon;
        this.evening = evening;

    }

}
