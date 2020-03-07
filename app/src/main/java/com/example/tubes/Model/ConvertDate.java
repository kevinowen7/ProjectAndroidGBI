package com.example.tubes.Model;

import java.util.Calendar;

public class ConvertDate {

    public static String monthStringToInt(String month) {
        String[] monthString = new String[]{"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        int i=0;
        for(; i<12; i++) {
            if (monthString[i].equals(String.valueOf(month))){
                break;
            }
        }
        String monthResult="";
        if (i+1<10){
            monthResult = "0" + (i + 1);
        } else {
            monthResult = ""+(i+1);
        }
        return monthResult;
    }

    public static String monthIntToString(int month) {
        String[] monthString = new String[]{"Jan", "Feb", "Mar", "Apr", "Mei", "Jun", "Jul", "Ags", "Sep", "Okt", "Nov", "Des"};
        int i=0;
        for(; i<12; i++) {
            if (String.valueOf(month).equals(String.valueOf(i+1))){
                break;
            }
        }
        return monthString[i];
    }

    //example data = "2019-03-30"
    public static String reformatDateToString(String data){
        String[] arrayData = data.split("-");
        if (arrayData.length==3) {
            return arrayData[2] + "-" + monthIntToString(Integer.parseInt(arrayData[1])) + "-" + arrayData[0];
        } else {
            return monthIntToString(Integer.parseInt(arrayData[0])) + "-" + arrayData[1];
        }
    }

    //example data = "2019-03-30"
    public static String reformatDateToInt(String data){
        String[] arrayData = data.split("-");
        if (arrayData.length==3) {
            return arrayData[2] + "-" + monthStringToInt(arrayData[1]) + "-" + arrayData[0];
        } else {
            return arrayData[1]+ "-" +monthStringToInt(arrayData[0]) ;
        }
    }

}
