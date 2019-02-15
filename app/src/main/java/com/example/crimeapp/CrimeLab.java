package com.example.crimeapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static  CrimeLab sCrimelab;
    private List<Crime> mCrimes;

    public static CrimeLab get(Context context) {
        if (sCrimelab == null) {
            sCrimelab = new CrimeLab(context);
        }
        return sCrimelab;
    }

    public void addCrime(Crime crime) {
        mCrimes.add(crime);
    }

    public void removeCrime(Crime crime) {
        mCrimes.remove(crime);
    }

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
    }

    public List<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        return null;
    }
}
