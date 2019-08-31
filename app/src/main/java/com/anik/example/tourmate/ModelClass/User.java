package com.anik.example.tourmate.ModelClass;

public class User {
    private String userName;
    private String userEmail;
    private int userTotalTours;
    private String userLocation;
    private String userProfileImage;
    private String userCoverImage;
    private String userImageDownloadURL;

    public User() {
    }

    public User(String userName, String userEmail) {
        this.userName = userName;
        this.userEmail = userEmail;
    }

    public User(String userImageDownloadURL) {
        this.userImageDownloadURL = userImageDownloadURL;
    }

    public User(String userName, String userEmail, String userLocation) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userLocation = userLocation;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserProfileImage() {
        return userProfileImage;
    }

    public String getUserCoverImage() {
        return userCoverImage;
    }

    public int getUserTotalTours() {
        return userTotalTours;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public String getUserImageDownloadURL() {
        return userImageDownloadURL;
    }
}
