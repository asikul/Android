package com.example.mdasikulislam.myapplication;

public class User {
    public String latitude;
    public String longitude;
    public String name;
    public String phone;
    public String roll;

    public User(String fName, String fPhone, String latitude, String longitude) {
        this.name = fName;
        this.phone = fPhone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }
}
