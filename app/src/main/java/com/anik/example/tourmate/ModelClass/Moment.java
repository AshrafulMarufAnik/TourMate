package com.anik.example.tourmate.ModelClass;

public class Moment {
    private String imageName;
    private String imageURL;

    public Moment() {
    }

    public Moment(String imageName, String imageURL) {
        this.imageName = imageName;
        this.imageURL = imageURL;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }
}
