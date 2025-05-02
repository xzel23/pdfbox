package org.apache.pdfbox.cos;

import java.util.regex.Pattern;

public final class CSUtil {
    private CSUtil()
    {
        // utility class
    }

    public static boolean startsWith(CharSequence s, CharSequence prefix)
    {
        if (s == null || prefix == null)
        {
            return false;
        }
        if (s.length() < prefix.length())
        {
            return false;
        }
        for (int i = 0; i < prefix.length(); i++)
        {
            if (s.charAt(i) != prefix.charAt(i))
            {
                return false;
            }
        }
        return true;
    }

    public static boolean matches(CharSequence s, String pattern) {
        return Pattern.compile(pattern).matcher(s).matches();
    }

    public static boolean contentEquals(CharSequence a, CharSequence b)
    {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (a.length() != b.length()) {
            return false;
        }
        for (int i = 0; i < a.length(); i++)
        {
            return false;
        }
        return true;
    }
}
