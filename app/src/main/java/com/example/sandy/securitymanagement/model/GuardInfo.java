package com.example.sandy.securitymanagement.model;

import java.util.ArrayList;

/**
 * Created by sandy on 21/11/17.
 */

public class GuardInfo
{


    int status;
    String datetime,id;
    String name;
    String working_status;
    String location;
    String latitude;
    String longitude;
    String blob;

    public GuardInfo(int status, String id,  String datetime, String name, String working_status, String location, String latitude, String longitude,String blob) {
        this.id = id;
        this.status = status;
        this.blob = blob;
        this.datetime = datetime;
        this.name = name;
        this.working_status = working_status;
        this.location = String.valueOf(location);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getBlob()
    {
        return blob;
    }

    public void setBlob(String blob)
    {
        this.blob = blob;
    }

    public String getDatetime()
    {
        return datetime;
    }

    public void setDatetime(String datetime)
    {
        this.datetime = datetime;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getWorking_status()
    {
        return working_status;
    }

    public void setWorking_status(String working_status)
    {
        this.working_status = working_status;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }
}
