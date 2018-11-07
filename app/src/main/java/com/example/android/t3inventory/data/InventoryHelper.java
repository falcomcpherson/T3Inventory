package com.example.android.t3inventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.t3inventory.data.InventoryContract.InventoryItem;

public class InventoryHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "items.db";

    private static final int DATABASE_VERSION = 1;

    public InventoryHelper(Context context) {
        super ( context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ITEMS_TABLE =  "CREATE TABLE " + InventoryItem.TABLE_NAME + " ("
                + InventoryItem._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + InventoryItem.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + InventoryItem.COLUMN_ITEM_MANUFACTURER + " TEXT NOT NULL, "
                + InventoryItem.COLUMN_ITEM_SUPPLIER + " TEXT NOT NULL, "
                + InventoryItem.COLUMN_ITEM_PHONE + " TEXT NOT NULL, "
                + InventoryItem.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL, "
                + InventoryItem.COLUMN_ITEM_PRICE + " INTEGER NOT NULL, "
                + InventoryItem.COLUMN_ITEM_DETAILS + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ InventoryItem.TABLE_NAME);
    }


}
