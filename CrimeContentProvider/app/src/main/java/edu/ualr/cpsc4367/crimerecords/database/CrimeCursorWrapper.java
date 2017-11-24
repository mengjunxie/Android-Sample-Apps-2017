package edu.ualr.cpsc4367.crimerecords.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import edu.ualr.cpsc4367.crimerecords.Crime;

import java.util.Date;
import java.util.UUID;

import edu.ualr.cpsc4367.crimerecords.database.CrimeDbSchema.CrimeTable;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeTable.COLUMN_UUID));
        String title = getString(getColumnIndex(CrimeTable.COLUMN_TITLE));
        long date = getLong(getColumnIndex(CrimeTable.COLUMN_DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.COLUMN_SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.COLUMN_SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);

        return crime;
    }
}
