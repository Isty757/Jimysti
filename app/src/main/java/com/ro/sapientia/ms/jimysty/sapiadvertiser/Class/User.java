package com.ro.sapientia.ms.jimysty.sapiadvertiser.Class;

import android.net.Uri;

/**
 * Created by Drako on 13-Nov-17.
 */

public class User {

    private String firstName;
    private String lastName;
    private String mobileNumber;
    private Uri image;

    public User(String firstName, String lastName, String mobileNumber, Uri image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.image = image;
    }
    public User(String firstName, String lastName, String mobileNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
