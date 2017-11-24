package edu.ualr.cpsc4367.crimerecords.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.ualr.cpsc4367.crimerecords.database.CrimeDbSchema.CrimeTable;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "CrimeBaseHelper";
    private static final int VERSION = 2;
    private static final String DATABASE_NAME = "crimeBase.db";

    private static final String CREATE_CRIME_TBL = "create table "
            + CrimeTable.NAME
            + "("
            + CrimeTable.COLUMN_ID + " integer primary key autoincrement, "
            + CrimeTable.COLUMN_UUID + ", "
            + CrimeTable.COLUMN_TITLE + ", "
            + CrimeTable.COLUMN_DATE + ", "
            + CrimeTable.COLUMN_SOLVED + ", "
            + CrimeTable.COLUMN_SUSPECT
            + ");";

    /*
     * Instantiates an open helper for the provider's SQLite data repository
     * Do not do database creation and upgrade here.
     */
    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /*
     * Creates the data repository. This is called when the provider attempts to open the
     * repository and SQLite reports that it doesn't exist.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the main table
        db.execSQL(CREATE_CRIME_TBL);

        Log.println(Log.ASSERT, TAG, "Database table " + CrimeTable.NAME + "was created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.println(Log.ASSERT, TAG, "Upgrading database from version" + oldVersion
                + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + CrimeTable.NAME);
        onCreate(db);
    }
}
