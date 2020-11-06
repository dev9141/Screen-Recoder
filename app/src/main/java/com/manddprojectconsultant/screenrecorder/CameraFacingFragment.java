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
public class CameraFacingFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    TextView tvfront, tvrear, tvcamerafacingcancel;
    private ItemClickListenercamerafacing itemClickListenercamerafacing;


    public CameraFacingFragment(){

    }



    public static CameraFacingFragment newInstance() {
        return new CameraFacingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera_facing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {

        //facing,rear,cancel  for views
        tvfront = view.findViewById(R.id.tvfront);
        tvrear = view.findViewById(R.id.tvrear);
        tvcamerafacingcancel = view.findViewById(R.id.tvcamerafacingcancel);

        //Set On CLick
        tvcamerafacingcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tvfront.setOnClickListener(this);
        tvrear.setOnClickListener(this);
    }






    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListenercamerafacing) {
            itemClickListenercamerafacing= (ItemClickListenercamerafacing) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        itemClickListenercamerafacing = null;
    }




    @Override
    public void onClick(View view) {
        TextView tvcamerafacing = (TextView) view;
        itemClickListenercamerafacing.onItemClickcamerafacing(tvcamerafacing.getText().toString());
        dismiss();


    }

    public interface ItemClickListenercamerafacing {
        void onItemClickcamerafacing(String camerafacing);
    }



}