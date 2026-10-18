package com.campus.lostandfound.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.campus.lostandfound.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite Database Helper for local storage of Campus Lost & Found records.
 * Provides complete CRUD operations without internet dependency.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "campus_lost_found.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    public static final String TABLE_ITEMS = "items";

    // Column Names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CONTACT_NAME = "contact_name";
    public static final String COLUMN_CONTACT_PHONE = "contact_phone";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    // Create Table SQL Query
    private static final String CREATE_TABLE_ITEMS = "CREATE TABLE " + TABLE_ITEMS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_CATEGORY + " TEXT, "
            + COLUMN_TYPE + " TEXT NOT NULL, "
            + COLUMN_LOCATION + " TEXT NOT NULL, "
            + COLUMN_DATE + " TEXT NOT NULL, "
            + COLUMN_CONTACT_NAME + " TEXT NOT NULL, "
            + COLUMN_CONTACT_PHONE + " TEXT NOT NULL, "
            + COLUMN_STATUS + " TEXT DEFAULT 'ACTIVE', "
            + COLUMN_TIMESTAMP + " INTEGER"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    /**
     * Create: Insert a new Lost or Found item
     */
    public long insertItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_CATEGORY, item.getCategory());
        values.put(COLUMN_TYPE, item.getType());
        values.put(COLUMN_LOCATION, item.getLocation());
        values.put(COLUMN_DATE, item.getDate());
        values.put(COLUMN_CONTACT_NAME, item.getContactName());
        values.put(COLUMN_CONTACT_PHONE, item.getContactPhone());
        values.put(COLUMN_STATUS, item.getStatus());
        values.put(COLUMN_TIMESTAMP, item.getTimestamp() > 0 ? item.getTimestamp() : System.currentTimeMillis());

        long id = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return id;
    }

    /**
     * Read: Get an item by its primary key ID
     */
    public Item getItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        Item item = null;
        if (cursor != null && cursor.moveToFirst()) {
            item = cursorToItem(cursor);
            cursor.close();
        }
        db.close();
        return item;
    }

    /**
     * Read: Retrieve items with optional type filter, category filter, and text search
     */
    public List<Item> getItems(String typeFilter, String categoryFilter, String searchQuery) {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder selection = new StringBuilder();
        List<String> selectionArgsList = new ArrayList<>();

        // 1. Status / Type Filter
        if (typeFilter != null && !typeFilter.isEmpty() && !typeFilter.equalsIgnoreCase("ALL")) {
            if (typeFilter.equalsIgnoreCase("CLAIMED")) {
                selection.append(COLUMN_STATUS).append(" = ?");
                selectionArgsList.add("CLAIMED");
            } else {
                selection.append(COLUMN_TYPE).append(" = ?");
                selectionArgsList.add(typeFilter);
            }
        }

        // 2. Category Filter
        if (categoryFilter != null && !categoryFilter.isEmpty() && !categoryFilter.equalsIgnoreCase("All Categories")) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(COLUMN_CATEGORY).append(" = ?");
            selectionArgsList.add(categoryFilter);
        }

        // 3. Search Query (Title, Description, or Location)
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append("(")
                    .append(COLUMN_TITLE).append(" LIKE ? OR ")
                    .append(COLUMN_DESCRIPTION).append(" LIKE ? OR ")
                    .append(COLUMN_LOCATION).append(" LIKE ?")
                    .append(")");
            String queryParam = "%" + searchQuery.trim() + "%";
            selectionArgsList.add(queryParam);
            selectionArgsList.add(queryParam);
            selectionArgsList.add(queryParam);
        }

        String selectionString = selection.length() > 0 ? selection.toString() : null;
        String[] selectionArgs = selectionArgsList.isEmpty() ? null : selectionArgsList.toArray(new String[0]);

        // Order by timestamp descending (newest items first)
        Cursor cursor = db.query(TABLE_ITEMS, null,
                selectionString,
                selectionArgs,
                null, null,
                COLUMN_TIMESTAMP + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                itemList.add(cursorToItem(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return itemList;
    }

    /**
     * Update: Update an existing item record
     */
    public int updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, item.getTitle());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_CATEGORY, item.getCategory());
        values.put(COLUMN_TYPE, item.getType());
        values.put(COLUMN_LOCATION, item.getLocation());
        values.put(COLUMN_DATE, item.getDate());
        values.put(COLUMN_CONTACT_NAME, item.getContactName());
        values.put(COLUMN_CONTACT_PHONE, item.getContactPhone());
        values.put(COLUMN_STATUS, item.getStatus());

        int rows = db.update(TABLE_ITEMS, values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(item.getId())});
        db.close();
        return rows;
    }

    /**
     * Update: Mark an item as Claimed/Resolved
     */
    public int markAsClaimed(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, Item.STATUS_CLAIMED);

        int rows = db.update(TABLE_ITEMS, values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    /**
     * Delete: Remove an item record by ID
     */
    public int deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_ITEMS,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    /**
     * Aggregate: Get statistical counts [total, lost, found, claimed]
     */
    public int[] getItemCounts() {
        int[] counts = new int[4]; // [total, lost, found, claimed]
        SQLiteDatabase db = this.getReadableDatabase();

        // Total
        Cursor cTotal = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ITEMS, null);
        if (cTotal != null && cTotal.moveToFirst()) {
            counts[0] = cTotal.getInt(0);
            cTotal.close();
        }

        // Lost
        Cursor cLost = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ITEMS + " WHERE " + COLUMN_TYPE + " = 'LOST'", null);
        if (cLost != null && cLost.moveToFirst()) {
            counts[1] = cLost.getInt(0);
            cLost.close();
        }

        // Found
        Cursor cFound = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ITEMS + " WHERE " + COLUMN_TYPE + " = 'FOUND'", null);
        if (cFound != null && cFound.moveToFirst()) {
            counts[2] = cFound.getInt(0);
            cFound.close();
        }

        // Claimed
        Cursor cClaimed = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ITEMS + " WHERE " + COLUMN_STATUS + " = 'CLAIMED'", null);
        if (cClaimed != null && cClaimed.moveToFirst()) {
            counts[3] = cClaimed.getInt(0);
            cClaimed.close();
        }

        db.close();
        return counts;
    }

    /**
     * Convert cursor row to Item object
     */
    private Item cursorToItem(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
        String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
        String location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION));
        String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
        String contactName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME));
        String contactPhone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_PHONE));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
        long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));

        return new Item(id, title, description, category, type, location, date, contactName, contactPhone, status, timestamp);
    }

    /**
     * Seeds initial realistic campus items if database is freshly created
     */
    public void seedInitialDataIfEmpty() {
        int[] counts = getItemCounts();
        if (counts[0] == 0) {
            Item item1 = new Item("Blue Dell 65W Laptop Charger",
                    "Left near desk row 4 during the Afternoon Database lab. Has a small black tape on the cord.",
                    "Electronics & Gadgets", "LOST", "Computer Lab 3, 2nd Floor",
                    "2026-10-14", "Aarav Sharma", "9876543210");
            insertItem(item1);

            Item item2 = new Item("Student Identity Card (Enrollment #2024CS089)",
                    "Found on the green bench outside the campus cafeteria during lunch break.",
                    "ID Cards & Documents", "FOUND", "Cafeteria Outdoor Lawn",
                    "2026-10-15", "Pooja Patel", "9823456789");
            insertItem(item2);

            Item item3 = new Item("Casio FX-991EX Scientific Calculator",
                    "Found in Seminar Hall A after the Mathematics guest lecture.",
                    "Electronics & Gadgets", "FOUND", "Seminar Hall A",
                    "2026-10-16", "Prof. R. Mehta", "9811223344");
            insertItem(item3);

            Item item4 = new Item("Black Quechua Backpack with Notebooks",
                    "Lost near Football ground pavilion. Contains DSA textbook and spiral notebooks.",
                    "Bags & Wallets", "LOST", "Sports Ground Pavilion",
                    "2026-10-16", "Karan Verma", "9765432198");
            insertItem(item4);
        }
    }
}
