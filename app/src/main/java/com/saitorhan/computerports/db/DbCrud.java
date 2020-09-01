package com.saitorhan.computerports.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.saitorhan.computerports.R;

public class DbCrud {
    public SQLiteDatabase database;
    dbProcessor dbProcessor;

    public DbCrud(Context context, boolean writable) {
        int dbVersion = Integer.parseInt(context.getString(R.string.dbVersion));

        dbProcessor = new dbProcessor(context, context.getString(R.string.dbName), null, dbVersion);
        database = writable ? dbProcessor.getWritableDatabase() : dbProcessor.getReadableDatabase();
    }
}
