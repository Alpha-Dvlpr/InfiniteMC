package com.alphadvlpr.infiniteminds.objects;

/**
 * Custom object for the categories.
 *
 * @author AlphaDvlpr.
 */
public class Category {

    private String name;

    /**
     * Empty constructor used when fetching the categories from the Firebase database.
     *
     * @author AlphaDvlpr.
     */
    public Category() {
    }

    /**
     * Full constructor used for creating a new category to be uploaded to the database.
     *
     * @param name The category's name.
     * @author AlphaDvlpr.
     */
    public Category(String name) {
        this.name = name;
    }

    /**
     * @return Returns the category's name.
     * @author AlphaDvlpr.
     */
    public String getName() {
        return name;
    }
}
