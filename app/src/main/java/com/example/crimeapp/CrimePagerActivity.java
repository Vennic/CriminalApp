package com.example.crimeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private static final String CRIME_UUID = "android_crimeactivity_crimeID";

    public static Intent newIntent(Context context, UUID uuid) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(CRIME_UUID, uuid);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);
        UUID crimeId = (UUID) getIntent().getSerializableExtra(CRIME_UUID);

        mViewPager = findViewById(R.id.crime_view_pager);
        mCrimes = CrimeLab.get(this).getCrimes();
        final Button firstButton = findViewById(R.id.button_first);
        final Button lastButton = findViewById(R.id.button_last);

        firstButton.setOnClickListener(this);
        lastButton.setOnClickListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                Crime crime = mCrimes.get(i);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    firstButton.setEnabled(false);
                } else firstButton.setEnabled(true);

                if (i == mCrimes.size() - 1) {
                    lastButton.setEnabled(false);
                } else lastButton.setEnabled(true);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                if (i == 0)
                    firstButton.setEnabled(false);
                if (i == mCrimes.size() - 1)
                    lastButton.setEnabled(false);
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_first :
                mViewPager.setCurrentItem(0);
                break;
            case R.id.button_last :
                mViewPager.setCurrentItem(mCrimes.size() - 1);
                break;
        }
    }
}
