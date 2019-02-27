package com.example.crimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class CrimeListActivity extends AppCompatActivity implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    Button addButton;


    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        //Log.i("TWO", "onCreate: listActiv");
        addButton = findViewById(R.id.add_button);
        if (CrimeLab.get(this).getCrimes().size() == 0) {
            addButton.setVisibility(View.VISIBLE);
            showButton();
        } else {
            addButton.setVisibility(View.INVISIBLE);
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CrimeLab.get(this).getCrimes().size() == 0) {
            addButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }

    }

    private void showButton() {
        addButton.setOnClickListener(v -> {
            Crime crime = new Crime();
            CrimeLab.get(CrimeListActivity.this).addCrime(crime);
            onCrimeSelected(crime);
        });
    }

    @Override
    public void onCrimeDeleted() {
        if (CrimeLab.get(this).getCrimes().size() == 0) {
            addButton.setVisibility(View.VISIBLE);
            showButton();
        } else {
            addButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment crimeListFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        crimeListFragment.updateUI();
    }
}
