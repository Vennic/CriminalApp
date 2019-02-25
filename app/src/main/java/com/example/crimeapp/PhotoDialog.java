package com.example.crimeapp;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

public class PhotoDialog extends DialogFragment {
    private ImageView mImageView;
    private String path;
    private static final String EXTRA_PHOTO_PATH = "extra_photo_path";

    public static PhotoDialog newInstance(String path) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_PHOTO_PATH, path);
        PhotoDialog photoDialog = new PhotoDialog();
        photoDialog.setArguments(bundle);
        return photoDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.photo_layout, null);
        path = (String) getArguments().getSerializable(EXTRA_PHOTO_PATH);
        mImageView = v.findViewById(R.id.photo_image);
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        mImageView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

    }
}
