package com.example.android.t3inventory;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.t3inventory.data.InventoryContract;

public class ItemCursorAdapter extends CursorAdapter {

    private Object context;
    private Button unitSoldBtn;
    private Cursor mCursor;

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 );
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context,Cursor cursor) {
        this.mCursor = cursor;

        TextView nameTextView = (TextView) view.findViewById(R.id.item_name);
        TextView manufacturerTextView = (TextView) view.findViewById(R.id.item_manufacturer);
        TextView quantityTextView = (TextView) view.findViewById(R.id.item_quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.item_price);

        int nameColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryItem.COLUMN_ITEM_NAME);
        int manufacturerColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryItem.COLUMN_ITEM_MANUFACTURER);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryItem.COLUMN_ITEM_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryContract.InventoryItem.COLUMN_ITEM_PRICE);

        String itemName = cursor.getString(nameColumnIndex);
        String itemManufacturer = cursor.getString(manufacturerColumnIndex);
        String itemQuantity = cursor.getString(quantityColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);

        nameTextView.setText(itemName);
        manufacturerTextView.setText(itemManufacturer);
        quantityTextView.setText(itemQuantity);
        priceTextView.setText(itemPrice);

        unitSoldBtn = view.findViewById ( R.id.unit_sold_button );
        unitSoldBtn.setOnClickListener ( new View.OnClickListener (){
            @Override
            public void onClick(View view) {
                int columnIdIndex = mCursor.getColumnIndex(InventoryContract.InventoryItem._ID);
                int quantityIndex = mCursor.getColumnIndex(InventoryContract.InventoryItem.COLUMN_ITEM_QUANTITY);
                String column= mCursor.getString(columnIdIndex);
                String quant= mCursor.getString(quantityIndex);
                ViewInventoryActivity viewInventoryActivity = (ViewInventoryActivity) context;
                viewInventoryActivity.decreaseCount( Integer.valueOf(column), Integer.valueOf(quant));
            }
        } );


        }
    }