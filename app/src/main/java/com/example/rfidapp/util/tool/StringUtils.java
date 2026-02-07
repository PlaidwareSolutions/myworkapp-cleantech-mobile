package com.example.rfidapp.util.tool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtils {
    public static String replaceUrlWithPlus(String str) {
        if (str != null) {
            return str.replaceAll("http://(.)*?/", "").replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
        }
        return null;
    }

    public static Boolean isIP(String str) {
        if (str == null || str.isEmpty() || !str.matches("^((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)(\\.((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)){3}$")) {
            return false;
        }
        return true;
    }

    public static Boolean isDomain(String str) {
        if (str == null || str.isEmpty() || !str.matches("^([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&amp;%\\$\\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|localhost|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))$")) {
            return false;
        }
        return true;
    }

    public static boolean isEmpty(CharSequence charSequence) {
        return charSequence == null || charSequence.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence charSequence) {
        return !isEmpty(charSequence);
    }

    public static String trim(String str) {
        if (str == null) {
            return null;
        }
        return str.trim();
    }

    public static int toInt(String str, int i) {
        try {
            return Integer.parseInt(str);
        } catch (Exception unused) {
            return i;
        }
    }

    public static int toInt(Object obj) {
        if (obj == null) {
            return 0;
        }
        return toInt(obj.toString(), 0);
    }

    public static long toLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception unused) {
            return 0;
        }
    }

    public static double toDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception unused) {
            return 0.0d;
        }
    }

    public static boolean toBool(String str) {
        try {
            return Boolean.parseBoolean(str);
        } catch (Exception unused) {
            return false;
        }
    }

    public static Boolean isInt(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static String getTimeString() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(System.currentTimeMillis()));
    }

    public static String getTimeFormat(long j) {
        return new SimpleDateFormat("yyyy MM dd HH:mm:ss").format(new Date(j));
    }

    public static boolean isHexNumber(String str) {
        int i = 0;
        boolean z = false;
        while (i < str.length()) {
            char charAt = str.charAt(i);
            if (charAt != '0' && charAt != '1' && charAt != '2' && charAt != '3' && charAt != '4' && charAt != '5' && charAt != '6' && charAt != '7' && charAt != '8' && charAt != '9' && charAt != 'A' && charAt != 'B' && charAt != 'C' && charAt != 'D' && charAt != 'E' && charAt != 'F' && charAt != 'a' && charAt != 'b' && charAt != 'c' && charAt != 'c' && charAt != 'd' && charAt != 'e' && charAt != 'f') {
                return false;
            }
            i++;
            z = true;
        }
        return z;
    }

    public static char[] HexStringToChars(String str) {
        int length = str.length() / 2;
        char[] cArr = new char[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            cArr[i] = (char) Integer.parseInt(str.substring(i2, i2 + 2), 16);
        }
        return cArr;
    }
}
