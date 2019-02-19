package com.example.crimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class CrimeListActivity extends AppCompatActivity {

    Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        addButton = findViewById(R.id.add_button);
        if (CrimeLab.get(this).getCrimes().size() == 0) {
            addButton.setVisibility(View.VISIBLE);

            addButton.setOnClickListener(v -> {
                Crime crime = new Crime();
                CrimeLab.get(CrimeListActivity.this).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(CrimeListActivity.this, crime.getId());
                startActivity(intent);
            });
        } else {
            addButton.setVisibility(View.INVISIBLE);
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new CrimeListFragment();
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
}
