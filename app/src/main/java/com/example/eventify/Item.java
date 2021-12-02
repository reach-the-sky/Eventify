package com.example.eventify;

import java.util.ArrayList;

public class Item {
    String title;
    String description;
    String uuid;
    ArrayList<String> category;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public ArrayList<String> getCategory() {
        return category;
    }
}
