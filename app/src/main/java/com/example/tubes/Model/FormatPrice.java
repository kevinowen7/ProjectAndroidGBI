package com.example.tubes.Model;

public class FormatPrice {
    public static String addDots(String digits) {
        String result = "";
        for (int i=1; i <= digits.length(); ++i) {
            char ch = digits.charAt(digits.length() - i);
            if (i % 3 == 1 && i > 1) {
                result = "." + result;
            }
            result = ch + result;
        }

        return result;
    }

    public static String removeDots(String digits) {
        return digits.replaceAll("\\.","");
    }
}
