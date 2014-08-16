package com.lordmau5.ytrssdl.util;

import java.text.DateFormatSymbols;
import java.util.GregorianCalendar;

/**
 * Created by Lordmau5 on 15.08.2014.
 */
public class DateFormat {

    public int day, month, year;
    public int hour, minute, second;
    public int hourOffset;

    public DateFormat(int day, int month, int year, int hour, int minute, int second) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public DateFormat(int day, String month, int year, int hour, int minute, int second) {
        this(day, 0, year, hour, minute, second);
        this.month = monthToNumber(month);
    }

    public DateFormat(String YTFeedDate) {
        day = Integer.parseInt(YTFeedDate.substring(0, YTFeedDate.indexOf(' ')));
        YTFeedDate = YTFeedDate.substring(YTFeedDate.indexOf(' ') + 1);

        month = monthToNumber(YTFeedDate.substring(0, YTFeedDate.indexOf(' ')));
        YTFeedDate = YTFeedDate.substring(YTFeedDate.indexOf(' ') + 1);

        year = Integer.parseInt(YTFeedDate.substring(0, YTFeedDate.indexOf(' ')));
        YTFeedDate = YTFeedDate.substring(YTFeedDate.indexOf(' ') + 1);

        String[] time = YTFeedDate.substring(0, YTFeedDate.indexOf(' ')).split(":");
        hour = Integer.parseInt(time[0]);
        minute = Integer.parseInt(time[1]);
        second = Integer.parseInt(time[2]);

        YTFeedDate = YTFeedDate.substring(YTFeedDate.indexOf(' ') + 1);
        hourOffset = Integer.parseInt(YTFeedDate.substring(1, 3));
        if(YTFeedDate.charAt(0) == '-') {
            hourOffset *= -1;
        }

        fixDate();
    }

    public boolean isEarlier(DateFormat otherDate) {
        if(otherDate.year > year)
            return true;
        else if(otherDate.year == year) {
            if(otherDate.month > month)
                return true;
            else if(otherDate.month == month) {
                if(otherDate.day > day) {
                    return true;
                }
                else if(otherDate.day == day) {
                    if(otherDate.hour > hour)
                        return true;
                    else if(otherDate.hour == hour) {
                        if(otherDate.minute > minute)
                            return true;
                        else if(otherDate.minute == minute) {
                            if(otherDate.second > second)
                                return true;
                            else {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public String saveFormat() {
        return (day < 10 ? (new StringBuilder().append(0).append(day)) : day) + " " +
                months[month - 1]  + " " +
                year + " " +
                (hour < 10 ? (new StringBuilder().append(0).append(hour)) : hour) + ":" +
                (minute < 10 ? (new StringBuilder().append(0).append(minute)) : minute) + ":" +
                (second < 10 ? (new StringBuilder().append(0).append(second)) : second) + " +0000";
    }

    @Override
    public String toString() {
        return (day < 10 ? (new StringBuilder().append(0).append(day)) : day) + "-" +
               (month < 10 ? (new StringBuilder().append(0).append(month)) : month) + "-" +
               year + " " +
               (hour < 10 ? (new StringBuilder().append(0).append(hour)) : hour) + ":" +
               (minute < 10 ? (new StringBuilder().append(0).append(minute)) : minute) + ":" +
               (second < 10 ? (new StringBuilder().append(0).append(second)) : second);
    }

    private void fixDate() {
        hour += hourOffset;

        if(hour >= 24) {
            hour -= 24;
            day += 1;

            int febCheck = new GregorianCalendar().isLeapYear(year) ? 29 : 28;
            if(month == 2 && day > febCheck || month != 2 && day > monthDays[month - 1]) {
                if(month == 2)
                    day -= febCheck;
                else
                    day -= monthDays[month - 1];
                month += 1;

                if(month > 12) {
                    year += 1;
                    month -= 12;
                }
            }
        }
        else if(hour < 0) {
            hour += 24;
            day -= 1;

            if(day < 1) {
                month -= 1;
                int febCheck = new GregorianCalendar().isLeapYear(year) ? 29 : 28;
                if(month == 2)
                    day += febCheck;
                else
                    day += monthDays[month - 1];

                if(month < 1) {
                    year -= 1;
                    month += 12;
                }
            }
        }
    }

    String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    int[] monthDays = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private int monthToNumber(String month) {
        for(int i=0; i<months.length; i++)
            if(months[i].equals(month))
                return i + 1;
        return -1;
    }

}
