package com.manddprojectconsultant.screenrecorder;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
public class QualitySheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    TextView tvHD,tvMedium,tvLow,tvCancel;
    private ItemClickListenervideo mListener;


    public QualitySheetFragment() {}



    public QualitySheetFragment newInstance(){
        return new QualitySheetFragment();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quality_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof QualitySheetFragment.ItemClickListenervideo) {
            mListener = (QualitySheetFragment.ItemClickListenervideo) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    private void init(View v) {

        tvCancel=v.findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        tvHD = v.findViewById(R.id.tvHD);
        tvLow = v.findViewById(R.id.tvLow);
        tvMedium = v.findViewById(R.id.tvMedium);

        tvHD.setOnClickListener(this);
        tvLow.setOnClickListener(this);
        tvMedium.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        TextView tvvideoq = (TextView) view;
        mListener.onItemClickvideo(tvvideoq.getText().toString());
        dismiss();

    }


    public interface ItemClickListenervideo {
        void onItemClickvideo(String videoquality);
    }
}