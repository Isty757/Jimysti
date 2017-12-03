package com.ro.sapientia.ms.jimysty.sapiadvertiser.Class;

import java.util.List;

/**
 * Created by Drako on 13-Nov-17.
 */

public class Advertisement {

    private String id;
    private String title;
    private String description;
    private List<String> images;
    private User googleUser;

    public Advertisement(String id , String title, String description, List<String> images, User googleUser) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.images = images;
        this.googleUser = googleUser;
    }

    public User getGoogleUser() {
        return googleUser;
    }

    public void setGoogleUser(User googleUser) {
        this.googleUser = googleUser;
    }

    public Advertisement(){}

    public Advertisement(String title, String description, List<String> images) {
        this.title = title;
        this.description = description;
        this.images = images;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
