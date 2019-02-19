package com.example.crimeapp.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.crimeapp.Crime;
import com.example.crimeapp.database.CrimedbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuid = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        int isReqPolice = getInt(getColumnIndex(CrimeTable.Cols.REQ_POLICE));

        Crime crime = new Crime(UUID.fromString(uuid));
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setRequiresPolice(isReqPolice != 0);
        crime.setTitle(title);

        return crime;
    }
}
