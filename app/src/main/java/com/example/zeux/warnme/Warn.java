package com.example.zeux.warnme;

/**
 * Created by zeux on 29/09/16.
 */
public class Warn {
    private String title, place, description, image, username;

    public Warn() {

    }

    public Warn(String title, String place, String description, String image, String username) {
        this.title = title;
        this.place = place;
        this.description = description;
        this.image = image;
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public String getUsername() {
        return username;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
