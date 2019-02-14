package com.example.crimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Date;

public class InstanceOfPicker extends DialogFragment{
    public static final String DATE_EXTRA_INSTANCE = "android_crimeApp_extraDateInstance";
    public static final String PICKER_EXTRA_DATE = "android_crimeApp_pickerExtraDate";
    public static final String PICKER_EXTRA_TIME = "android_crimeApp_pickerExtraTime";

    public static <T extends DialogFragment>  T newInstance(Date date, T instanceOfDialogF) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DATE_EXTRA_INSTANCE, date);
        instanceOfDialogF.setArguments(bundle);
        return instanceOfDialogF;
    }

    protected void sendResult(int requestCode, Date date, String extraId) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(extraId, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), requestCode, intent);
    }
}
