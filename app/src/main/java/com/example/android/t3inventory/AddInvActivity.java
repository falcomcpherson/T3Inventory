package com.example.android.t3inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.t3inventory.data.InventoryContract;
import com.example.android.t3inventory.data.InventoryContract.InventoryItem;

public class AddInvActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_ITEM_LOADER = 0;

    private Uri mCurrentItemUri;

    private EditText mNameEditText;
    private EditText mManufacturerEditText;
    private EditText mSupplierEditText;
    private EditText mPhoneEditText;
    private EditText mPriceEditText;
    private EditText mQuantityEditText;
    private EditText mDetailsEditText;
    private boolean mItemHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener () {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_add_item );

        mNameEditText = (EditText) findViewById ( R.id.edit_item_name );
        mManufacturerEditText = (EditText) findViewById ( R.id.edit_manufacturer );
        mSupplierEditText = (EditText) findViewById ( R.id.edit_supplier_name );
        mPhoneEditText = (EditText) findViewById ( R.id.edit_supplier_phone );
        mQuantityEditText = (EditText) findViewById ( R.id.edit_quantity );
        mPriceEditText = (EditText) findViewById ( R.id.edit_item_price );
        mDetailsEditText = (EditText) findViewById ( R.id.edit_item_details );


        mNameEditText.setOnTouchListener ( mTouchListener );
        mManufacturerEditText.setOnTouchListener ( mTouchListener );
        mSupplierEditText.setOnTouchListener ( mTouchListener );
        mPhoneEditText.setOnTouchListener ( mTouchListener );
        mQuantityEditText.setOnTouchListener ( mTouchListener );
        mPriceEditText.setOnTouchListener ( mTouchListener );
        mDetailsEditText.setOnTouchListener ( mTouchListener );

    }

    private void insertItem() {
        String nameString = mNameEditText.getText ().toString ().trim ();
        String manufacturerString = mManufacturerEditText.getText ().toString ().trim ();
        String supplierString = mSupplierEditText.getText ().toString ().trim ();
        String phoneString = mPhoneEditText.getText ().toString ().trim ();
        String quantityString = mQuantityEditText.getText ().toString ().trim ();
        String priceString = mPriceEditText.getText ().toString ().trim ();
        String detailString = mDetailsEditText.getText ().toString ().trim ();


        if (mCurrentItemUri == null &&
                TextUtils.isEmpty ( nameString ) && TextUtils.isEmpty ( priceString ) &&
                TextUtils.isEmpty ( quantityString ) && TextUtils.isEmpty ( manufacturerString ) &&
                TextUtils.isEmpty ( supplierString ) && TextUtils.isEmpty ( phoneString ) &&
                TextUtils.isEmpty ( detailString )) {
            return;
        }

        ContentValues values = new ContentValues ();

        if (!TextUtils.isEmpty ( nameString )) {
            values.put ( InventoryItem.COLUMN_ITEM_NAME, nameString );
        } else {
                Toast.makeText ( this, getString ( R.string.insert_name_req ),
                        Toast.LENGTH_LONG ).show ();
                return;
        }

        if (!TextUtils.isEmpty ( manufacturerString )) {
            values.put ( InventoryItem.COLUMN_ITEM_MANUFACTURER, manufacturerString );
        } else {
                Toast.makeText ( this, getString ( R.string.insert_manufacturer_req ),
                        Toast.LENGTH_LONG ).show ();
                return;
        }

        if (!TextUtils.isEmpty ( supplierString )) {
            values.put ( InventoryItem.COLUMN_ITEM_SUPPLIER, supplierString );
        } else {
                Toast.makeText ( this, getString ( R.string.insert_supplier_req ),
                        Toast.LENGTH_LONG ).show ();
                return;
        }

        if (!TextUtils.isEmpty ( phoneString )) {
            values.put ( InventoryItem.COLUMN_ITEM_PHONE, phoneString );
        } else {
                Toast.makeText ( this, getString ( R.string.insert_phone_req ),
                        Toast.LENGTH_LONG ).show ();
                return;
        }

        int quantity = 0;
        if (!TextUtils.isEmpty ( quantityString )) {
            quantity = Integer.parseInt ( quantityString );
            values.put ( InventoryItem.COLUMN_ITEM_QUANTITY, quantity );
        } else {
                Toast.makeText ( this, getString ( R.string.insert_quantity_req ),
                        Toast.LENGTH_LONG ).show ();
                return;
        }


        int price = 0;
        if (!TextUtils.isEmpty ( priceString )) {
            price = Integer.parseInt ( priceString );
            values.put ( InventoryItem.COLUMN_ITEM_PRICE, price );
        } else {
                Toast.makeText ( this, getString ( R.string.insert_price_req ),
                        Toast.LENGTH_LONG ).show ();
                return;
        }

        if (!TextUtils.isEmpty ( detailString )) {
            values.put ( InventoryItem.COLUMN_ITEM_DETAILS, detailString );
        } else {
                Toast.makeText ( this, getString ( R.string.insert_details_req ),
                        Toast.LENGTH_LONG).show ();
                return;
        }

        if (mCurrentItemUri == null) {

            Uri newUri = getContentResolver ().insert ( InventoryItem.CONTENT_URI, values );
            if (newUri == null) {
                Toast.makeText ( this, getString ( R.string.editor_insert_item_failed ),
                        Toast.LENGTH_LONG ).show ();
            } else {
                Toast.makeText ( this, getString ( R.string.editor_insert_item_successful ),
                        Toast.LENGTH_SHORT ).show ();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater ().inflate ( R.menu.menu_inv_add, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId ()) {
            case R.id.action_save:
                insertItem ();
                finish ();
                return true;
            case R.id.action_cancel:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask ( AddInvActivity.this );
                    return true;
                }
                DialogInterface.OnClickListener cancelButtonClickListener =
                        new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask ( AddInvActivity.this );
                            }
                        };
                showUnsavedChangesDialog ( cancelButtonClickListener );
                return true;
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask ( AddInvActivity.this );
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask ( AddInvActivity.this );
                            }
                        };
                showUnsavedChangesDialog ( discardButtonClickListener );
                return true;
        }
        return super.onOptionsItemSelected ( item );
    }

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed ();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish ();
                    }
                };
        showUnsavedChangesDialog ( discardButtonClickListener );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryItem._ID,
                InventoryItem.COLUMN_ITEM_NAME,
                InventoryItem.COLUMN_ITEM_MANUFACTURER,
                InventoryItem.COLUMN_ITEM_SUPPLIER,
                InventoryItem.COLUMN_ITEM_PHONE,
                InventoryItem.COLUMN_ITEM_QUANTITY,
                InventoryItem.COLUMN_ITEM_PRICE};

        return new CursorLoader ( this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount () < 1) {
            return;
        }
        if (cursor.moveToFirst ()) {
            int nameColumnIndex = cursor.getColumnIndex ( InventoryContract.InventoryItem.COLUMN_ITEM_NAME );
            int manufacturerColumnIndex = cursor.getColumnIndex ( InventoryContract.InventoryItem.COLUMN_ITEM_MANUFACTURER );
            int supplierColumnIndex = cursor.getColumnIndex ( InventoryContract.InventoryItem.COLUMN_ITEM_SUPPLIER );
            int phoneColumnIndex = cursor.getColumnIndex ( InventoryContract.InventoryItem.COLUMN_ITEM_PHONE );
            int quantityColumnIndex = cursor.getColumnIndex ( InventoryContract.InventoryItem.COLUMN_ITEM_QUANTITY );
            int priceColumnIndex = cursor.getColumnIndex ( InventoryContract.InventoryItem.COLUMN_ITEM_PRICE );
            int detailColumnIndex = cursor.getColumnIndex ( InventoryContract.InventoryItem.COLUMN_ITEM_DETAILS );

            String itemName = cursor.getString ( nameColumnIndex );
            String itemManufacturer = cursor.getString ( manufacturerColumnIndex );
            String itemSupplier = cursor.getString ( supplierColumnIndex );
            String itemPhone = cursor.getString ( phoneColumnIndex );
            int itemQuantity = cursor.getInt ( quantityColumnIndex );
            int itemPrice = cursor.getInt ( priceColumnIndex );
            String itemDetails = cursor.getString ( detailColumnIndex );

            mNameEditText.setText ( itemName );
            mManufacturerEditText.setText ( itemManufacturer );
            mSupplierEditText.setText ( itemSupplier );
            mPhoneEditText.setText ( itemPhone );
            mQuantityEditText.setText ( Integer.toString ( itemQuantity ) );
            mPriceEditText.setText ( Integer.toString ( itemPrice ) );
            mDetailsEditText.setText (itemDetails);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText ( "" );
        mManufacturerEditText.setText ( "" );
        mSupplierEditText.setText ( "" );
        mPhoneEditText.setText ( "" );
        mQuantityEditText.setText ( "" );
        mPriceEditText.setText ( "" );
        mDetailsEditText.setText ( "" );
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setMessage ( R.string.unsaved_changes_dialog_msg );
        builder.setPositiveButton ( R.string.discard, discardButtonClickListener );
        builder.setNegativeButton ( R.string.keep_editing, new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss ();
                }
            }
        } );
        AlertDialog alertDialog = builder.create ();
        alertDialog.show ();
    }
}