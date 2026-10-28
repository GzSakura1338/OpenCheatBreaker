package net.optifine;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class StrUtils {
    public static boolean equalsMask(String str, String str2, char c, char c2) {
        if (str2 != null && str != null) {
            if (str2.indexOf(c) < 0) {
                return str2.indexOf(c2) < 0 ? str2.equals(str) : equalsMaskSingle(str, str2, c2);
            }

            ArrayList list = new ArrayList();
            String str3 = "" + c;
            if (str2.startsWith(str3)) {
                list.add("");
            }

            StringTokenizer stringTokenizer = new StringTokenizer(str2, str3);

            while (stringTokenizer.hasMoreElements()) {
                list.add(stringTokenizer.nextToken());
            }

            if (str2.endsWith(str3)) {
                list.add("");
            }

            String str4 = (String)list.get(0);
            if (!startsWithMaskSingle(str, str4, c2)) {
                return false;
            }

            String str5 = (String)list.get(list.size() - 1);
            if (!endsWithMaskSingle(str, str5, c2)) {
                return false;
            }

            int n = 0;

            for (int i = 0; i < list.size(); i++) {
                String str6 = (String)list.get(i);
                if (str6.length() > 0) {
                    int indexOfMaskSingle2 = indexOfMaskSingle(str, str6, n, c2);
                    if (indexOfMaskSingle2 < 0) {
                        return false;
                    }

                    n = indexOfMaskSingle2 + str6.length();
                }
            }

            return true;
        } else {
            return str2 == str;
        }
    }

    public static boolean equalsMaskSingle(String str, String str2, char c) {
        if (str != null && str2 != null) {
            if (str.length() != str2.length()) {
                return false;
            }

            for (int i = 0; i < str2.length(); i++) {
                char charAt = str2.charAt(i);
                if (charAt != c && str.charAt(i) != charAt) {
                    return false;
                }
            }

            return true;
        } else {
            return str == str2;
        }
    }

    public static int indexOfMaskSingle(String str, String str2, int n, char c) {
        if (str != null && str2 != null) {
            if (n >= 0 && n <= str.length()) {
                if (str.length() < n + str2.length()) {
                    return -1;
                }

                for (int i = n; i + str2.length() <= str.length(); i++) {
                    String substring = str.substring(i, i + str2.length());
                    if (equalsMaskSingle(substring, str2, c)) {
                        return i;
                    }
                }

                return -1;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public static boolean endsWithMaskSingle(String str, String str2, char c) {
        if (str != null && str2 != null) {
            if (str.length() < str2.length()) {
                return false;
            }

            String substring = str.substring(str.length() - str2.length(), str.length());
            return equalsMaskSingle(substring, str2, c);
        } else {
            return str == str2;
        }
    }

    public static boolean startsWithMaskSingle(String str, String str2, char c) {
        if (str != null && str2 != null) {
            if (str.length() < str2.length()) {
                return false;
            }

            String substring = str.substring(0, str2.length());
            return equalsMaskSingle(substring, str2, c);
        } else {
            return str == str2;
        }
    }

    public static boolean equalsMask(String str, String[] strs, char c) {
        for (int i = 0; i < strs.length; i++) {
            String str2 = strs[i];
            if (equalsMask(str, str2, c)) {
                return true;
            }
        }

        return false;
    }

    public static boolean equalsMask(String str, String str2, char c) {
        if (str2 != null && str != null) {
            if (str2.indexOf(c) < 0) {
                return str2.equals(str);
            }

            ArrayList list = new ArrayList();
            String str3 = "" + c;
            if (str2.startsWith(str3)) {
                list.add("");
            }

            StringTokenizer stringTokenizer = new StringTokenizer(str2, str3);

            while (stringTokenizer.hasMoreElements()) {
                list.add(stringTokenizer.nextToken());
            }

            if (str2.endsWith(str3)) {
                list.add("");
            }

            String str4 = (String)list.get(0);
            if (!str.startsWith(str4)) {
                return false;
            }

            String str5 = (String)list.get(list.size() - 1);
            if (!str.endsWith(str5)) {
                return false;
            }

            int n = 0;

            for (int i = 0; i < list.size(); i++) {
                String str6 = (String)list.get(i);
                if (str6.length() > 0) {
                    int indexOf = str.indexOf(str6, n);
                    if (indexOf < 0) {
                        return false;
                    }

                    n = indexOf + str6.length();
                }
            }

            return true;
        } else {
            return str2 == str;
        }
    }

    public static String[] split(String str, String str2) {
        if (str != null && str.length() > 0) {
            if (str2 == null) {
                return new String[]{str};
            }

            ArrayList list = new ArrayList();
            int n = 0;

            for (int i = 0; i < str.length(); i++) {
                char charAt = str.charAt(i);
                if (equals(charAt, str2)) {
                    list.add(str.substring(n, i));
                    n = i + 1;
                }
            }

            list.add(str.substring(n, str.length()));
            return (String[])list.toArray(new String[list.size()]);
        } else {
            return new String[0];
        }
    }

    public static boolean equals(char c, String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                return true;
            }
        }

        return false;
    }

    public static boolean equalsTrim(String str, String str2) {
        if (str != null) {
            str = str.trim();
        }

        if (str2 != null) {
            str2 = str2.trim();
        }

        return equals(str, str2);
    }

    public static boolean isEmpty(String str) {
        return str == null ? true : str.trim().length() <= 0;
    }

    public static String stringInc(String str) {
        int parseInt2 = parseInt(str, -1);
        if (parseInt2 == -1) {
            return "";
        }

        String str2 = "" + ++parseInt2;
        return str2.length() > str.length() ? "" : fillLeft("" + parseInt2, str.length(), '0');
    }

    public static int parseInt(String str, int n) {
        if (str == null) {
            return n;
        }

        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return n;
        }
    }

    public static boolean isFilled(String str) {
        return !isEmpty(str);
    }

    public static String addIfNotContains(String str, String str2) {
        for (int i = 0; i < str2.length(); i++) {
            if (str.indexOf(str2.charAt(i)) < 0) {
                str = str + str2.charAt(i);
            }
        }

        return str;
    }

    public static String fillLeft(String str, int n, char c) {
        if (str == null) {
            str = "";
        }

        if (str.length() >= n) {
            return str;
        }

        StringBuffer sb = new StringBuffer();
        int n2 = n - str.length();

        while (sb.length() < n2) {
            sb.append(c);
        }

        return sb.toString() + str;
    }

    public static String fillRight(String str, int n, char c) {
        if (str == null) {
            str = "";
        }

        if (str.length() >= n) {
            return str;
        }

        StringBuffer sb = new StringBuffer(str);

        while (sb.length() < n) {
            sb.append(c);
        }

        return sb.toString();
    }

    public static boolean equals(Object obj, Object obj2) {
        return obj == obj2 ? true : (obj != null && obj.equals(obj2) ? true : obj2 != null && obj2.equals(obj));
    }

    public static boolean startsWith(String str, String[] strs) {
        if (str == null) {
            return false;
        }

        if (strs == null) {
            return false;
        }

        for (int i = 0; i < strs.length; i++) {
            String str2 = strs[i];
            if (str.startsWith(str2)) {
                return true;
            }
        }

        return false;
    }

    public static boolean endsWith(String str, String[] strs) {
        if (str == null) {
            return false;
        }

        if (strs == null) {
            return false;
        }

        for (int i = 0; i < strs.length; i++) {
            String str2 = strs[i];
            if (str.endsWith(str2)) {
                return true;
            }
        }

        return false;
    }

    public static String removePrefix(String str, String str2) {
        if (str != null && str2 != null) {
            if (str.startsWith(str2)) {
                str = str.substring(str2.length());
            }

            return str;
        } else {
            return str;
        }
    }

    public static String removeSuffix(String str, String str2) {
        if (str != null && str2 != null) {
            if (str.endsWith(str2)) {
                str = str.substring(0, str.length() - str2.length());
            }

            return str;
        } else {
            return str;
        }
    }

    public static String replaceSuffix(String str, String str2, String str3) {
        if (str == null || str2 == null) {
            return str;
        }

        if (!str.endsWith(str2)) {
            return str;
        }

        if (str3 == null) {
            str3 = "";
        }

        str = str.substring(0, str.length() - str2.length());
        return str + str3;
    }

    public static String replacePrefix(String str, String str2, String str3) {
        if (str == null || str2 == null) {
            return str;
        }

        if (!str.startsWith(str2)) {
            return str;
        }

        if (str3 == null) {
            str3 = "";
        }

        str = str.substring(str2.length());
        return str3 + str;
    }

    public static String[] remove(String[] strs, int n, int n2) {
        if (strs == null) {
            return strs;
        }

        if (n2 > 0 && n < strs.length) {
            if (n >= n2) {
                return strs;
            }

            ArrayList list = new ArrayList(strs.length);

            for (int i = 0; i < strs.length; i++) {
                String str = strs[i];
                if (i < n || i >= n2) {
                    list.add(str);
                }
            }

            return (String[])list.toArray(new String[list.size()]);
        } else {
            return strs;
        }
    }

    public static String removeSuffix(String str, String[] strs) {
        if (str != null && strs != null) {
            int n = str.length();

            for (int i = 0; i < strs.length; i++) {
                String str2 = strs[i];
                str = removeSuffix(str, str2);
                if (str.length() != n) {
                    break;
                }
            }

            return str;
        } else {
            return str;
        }
    }

    public static String removePrefix(String str, String[] strs) {
        if (str != null && strs != null) {
            int n = str.length();

            for (int i = 0; i < strs.length; i++) {
                String str2 = strs[i];
                str = removePrefix(str, str2);
                if (str.length() != n) {
                    break;
                }
            }

            return str;
        } else {
            return str;
        }
    }

    public static String removePrefixSuffix(String str, String[] strs, String[] strs2) {
        str = removePrefix(str, strs);
        return removeSuffix(str, strs2);
    }

    public static String removePrefixSuffix(String str, String str2, String str3) {
        return removePrefixSuffix(str, new String[]{str2}, new String[]{str3});
    }

    public static String getSegment(String str, String str2, String str3) {
        if (str != null && str2 != null && str3 != null) {
            int indexOf = str.indexOf(str2);
            if (indexOf < 0) {
                return null;
            }

            int indexOf2 = str.indexOf(str3, indexOf);
            return indexOf2 < 0 ? null : str.substring(indexOf, indexOf2 + str3.length());
        } else {
            return null;
        }
    }

    public static int endsWith(String[] strs, String str) {
        if (strs != null && str != null) {
            for (int i = 0; i < strs.length; i++) {
                String str2 = strs[i];
                if (str2.endsWith(str)) {
                    return i;
                }
            }

            return -1;
        } else {
            return -1;
        }
    }

    public static int startsWith(String[] strs, String str) {
        if (strs != null && str != null) {
            for (int i = 0; i < strs.length; i++) {
                String str2 = strs[i];
                if (str2.startsWith(str)) {
                    return i;
                }
            }

            return -1;
        } else {
            return -1;
        }
    }

    public static String createStringBuilder(String str, String str2) {
        return str == null || str2 == null ? str : (str.endsWith(str2) ? str : str + str2);
    }

    public static String createStringBuilder2(String str, String str2) {
        return str == null || str2 == null ? str : (str.endsWith(str2) ? str : str2 + str);
    }
}
