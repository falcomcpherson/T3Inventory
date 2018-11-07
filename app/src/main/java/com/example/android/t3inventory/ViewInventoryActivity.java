package com.example.android.t3inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.t3inventory.data.InventoryContract;
import com.example.android.t3inventory.data.InventoryContract.InventoryItem;

public class ViewInventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ITEM_LOADER = 0;

    ItemCursorAdapter mCursorAdapter;
    private DrawerLayout mDrawerLayout;
    private Uri mCurrentItemUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);

        mDrawerLayout = findViewById ( R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case android.R.id.home:
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.help_menu:
                                Intent helpIntent = new Intent (ViewInventoryActivity.this, HelpActivity.class);
                                mDrawerLayout.closeDrawers();
                                startActivity(helpIntent);
                                return true;
                            case R.id.add_item:
                                Intent addIntent = new Intent (ViewInventoryActivity.this, AddInvActivity.class);
                                mDrawerLayout.closeDrawers();
                                startActivity(addIntent);
                                return true;
                            case R.id.test_item:
                                insertItem ();
                                mDrawerLayout.closeDrawers ();
                                return true;
                            case R.id.clear_inventory:
                                deleteAllItems ();
                                mDrawerLayout.closeDrawers();
                                return true;
                            default:
                                menuItem.setChecked(false);
                                mDrawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                        }
                    }
                });

        ListView itemListView = (ListView) findViewById(R.id.list_item);

        mCursorAdapter = new ItemCursorAdapter ( this, null );
        itemListView.setAdapter ( mCursorAdapter );

        itemListView.setOnItemClickListener( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(ViewInventoryActivity.this, EditInvActivity.class);

                Uri currentItemUri = ContentUris.withAppendedId (InventoryItem.CONTENT_URI, id);

                intent.setData(currentItemUri);

                startActivity(intent);

            }
        } );

        itemListView.setOnItemLongClickListener ( new AdapterView.OnItemLongClickListener () {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {

                Uri currentItemUri = ContentUris.withAppendedId (InventoryItem.CONTENT_URI, id);
                mCurrentItemUri = currentItemUri;
                showDeleteConfirmationDialog ();
                return true;
            }});

        getLoaderManager().initLoader ( ITEM_LOADER, null, this );
    }

    private void insertItem() {
        ContentValues values = new ContentValues();
        values.put(InventoryItem.COLUMN_ITEM_NAME, "GXP 2170");
        values.put(InventoryItem.COLUMN_ITEM_MANUFACTURER, "Grandstream");
        values.put(InventoryItem.COLUMN_ITEM_SUPPLIER, "Teledynamics");
        values.put(InventoryItem.COLUMN_ITEM_PHONE, "2398675309");
        values.put(InventoryItem.COLUMN_ITEM_QUANTITY, "5");
        values.put(InventoryItem.COLUMN_ITEM_PRICE, "10");
        values.put(InventoryItem.COLUMN_ITEM_DETAILS, "This is a test object");

        Uri newUri = getContentResolver().insert(InventoryItem.CONTENT_URI, values);
    }


    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(InventoryItem.CONTENT_URI, null, null);
        Log.v("ViewInventoryActivity", rowsDeleted + " rows deleted from Inventory");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver ().delete ( mCurrentItemUri, null, null );
            if (rowsDeleted == 0) {
                Toast.makeText ( this, getString ( R.string.editor_delete_item_failed ),
                        Toast.LENGTH_SHORT ).show ();
            } else {
                Toast.makeText ( this, getString ( R.string.editor_delete_item_successful ),
                        Toast.LENGTH_SHORT ).show ();
            }
        }
    }

    public void decreaseCount(int columnId, int quantity){

        quantity = quantity -1;
        if (quantity < 0) {
            quantity = 0;
        }
        Toast.makeText ( this, "Item sold from position: " + String.valueOf ( columnId ), Toast.LENGTH_LONG ).show();

        ContentValues values = new ContentValues();
        values.put(InventoryContract.InventoryItem.COLUMN_ITEM_QUANTITY, quantity);

        Uri updateUri = ContentUris.withAppendedId(InventoryContract.InventoryItem.CONTENT_URI, columnId);

        int rowsAffected = getContentResolver().update(updateUri, values,null, null);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryItem._ID,
                InventoryItem.COLUMN_ITEM_NAME,
                InventoryItem.COLUMN_ITEM_MANUFACTURER,
                InventoryItem.COLUMN_ITEM_QUANTITY,
                InventoryItem.COLUMN_ITEM_PRICE};

        return new CursorLoader (this,
                InventoryItem.CONTENT_URI,   // The content URI of the words table
                projection,             // The columns to return for each row
                null,                   // Selection criteria
                null,                   // Selection criteria
                null);                  // The sort order for the returned rows
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor (data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor ( null );

    }
}