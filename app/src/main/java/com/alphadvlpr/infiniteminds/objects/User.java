package com.alphadvlpr.infiniteminds.objects;

public class User {

    private String email, nickname;
    private Long published;
    private boolean admin;

    public User(){}

    public User(String email, String nickname, Long published, boolean admin){
        this.email = email;
        this.nickname = nickname;
        this.published = published;
        this.admin = admin;
    }

    public String getEmail(){ return email; }

    public String getNickname(){ return nickname; }

    public boolean getAdmin(){ return admin; }

    public Long getPublished(){ return published; }
}
