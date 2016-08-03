package com.example.lior.instakilo.model;

/**
 * Created by eliav.menachi on 25/03/2015.
 */
public class Student {
    String id;
    String fname;

    public String getLastUpdated() {
        return lastUpdated;
    }

    String lname;
    String phone;
    String address;

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    String imageName;
    String lastUpdated;
    boolean checked;

    public Student(){

    }
    public Student(String id, String fname, String lname, String phone, String address, String imageName, boolean checked) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.phone = phone;
        this.address = address;
        this.imageName = imageName;
        this.checked = checked;
    }

    public String getId() {
        return id;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getImageName() {
        return imageName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
