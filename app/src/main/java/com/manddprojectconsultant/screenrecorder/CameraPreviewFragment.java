package com.manddprojectconsultant.screenrecorder;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
public class CameraPreviewFragment extends BottomSheetDialogFragment implements View.OnClickListener {


   /* public CameraFacingFragment()
    {

    }*/
    TextView tvlarge, tvmedium, tvsmall, tvcamerapreviewcancel;
    private ItemClickListenercamerapreview itemClickListenercamerapreview;

    public static CameraPreviewFragment newInstance() {
        return new CameraPreviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        //large,small,medium view
        tvlarge = view.findViewById(R.id.tvlarge);
        tvmedium = view.findViewById(R.id.tvmedium);
        tvsmall = view.findViewById(R.id.tvsmall);
        tvcamerapreviewcancel = view.findViewById(R.id.tvcamerapreviewcancel);
        tvcamerapreviewcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tvsmall.setOnClickListener(this);
        tvmedium.setOnClickListener(this);
        tvlarge.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListenercamerapreview) {
            itemClickListenercamerapreview = (ItemClickListenercamerapreview) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemClickListenercamerapreview = null;
    }

    @Override
    public void onClick(View view) {
        TextView tvcamerapreview = (TextView) view;
        itemClickListenercamerapreview.onItemClickcamerapreview(tvcamerapreview.getText().toString());
        dismiss();
    }

    public interface ItemClickListenercamerapreview {
        void onItemClickcamerapreview(String camerapreview);
    }
}