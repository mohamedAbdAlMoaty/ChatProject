package com.mohamed.abdelmoaty.chatproject.models;

/**
 * Created by HP on 6/11/2018.
 */

public class User {
    String user_id;
    String name;
    String status_date;
    String image;
    String icon;

    public User(String user_id,String name, String status_date, String image,String icon) {
        this.user_id=user_id;
        this.name = name;
        this.status_date = status_date;
        this.image = image;
        this.icon=icon;
    }

    public String getStatus_date() {
        return status_date;
    }

    public void setStatus_date(String status_date) {
        this.status_date = status_date;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
