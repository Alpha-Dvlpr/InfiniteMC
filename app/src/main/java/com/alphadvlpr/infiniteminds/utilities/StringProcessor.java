package com.alphadvlpr.infiniteminds.utilities;

import java.text.Normalizer;

/**
 * This class checks and fix the following:
 * 1) Removes the accents typical of non-english languages such as Spanish or French.
 * 2) Removes the special characters such as '?', ':', '&', etc.
 * 3) Check if the given URL can be opened, if not if fixes it.
 *
 * @author AlphaDvlpr.
 */
public class StringProcessor {

    /**
     * This method checks if the String contains non-desired accented characters and replaces
     * them with non-accented desired ones.
     *
     * @param s The String to be checked.
     * @return Returns the fixed String.
     * @author AlphaDvlpr.
     */
    public static String removeAccents(String s) {
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /**
     * This method checks if the String contains non-desired special characters and deletes them.
     *
     * @param s The String to be checked.
     * @return Returns the String without special Characters.
     * @uthor AlphaDvlpr.
     */
    public static String removeSpecial(String s) {
        return s.replaceAll("[^\\w\\s]", " ");
    }

    /**
     * This method checks if the URL can be loaded and fixes it if not.
     *
     * @param s The URL to be checked.
     * @return Returns the fixed URL.
     * @author AlphaDvlpr.
     */
    public static String checkAndFixLink(String s) {
        if (s.isEmpty()) {
            return "";
        } else if (!s.contains("http")) {
            return "http://" + s;
        } else {
            return s;
        }
    }
}
