package com.anik.example.tourmate;

public class Route {
    private String routePointName;
    private String routeDetails;

    public Route() {
    }

    public Route(String routePointName) {
        this.routePointName = routePointName;
    }

    public Route(String routePointName, String routeDetails) {
        this.routePointName = routePointName;
        this.routeDetails = routeDetails;
    }

    public String getRoutePointName() {
        return routePointName;
    }

    public String getRouteDetails() {
        return routeDetails;
    }
}
