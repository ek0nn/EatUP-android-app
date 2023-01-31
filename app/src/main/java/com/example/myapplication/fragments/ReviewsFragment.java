package com.example.myapplication.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.myapplication.R;
import com.example.myapplication.activities.MainActivity;
import com.example.myapplication.activities.ReviewActivity;
import com.google.android.material.button.MaterialButton;

public class ReviewsFragment extends Fragment {
    public static final int REQUEST_CODE_ADD_NOTE = 100;
    //for add button to work
    MainActivity mainActivity;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    public static ReviewsFragment newInstance(String param1, String param2) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        MaterialButton b1 = view.findViewById(R.id.viewNotes);
        //ImageView b2 = view.findViewById(R.id.AddReviewMain);
        mainActivity=(MainActivity)getActivity();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clicking the plus button takes u to add page

                Intent intent = new Intent(getActivity(), ReviewActivity.class);
                startActivity(intent);
            }

           });

        ImageView b2 = view.findViewById(R.id.AddReviewMain);
        mainActivity=(MainActivity)getActivity();
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clicking the plus button takes u to add page

                Intent intent = new Intent(getActivity(), ReviewActivity.class);
                startActivity(intent);
            }

        });

        return view;




    }


}