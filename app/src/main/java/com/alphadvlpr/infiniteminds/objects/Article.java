package com.alphadvlpr.infiniteminds.objects;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class Article {

    private String title = "", content = "";
    private Timestamp date = null;
    private ArrayList<String> categories = new ArrayList<>(),
            downloadURL = new ArrayList<>(),
            keywords = new ArrayList<>(),
            images = new ArrayList<>();
    private Long visits = -1L;

    public Article() {}

    public Article(String title, String content, ArrayList<String> images,
                   Timestamp date, ArrayList<String> categories,
                   ArrayList<String> downloadURL, Long visits, ArrayList<String> keywords){
        this.title = title;
        this.content = content;
        this.images = images;
        this.categories = categories;
        this.downloadURL = downloadURL;
        this.visits = visits;
        this.date = date;
        this.keywords = keywords;
    }

    public String getTitle(){
        return title;
    }

    public Long getVisits(){ return visits; }

    public ArrayList<String> getImages(){ return images; }

    public String getContent() { return content; }

    public ArrayList<String> getDownloadURL() { return downloadURL; }

    public Timestamp getDate() { return date; }

    public ArrayList<String> getCategories() { return categories; }

    public ArrayList<String> getKeywords() { return keywords; }
}
