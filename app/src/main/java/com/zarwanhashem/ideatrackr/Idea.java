package com.zarwanhashem.ideatrackr;

public class Idea {
    private String title;
    private String details;

    public Idea (String title, String details) {
        this.title = title;
        this.details = details;
    }

    public String getTitle() { return title; }

    public void setTitle(String newTitle) { title = newTitle; }

    public String getDetails() { return details; }

    public void setDetails(String newDetails) { details = newDetails; }
}
