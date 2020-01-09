package com.alphadvlpr.infiniteminds.utilities;

import java.text.Normalizer;

public class StringProcessor {
    public static String removeAccents(String s){
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String removeSpecial(String s){
        return s.replaceAll("[^\\w\\s]"," ");
    }

    public static String checkAndFixLink(String s){
        if(s.isEmpty()){ return ""; }
        else if(!s.contains("http")){ return "http://" + s; }
        else{ return s; }
    }
}
