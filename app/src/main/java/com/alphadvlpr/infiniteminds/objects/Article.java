package com.alphadvlpr.infiniteminds.objects;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

/**
 * Custom object for the Articles.
 *
 * @author AlphaDvlpr.
 */
public class Article {

    private String title = "", content = "";
    private Timestamp date = null;
    private ArrayList<String> categories = new ArrayList<>();
    private ArrayList<String> downloadURL = new ArrayList<>();
    private ArrayList<String> keywords = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private Long visits = -1L;

    /**
     * Empty constructor used when fetching information from Firebase database.
     *
     * @author AlphaDvlpr.
     */
    public Article() {
    }

    /**
     * Full constructor used when creating a new Article for been uploaded to the database.
     *
     * @param title       The article's title.
     * @param content     The article's content.
     * @param images      All the images of the article converted to String using
     *                    {@link com.alphadvlpr.infiniteminds.utilities.ImageDecoder}.
     * @param date        The date when the articles has been created or edited.
     * @param categories  All the categories of the article.
     * @param downloadURL All the download URLs of the article.
     * @param visits      The visits counter of the article.
     * @param keywords    All the keywords of the article. This is an array containing all the words
     *                    of the title without spaces and special characters.
     * @author AlphaDvlpr.
     */
    public Article(String title, String content, ArrayList<String> images, Timestamp date, ArrayList<String> categories, ArrayList<String> downloadURL, Long visits, ArrayList<String> keywords) {
        this.title = title;
        this.content = content;
        this.images = images;
        this.categories = categories;
        this.downloadURL = downloadURL;
        this.visits = visits;
        this.date = date;
        this.keywords = keywords;
    }

    /**
     * @return Returns the article's title.
     * @author AlphaDvlpr.
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return Returns the article's visits.
     * @author AlphaDvlpr.
     */
    public Long getVisits() {
        return visits;
    }

    /**
     * @return Returns the article's images as a String array.
     * @author AlphaDvlpr.
     */
    public ArrayList<String> getImages() {
        return images;
    }

    /**
     * @return Returns the article's content.
     * @author AlphaDvlpr.
     */
    public String getContent() {
        return content;
    }

    /**
     * @return Returns the article's download URLs.
     * @author AlphaDvlpr.
     */
    public ArrayList<String> getDownloadURL() {
        return downloadURL;
    }

    /**
     * @return Returns the article's publish or update date.
     * @author AlphaDvlpr.
     */
    public Timestamp getDate() {
        return date;
    }

    /**
     * @return Returns the article's categories as a String array.
     * @author AlphaDvlpr.
     */
    public ArrayList<String> getCategories() {
        return categories;
    }

    /**
     * @return Returns the article's keywords as a String array.
     * @author AlphaDvlpr.
     */
    public ArrayList<String> getKeywords() {
        return keywords;
    }
}
