package com.campus.lostandfound.model;

import java.io.Serializable;

/**
 * Model class representing a Lost or Found item reported within the campus.
 */
public class Item implements Serializable {

    public static final String TYPE_LOST = "LOST";
    public static final String TYPE_FOUND = "FOUND";

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_CLAIMED = "CLAIMED";

    private int id;
    private String title;
    private String description;
    private String category;
    private String type;            // "LOST" or "FOUND"
    private String location;        // e.g., "Library 2nd Floor", "Room 304"
    private String date;            // e.g., "2026-10-15"
    private String contactName;     // Finder or Owner Name
    private String contactPhone;    // Contact Phone Number
    private String status;          // "ACTIVE" or "CLAIMED"
    private long timestamp;         // Milliseconds when created

    // Default Constructor
    public Item() {
        this.status = STATUS_ACTIVE;
        this.timestamp = System.currentTimeMillis();
    }

    // Parameterized Constructor
    public Item(int id, String title, String description, String category,
                String type, String location, String date,
                String contactName, String contactPhone, String status, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.type = type;
        this.location = location;
        this.date = date;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.status = status;
        this.timestamp = timestamp;
    }

    // Constructor without ID (for new insertions)
    public Item(String title, String description, String category,
                String type, String location, String date,
                String contactName, String contactPhone) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.type = type;
        this.location = location;
        this.date = date;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.status = STATUS_ACTIVE;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isLost() {
        return TYPE_LOST.equalsIgnoreCase(this.type);
    }

    public boolean isClaimed() {
        return STATUS_CLAIMED.equalsIgnoreCase(this.status);
    }
}
