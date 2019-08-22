package com.anik.example.tourmate.ModelClass;

public class Route {
    private String routePointName;
    private String routeDetails;
    private String routeID;

    public Route() {
    }

    public Route(String routePointName) {
        this.routePointName = routePointName;
    }

    public Route(String routePointName, String routeID) {
        this.routePointName = routePointName;
        this.routeID = routeID;
    }

    public Route(String routePointName, String routeDetails, String routeID) {
        this.routePointName = routePointName;
        this.routeDetails = routeDetails;
        this.routeID = routeID;
    }

    public String getRouteID() {
        return routeID;
    }

    public String getRoutePointName() {
        return routePointName;
    }

    public String getRouteDetails() {
        return routeDetails;
    }
}
