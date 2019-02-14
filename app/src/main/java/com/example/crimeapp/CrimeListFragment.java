package com.example.crimeapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;
    private int ITEM_ID;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("MyLog", "onCreateView"); //LOGGING
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI(){
        Log.i("MyLog", "updateUI"); //LOGGING
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        //mCrimeAdapter = new CrimeAdapter(crimes);
        //mCrimeRecyclerView.setAdapter(mCrimeAdapter);

        if (mCrimeAdapter == null) {
            mCrimeAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
        } else {
            mCrimeAdapter.notifyItemChanged(ITEM_ID);
        }
    }

    //Holder
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent, int itemId) {
            super(inflater.inflate(itemId, parent, false));
            Log.i("MyLog", "CrimeHolder constructor"); //LOGGING
            if (itemId == R.layout.list_item_crime) {
                mTitleTextView = itemView.findViewById(R.id.crime_title);
                mDateTextView = itemView.findViewById(R.id.crime_date);
                mSolvedImageView = itemView.findViewById(R.id.crime_solved);
                itemView.setOnClickListener(this);
            } else if (itemId == R.layout.list_item_seriouscrime) {
                mTitleTextView = itemView.findViewById(R.id.crime_title_police);
                mDateTextView = itemView.findViewById(R.id.crime_date_police);
                mSolvedImageView = itemView.findViewById(R.id.crime_solved);
                itemView.setOnClickListener(this);
                Button button = itemView.findViewById(R.id.police_button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "Police is coming for " + mCrime.getTitle(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
            }
        }

        public void bind(Crime crime) {
            Log.i("MyLog", "Bind method");  //LOGGING
            mCrime = crime;
            mTitleTextView.setText(crime.getTitle());
            mDateTextView.setText(crime.getStringDate());
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            ITEM_ID = getAdapterPosition();
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);
        }
    }


    //Adapter
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;
        private final int TYPE_CRIME_ONE = 1;
        private final int TYPE_CRIME_TWO = 2;

        public CrimeAdapter(List<Crime> crimes) {
            Log.i("MyLog", "CrimeAdapter constructor");  //LOGGING
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.i("MyLog", "onCreateViewHolder");  //LOGGING
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            //Выбор холдера в зависимости от типа
            return viewType == TYPE_CRIME_ONE ? new CrimeHolder(layoutInflater, parent, R.layout.list_item_seriouscrime) : new CrimeHolder(layoutInflater, parent, R.layout.list_item_crime);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int i) {
            Log.i("MyLog", "onBindViewHolder " + i);  //LOGGING
            Crime crime = mCrimes.get(i);
            crimeHolder.bind(crime);
        }

        @Override
        public int getItemCount() {
            Log.i("MyLog", "getItemCount");  //LOGGING
            return mCrimes.size();
        }

        @Override
        public int getItemViewType(int position) {
            Log.i("MyLog", "getItemViewTipe");  //LOGGING
            if (mCrimes.get(position).isRequiresPolice()) {
                return TYPE_CRIME_ONE;
            } else
                return TYPE_CRIME_TWO;
        }
    }
}
