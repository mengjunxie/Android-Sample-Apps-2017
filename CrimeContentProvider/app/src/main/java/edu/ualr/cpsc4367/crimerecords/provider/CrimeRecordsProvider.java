package edu.ualr.cpsc4367.crimerecords.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import edu.ualr.cpsc4367.crimerecords.database.CrimeBaseHelper;
import edu.ualr.cpsc4367.crimerecords.database.CrimeDbSchema;

public class CrimeRecordsProvider extends ContentProvider {

    // The handler to the database helper object
    private CrimeBaseHelper mOpenHelper;

    // Code for UriMatcher
    private static final int QUERY_TBL_CODE = 1;
    private static final int QUERY_ROW_CODE = 2;

    private static final String AUTHORITY = "edu.ualr.cpsc4367.crimerecords.provider";
    private static final String BASE_PATH = CrimeDbSchema.CrimeTable.NAME;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/crimerecords";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/crimerecords";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, QUERY_TBL_CODE);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", QUERY_ROW_CODE);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new CrimeBaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Using SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Set the table for querying
        queryBuilder.setTables(CrimeDbSchema.CrimeTable.NAME);

        switch (sURIMatcher.match(uri)) {
            case QUERY_TBL_CODE:
                break;
            case QUERY_ROW_CODE:
                queryBuilder.appendWhere(CrimeDbSchema.CrimeTable.COLUMN_ID
                        + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        try {
            Cursor cr = queryBuilder.query(db, projection, selection, selectionArgs,
                    null, // groupBy
                    null, // having
                    sortOrder
            );

            cr.setNotificationUri(getContext().getContentResolver(), uri);

            return cr;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("query error!");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted = 0;

        switch (sURIMatcher.match(uri)) {
            case QUERY_TBL_CODE:
                rowsDeleted = db.delete(CrimeDbSchema.CrimeTable.NAME,
                        selection, selectionArgs);
                break;
            case QUERY_ROW_CODE:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(CrimeDbSchema.CrimeTable.NAME,
                            CrimeDbSchema.CrimeTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = db.delete(CrimeDbSchema.CrimeTable.NAME,
                            CrimeDbSchema.CrimeTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long id = 0;

        switch (sURIMatcher.match(uri)) {
            case QUERY_TBL_CODE:
                id = db.insert(CrimeDbSchema.CrimeTable.NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated = 0;

        switch (sURIMatcher.match(uri)) {
            case QUERY_TBL_CODE:
                rowsUpdated = db.update(CrimeDbSchema.CrimeTable.NAME,
                        values, selection, selectionArgs);
                break;
            case QUERY_ROW_CODE:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(CrimeDbSchema.CrimeTable.NAME,
                            values,
                            CrimeDbSchema.CrimeTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = db.update(CrimeDbSchema.CrimeTable.NAME,
                            values,
                            CrimeDbSchema.CrimeTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
