package com.example.myapplication;

public class DataModel {

    private String downloadURL;
    private String plantName;

    DataModel(){}

    public DataModel(String plantName, String downloadURL) {
        this.plantName = plantName;
        this.downloadURL= downloadURL;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String name) {
        this.plantName = name;
    }
}
