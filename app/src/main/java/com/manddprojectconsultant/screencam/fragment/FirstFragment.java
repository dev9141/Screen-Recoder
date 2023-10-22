package com.manddprojectconsultant.screencam.fragment;

import android.os.Bundle;

import android.app.Fragment;;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.manddprojectconsultant.screencam.R;
public class FirstFragment extends Fragment {

    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_first, container, false);
// get the reference of Button

        return view;
    }
}
