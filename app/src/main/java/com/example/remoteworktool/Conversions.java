package com.example.remoteworktool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Conversions {

    public static String formatDateTo_yyyy_MM_dd(LocalDateTime date){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(dateFormatter);
    }

}
