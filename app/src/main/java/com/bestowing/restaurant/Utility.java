package com.bestowing.restaurant;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
    public String calculateTimeStamp(Date date) {
        Date now = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String current_date = format.format(now);
        String cmp_date = format.format(date);
        int current_year = Integer.parseInt(current_date.substring(0,4));
        int then_year = Integer.parseInt(cmp_date.substring(0,4));
        int current_month = Integer.parseInt(current_date.substring(5,7));
        int then_month = Integer.parseInt(cmp_date.substring(5,7));
        int current_day = Integer.parseInt(current_date.substring(8,10));
        int then_day = Integer.parseInt(cmp_date.substring(8,10));
        int current_hour = Integer.parseInt(current_date.substring(11,13));
        int then_hour = Integer.parseInt(cmp_date.substring(11,13));
        int current_minute = Integer.parseInt(current_date.substring(14,16));
        int then_minute = Integer.parseInt(cmp_date.substring(14,16));
        int current_second = Integer.parseInt(current_date.substring(17,19));
        int then_second = Integer.parseInt(cmp_date.substring(17,19));

        if((current_year-then_year>=2) || (current_year - then_year == 1 && current_month - then_month >= 0)){
            return current_year-then_year+"년 전";
        } else if(current_year - then_year == 1 && current_month - then_month < 0){
            return (current_month + 12 - then_month+"달 전");
        } else if((current_month-then_month>=2) || (current_month - then_month == 1 && current_day - then_day >= 0)){
            return (current_month-then_month+"달 전");
        } else if(current_month - then_month == 1 && current_day - then_day < 0){
            return (current_day + 30 - then_day+"일 전");
        } else if((current_day-then_day>=2) || (current_day - then_day == 1 && current_hour - then_hour >= 0)){
            return (current_day-then_day+"일 전");
        } else if(current_day - then_day == 1 && current_hour - then_hour < 0){
            return (current_hour + 24 - then_hour+"시간 전");
        } else if((current_hour-then_hour>=2) || (current_hour - then_hour == 1 && current_minute - then_minute >= 0)){
            return (current_hour-then_hour+"시간 전");
        } else if(current_hour - then_hour == 1 && current_minute - then_minute < 0){
            return (current_minute + 60 - then_minute+"분 전");
        } else if((current_minute-then_minute>=2) || (current_minute - then_minute == 1 && current_second - then_second >= 0)){
            return (current_minute-then_minute+"분 전");
        } else{
            return ("방금 전");
        }
    }
}
