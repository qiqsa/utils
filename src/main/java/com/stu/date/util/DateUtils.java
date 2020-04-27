package com.stu.date.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

/**
 * Java8
 * @author Qi.qingshan
 * @date 2020/4/26
 */
public class DateUtils {

    /**
     * 获取年 yyyy
     * @return
     */
    public static int getYear(){
        return LocalDate.now().getYear();
    }

    /**
     * 获取日期 yyyy-MM-dd
     * @return
     */
    public static LocalDate getLocalDate(){
        return LocalDate.now();
    }

    /**
     * 获取时间 hh:mm:ss   17:48:23.096
     * @return
     */
    public static LocalTime getLocalTime(){
        return LocalTime.now();
    }

    /**
     * 获取日期时间 yyyy-MM-dd hh:mm:ss
     * @return
     */
    public static LocalDateTime getLocalDateTime(){
        return LocalDateTime.now();
    }

    /**
     * 自定义日期
     * @param date yyyy-MM-dd
     * @return
     */
    public static LocalDate parse(String date){
        return LocalDate.parse(date);
    }

    /**
     *获取本月的第几天
     * @param day
     * @return
     */
    public static LocalDate getLocaDate(int day){
        return LocalDate.now().withDayOfMonth(day);
    }

    /**
     * 获取一个月的某一天日期
     * @return
     */
    public static LocalDate getLocalDateOfDay(){
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取下一天
     * @return
     */
    public static LocalDate getPlusDays(){
        return LocalDate.now().plusDays(1);
    }

    /**
     * 自定义时间 "12:0:0"
     * @param time
     * @return
     */
    public static LocalTime parseTime(String time){
       return LocalTime.parse(time);
    }

    /**
     *使用自定义格式器DateTimeFormatter替换了Java8之前的SimpleDateFormat
     * @param format yyyy-MM-dd
     * @return
     */
    public static String format(String format){
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern(format, Locale.CHINA);
        return LocalDateTime.now().format(formatter);
    }

    public static void main(String[] args) {
        System.out.println(format("yyyy-MM-dd hh:mm:ss"));
    }
}
