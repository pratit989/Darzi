package com.infinityandriod.darzi.HelperClasses.HomeAdapter;

public class ManagementHelperClass {

    int image;
    String title,description;

    public ManagementHelperClass(int image, String title, String description) {
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
