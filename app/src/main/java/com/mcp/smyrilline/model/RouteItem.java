package com.mcp.smyrilline.model;

/**
 * Object class to store single route item for Route list in Ticket fragment
 */
public class RouteItem {
    private String departureHarbor;
    private String departureDate;
    private String arrivalHarbor;
    private String arrivalDate;

    public RouteItem(String departureHarbor, String departureDate, String arrivalHarbor, String arrivalDate) {
        this.departureHarbor = departureHarbor;
        this.departureDate = departureDate;
        this.arrivalHarbor = arrivalHarbor;
        this.arrivalDate = arrivalDate;
    }

    public String getDepartureHarbor() {
        return departureHarbor;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getArrivalHarbor() {
        return arrivalHarbor;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }
}
