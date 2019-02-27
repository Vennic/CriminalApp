package com.example.crimeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mContactButton;
    private Button mCallButton;
    private ImageButton mImageButton;
    private ImageView mImageView;
    private File mPhotoFile;
    private int width;
    private int height;
    private Callbacks mCallbacks;
    private static final String CRIME_ID = "android_crime_fragment_ID";
    private static final String DIALOG_ID = "android_crime_dialogFragment_ID";
    private final static int REQUEST_DATE = 0;
    private final static int REQUEST_TIME = 1;
    private final static int REQUEST_CONTACT = 2;
    private final static int REQUEST_CAMERA = 3;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }


    public static CrimeFragment newInstance(UUID id) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_ID, id);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    private String getCrimeReport() {
        String solvedString;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = mCrime.getStringDate();

        String suspect = mCrime.getSuspect();

        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        return getString(R.string.crime_report, mCrime.getTitle(), dateFormat, solvedString, suspect);

    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mImageView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), width, height);
            mImageView.setImageBitmap(bitmap);
        }
    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)  {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.PICKER_EXTRA_DATE);

            mCrime.setDate(date);
            updateCrime();
            mDateButton.setText(mCrime.getStringDate());
        } else if (requestCode == REQUEST_CAMERA) {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.android.crimeapp.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhotoView();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();

            String[] queryFields = new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

            try (Cursor cursor = getActivity().getContentResolver().query(
                    contactUri,
                    queryFields,
                    null,
                    null,
                    null)) {

                if (cursor.getCount() == 0) {
                    return;
                }

                cursor.moveToFirst();
                String suspect = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mCrime.setSuspect(suspect);
                updateCrime();
                mCrime.setSuspectPhone(phoneNumber);
                mContactButton.setText(suspect);
            }
        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date) data
                    .getSerializableExtra(TimePickerFragment.PICKER_EXTRA_TIME);
            mCrime.setDate(date);
            updateCrime();
            mTimeButton.setText(new SimpleDateFormat("HH : mm", Locale.getDefault()).format(date));
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setHasOptionsMenu(true);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_crime) {
            CrimeLab crimeLab = CrimeLab.get(getActivity());
            crimeLab.removeCrime(mCrime);
            Intent intent = new Intent(getActivity(), CrimeListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
            Toast.makeText(getActivity(), "Crime removed", Toast.LENGTH_SHORT).show();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mDateButton = v.findViewById(R.id.crime_date);
        mTimeButton = v.findViewById(R.id.time_button);
        mDateButton.setText(mCrime.getStringDate());
        mTimeButton.setText(new SimpleDateFormat("HH : mm", Locale.getDefault()).format(mCrime.getDate()));

        mImageView = v.findViewById(R.id.crime_photo);
        mImageButton = v.findViewById(R.id.crime_camera);

        ViewTreeObserver vo = mImageView.getViewTreeObserver();
        vo.addOnGlobalLayoutListener(() -> {
            width = mImageView.getWidth();
            height = mImageView.getHeight();
        });

        mImageView.setOnClickListener(v1 -> {
            if (mPhotoFile == null || !mPhotoFile.exists()) {
                Toast.makeText(getActivity(), "No photo.", Toast.LENGTH_SHORT).show();
            } else {
                FragmentManager manager = getFragmentManager();
                PhotoDialog photoDialog = PhotoDialog.newInstance(mPhotoFile.getPath());
                photoDialog.show(manager, DIALOG_ID);
                Toast.makeText(getActivity(), "height = " + height + " : width = " + width,
                        Toast.LENGTH_LONG).show();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mContactButton.setEnabled(false);
        }

        mDateButton.setOnClickListener(v12 -> {
            FragmentManager manager = getFragmentManager();
            DatePickerFragment datePickerFragment = InstanceOfPicker.newInstance(mCrime.getDate(), new DatePickerFragment());
            datePickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
            datePickerFragment.show(manager, DIALOG_ID);
        });

        mTimeButton.setOnClickListener(v1 -> {
            FragmentManager fragmentManager = getFragmentManager();
            TimePickerFragment pickerFragment = InstanceOfPicker.newInstance(mCrime.getDate(), new TimePickerFragment());
            pickerFragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
            pickerFragment.show(fragmentManager, DIALOG_ID);
        });

        mReportButton = v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(v1 -> {
            ShareCompat.IntentBuilder.from(getActivity())
                    .setType("text/plain")
                    .setText(getCrimeReport())
                    .setSubject(getString(R.string.crime_report_subject))
                    .setChooserTitle(R.string.send_report)
                    .startChooser();
        });

        mCallButton = v.findViewById(R.id.call_button);
        mCallButton.setOnClickListener(v1 -> {
            if (mCrime.getSuspectPhone() != null) {
                Uri uri = Uri.parse("tel:" + mCrime.getSuspectPhone());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "Suspect hasn't phone", Toast.LENGTH_SHORT).show();
            }
        });

        mContactButton = v.findViewById(R.id.crime_suspect);
        mContactButton.setOnClickListener(v1 -> {
            startActivityForResult(pickContact, REQUEST_CONTACT);
        });

        if (mCrime.getSuspect() != null) {
            mContactButton.setText(mCrime.getSuspect());
        }

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mImageButton.setEnabled(canTakePhoto);

        mImageButton.setOnClickListener(v1 -> {
            Uri uri = FileProvider
                    .getUriForFile(getActivity(),"com.example.android.crimeapp.fileprovider", mPhotoFile);

            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            List<ResolveInfo> cameraActivities = getActivity()
                    .getPackageManager()
                    .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo activities : cameraActivities) {
                getActivity().grantUriPermission(activities.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            startActivityForResult(captureImage, REQUEST_CAMERA);

        });

        CheckBox mSeriousCheckBox = v.findViewById(R.id.serious_crime_check);
        mSeriousCheckBox.setChecked(mCrime.isRequiresPolice());

        mSeriousCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> mCrime.setRequiresPolice(isChecked));

        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mCrime.setSolved(isChecked);
            updateCrime();
        });

        mTitleField = v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        updatePhotoView();
        return v;
    }

}
