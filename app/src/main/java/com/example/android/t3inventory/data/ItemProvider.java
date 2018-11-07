package com.example.android.t3inventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.t3inventory.data.InventoryContract.InventoryItem;

public class ItemProvider extends ContentProvider {

    public static final String LOG_TAG = ItemProvider.class.getSimpleName ();

    private static final int ITEMS = 100;

    private static final int ITEM_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher ( UriMatcher.NO_MATCH );

    static {

        sUriMatcher.addURI ( InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS, ITEMS );
        sUriMatcher.addURI ( InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_ITEMS + "/#", ITEM_ID );
    }

    private InventoryHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryHelper ( getContext () );
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase ();

        Cursor cursor;

        int match = sUriMatcher.match ( uri );
        switch (match) {
            case ITEMS:
                cursor = database.query ( InventoryItem.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder );
                break;
            case ITEM_ID:
                selection = InventoryItem._ID + "=?";
                selectionArgs = new String[]{String.valueOf ( ContentUris.parseId ( uri ) )};

                cursor = database.query ( InventoryItem.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder );
                break;
            default:
                throw new IllegalArgumentException ( "Cannot query unknown URI " + uri );
        }

        cursor.setNotificationUri ( getContext().getContentResolver (), uri );
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match ( uri );
        switch (match) {
            case ITEMS:
                return insertItem ( uri, contentValues );
            default:
                throw new IllegalArgumentException ( "Insertion is not supported for " + uri );
        }
    }

    private Uri insertItem(Uri uri, ContentValues values) {
        String name = values.getAsString ( InventoryItem.COLUMN_ITEM_NAME );
        if (name == null) {
            throw new IllegalArgumentException ( "Item name required" );
        }

        String manufacturer = values.getAsString ( InventoryItem.COLUMN_ITEM_MANUFACTURER );
        if (manufacturer == null) {
            throw new IllegalArgumentException ( "Manufacturer name required" );
        }

        String supplier = values.getAsString ( InventoryItem.COLUMN_ITEM_SUPPLIER );
        if (supplier == null) {
            throw new IllegalArgumentException ( "Supplier name required" );
        }

        String phone = values.getAsString ( InventoryItem.COLUMN_ITEM_PHONE );
        if (phone == null) {
            throw new IllegalArgumentException ( "Phone number required" );
        }

        Integer quantity = values.getAsInteger ( InventoryItem.COLUMN_ITEM_QUANTITY );
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException ( "Quantity required" );
        }


        Integer price = values.getAsInteger ( InventoryItem.COLUMN_ITEM_PRICE );
        if (price != null && price < 0) {
            throw new IllegalArgumentException ( "Price required" );
        }


        SQLiteDatabase database = mDbHelper.getWritableDatabase ();

        long id = database.insert ( InventoryItem.TABLE_NAME, null, values );
        if (id == -1) {
            Log.e ( LOG_TAG, "Failed to insert row for " + uri );
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId ( uri, id );
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match ( uri );
        switch (match) {
            case ITEMS:
                return updateItem ( uri, contentValues, selection, selectionArgs );
            case ITEM_ID:
                selection = InventoryItem._ID + "=?";
                selectionArgs = new String[]{String.valueOf ( ContentUris.parseId ( uri ) )};
                return updateItem ( uri, contentValues, selection, selectionArgs );
            default:
                throw new IllegalArgumentException ( "Update is not supported for " + uri );
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey ( InventoryItem.COLUMN_ITEM_NAME )) {
            String name = values.getAsString ( InventoryItem.COLUMN_ITEM_NAME );
            if (name == null) {
                throw new IllegalArgumentException ( "Item name required" );
            }
        }

        if (values.containsKey ( InventoryItem.COLUMN_ITEM_MANUFACTURER )) {
            String manufacturer = values.getAsString ( InventoryItem.COLUMN_ITEM_MANUFACTURER );
            if (manufacturer == null) {
                throw new IllegalArgumentException ( "Manufacturer name required" );
            }
        }

        if (values.containsKey ( InventoryItem.COLUMN_ITEM_QUANTITY )) {
            Integer quantity = values.getAsInteger ( InventoryItem.COLUMN_ITEM_QUANTITY );
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException ( "Can not have less than 0 units" );
            }
        }

        if (values.containsKey ( InventoryItem.COLUMN_ITEM_PRICE )) {
            Integer price = values.getAsInteger ( InventoryItem.COLUMN_ITEM_PRICE );
            if (price != null && price < 0) {
                throw new IllegalArgumentException ( "Price must be a positive dollar amount" );
            }
        }

        if (values.size () == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase ();

        int rowsUpdated = database.update(InventoryItem.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase ();

        int rowsDeleted;

        final int match = sUriMatcher.match ( uri );
        switch (match) {
            case ITEMS:
                rowsDeleted = database.delete(InventoryItem.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM_ID:
                selection = InventoryItem._ID + "=?";
                selectionArgs = new String[]{String.valueOf ( ContentUris.parseId ( uri ) )};
                rowsDeleted = database.delete(InventoryItem.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException ( "Deletion is not supported for " + uri );
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match ( uri );
        switch (match) {
            case ITEMS:
                return InventoryItem.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return InventoryItem.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException ( "Unknown URI " + uri + " with match " + match );
        }
    }
}