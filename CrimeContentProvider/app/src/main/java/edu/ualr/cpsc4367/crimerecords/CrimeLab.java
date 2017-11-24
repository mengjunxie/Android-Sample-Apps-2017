package edu.ualr.cpsc4367.crimerecords;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.ualr.cpsc4367.crimerecords.database.CrimeCursorWrapper;
import edu.ualr.cpsc4367.crimerecords.database.CrimeDbSchema.CrimeTable;
import edu.ualr.cpsc4367.crimerecords.provider.CrimeRecordsProvider;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;
    private ContentResolver mResolver;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
//        mDatabase = new CrimeBaseHelper(mContext)
//                .getWritableDatabase();
        mResolver = context.getContentResolver();
    }


    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);

        //mDatabase.insert(CrimeTable.NAME, null, values);
        mResolver.insert(CrimeRecordsProvider.CONTENT_URI, values);
    }

    public List<Crime> getCrimes() {
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = queryCrimes(null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
            cursor.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.COLUMN_UUID + " = ?",
                new String[]{id.toString()}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Crime crime) {
        File externalFilesDir = mContext
                .getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, crime.getPhotoFilename());
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

//        mDatabase.update(CrimeTable.NAME, values,
//                CrimeTable.Cols.UUID + " = ?",
//                new String[] { uuidString });
        mResolver.update(CrimeRecordsProvider.CONTENT_URI,
                values,
                CrimeTable.COLUMN_UUID + " = ?",
                new String[]{uuidString});
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.COLUMN_UUID, crime.getId().toString());
        values.put(CrimeTable.COLUMN_TITLE, crime.getTitle());
        values.put(CrimeTable.COLUMN_DATE, crime.getDate().getTime());
        values.put(CrimeTable.COLUMN_SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.COLUMN_SUSPECT, crime.getSuspect());

        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
/*        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );
*/
        Cursor cursor = mResolver.query(CrimeRecordsProvider.CONTENT_URI,
                null, whereClause, whereArgs, null);

        return new CrimeCursorWrapper(cursor);
    }
}
