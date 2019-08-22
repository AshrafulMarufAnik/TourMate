package com.anik.example.tourmate.ModelClass;

import java.util.ArrayList;

public class Tour {
    private String tourID,tourName,tourLocation,tourReturnDate,tourDate,tourTime;
    private double tourBudget;
    private ArrayList<Route> routeList;
    private ArrayList<Expense> expenseList;

    public Tour() {
    }

    public Tour(String tourID, String tourName, String tourReturnDate, String tourDate, String tourTime, double tourBudget) {
        this.tourID = tourID;
        this.tourName = tourName;
        this.tourReturnDate = tourReturnDate;
        this.tourDate = tourDate;
        this.tourTime = tourTime;
        this.tourBudget = tourBudget;
    }

    public Tour(String tourID, String tourName, String tourLocation, String tourReturnDate, String tourDate, String tourTime, double tourBudget) {
        this.tourID = tourID;
        this.tourName = tourName;
        this.tourLocation = tourLocation;
        this.tourReturnDate = tourReturnDate;
        this.tourDate = tourDate;
        this.tourTime = tourTime;
        this.tourBudget = tourBudget;
    }

    public Tour(String tourID, String tourName, String tourLocation, String tourReturnDate, String tourDate, String tourTime, double tourBudget, ArrayList<Route> routeList) {
        this.tourID = tourID;
        this.tourName = tourName;
        this.tourLocation = tourLocation;
        this.tourReturnDate = tourReturnDate;
        this.tourDate = tourDate;
        this.tourTime = tourTime;
        this.tourBudget = tourBudget;
        this.routeList = routeList;
    }

    public Tour(String tourID, String tourName, String tourLocation, String tourReturnDate, String tourDate, String tourTime, double tourBudget, ArrayList<Route> routeList, ArrayList<Expense> expenseList) {
        this.tourID = tourID;
        this.tourName = tourName;
        this.tourLocation = tourLocation;
        this.tourReturnDate = tourReturnDate;
        this.tourDate = tourDate;
        this.tourTime = tourTime;
        this.tourBudget = tourBudget;
        this.routeList = routeList;
        this.expenseList = expenseList;
    }

    public String getTourID() {
        return tourID;
    }

    public String getTourName() {
        return tourName;
    }

    public String getTourLocation() {
        return tourLocation;
    }

    public String getTourReturnDate() {
        return tourReturnDate;
    }

    public String getTourDate() {
        return tourDate;
    }

    public String getTourTime() {
        return tourTime;
    }

    public double getTourBudget() {
        return tourBudget;
    }

    public ArrayList<Route> getRouteList() {
        return routeList;
    }

    public ArrayList<Expense> getExpenseList() {
        return expenseList;
    }
}
