package com.exercise.continusebiometrics.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TextUtils {

    public TextUtils() {
    }

    public static boolean isPhoneNumberValid(String phone) {
        /*phone.matches("^(?=(?:[8-9]){1})(?=[0-9]{8}).*")*/
        phone = phone.trim().replaceAll("-", "");
        if (phone.isEmpty()) return false;
        Long phoneNum = new BigDecimal(phone).setScale(0, RoundingMode.HALF_UP).longValue();
        phone = phoneNum.toString();
        if (phone.length() > 12 || phone.length() < 9) return false;
        if (!phone.startsWith("0")) {
            if (phone.startsWith("972") && phone.length() == 12) return true;
            if (phone.length() == 9 && !phone.startsWith("0")) return true;

        } else if (phone.length() == 10) {
            return true;
        }
        return false;
    }

    public static String convertToValidPhoneNumber(String cellValue) {
        cellValue = cellValue.trim().replaceAll("-", "");
        Long phone = new BigDecimal(cellValue).setScale(0, RoundingMode.HALF_UP).longValue();
        String result = phone.toString();
        if (!result.startsWith("0") && result.length() == 9) return "0" + result;
        else return result;
    }

}
