package com.alphadvlpr.infiniteminds.objects;

/**
 * Custom object for the users.
 *
 * @author AlphaDvlpr.
 */
public class User {

    private String email, nickname;
    private Long published;
    private boolean admin;

    /**
     * Empty constructor used when fetching data from Firebase database.
     *
     * @author AlphaDvlpr.
     */
    public User() {
    }

    /**
     * Full constructor used when creating a new user to be uploaded to the database.
     *
     * @param email     The user's email.
     * @param nickname  The user's nickname.
     * @param published The user's number of article's published.
     * @param admin     The privilege of the user.
     * @author AlphaDvlpr.
     */
    public User(String email, String nickname, Long published, boolean admin) {
        this.email = email;
        this.nickname = nickname;
        this.published = published;
        this.admin = admin;
    }

    /**
     * @return Returns the user's email.
     * @author AlphaDvlpr.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return Returns the user's nickname.
     * @author AlphaDvlpr.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return Returns the user's admin status.
     * @author AlphaDvlpr.
     */
    public boolean getAdmin() {
        return admin;
    }

    /**
     * @return Returns the user's total number of published articles.
     * @author AlphaDvlpr.
     */
    public Long getPublished() {
        return published;
    }
}
