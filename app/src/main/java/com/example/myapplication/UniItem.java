package com.example.myapplication;

public class UniItem {
    private String uniName;
    private String uniType;
    private String state;
    private String uniImage;

    public UniItem(){

    }

    public UniItem(String uniName, String uniType, String state, String uniImage) {
        this.uniName = uniName;
        this.uniType = uniType;
        this.state = state;
        this.uniImage = uniImage;
    }

    public String getUniName() {
        return uniName;
    }

    public void setUniName(String uniName) {
        this.uniName = uniName;
    }

    public String getUniType() {
        return uniType;
    }

    public void setUniType(String uniType) {
        this.uniType = uniType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUniImage() {
        return uniImage;
    }

    public void setUniImage(String uniImage) {
        this.uniImage = uniImage;
    }
}
