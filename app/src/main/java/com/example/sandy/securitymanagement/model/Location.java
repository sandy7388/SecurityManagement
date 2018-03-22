package com.example.sandy.securitymanagement.model;

/**
 * Created by sandy on 9/2/18.
 */

public class Location
{
    String location_name,location_id;

    public Location(String location_name, String location_id) {
        this.location_name = location_name;
        this.location_id = location_id;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }
}
