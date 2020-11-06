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
public class OrientationFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    TextView tvlandscape, tvprotrait, tvorientationcancel;
    private ItemClickListenerorientation itemClickListenerorientation;

    public OrientationFragment() {
    }

    public static OrientationFragment newInstance() {
        return new OrientationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orientation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        //landscape,protrait,cancel button views
        tvlandscape = view.findViewById(R.id.tvlandscape);
        tvprotrait = view.findViewById(R.id.tvportrait);
        tvorientationcancel = view.findViewById(R.id.tvorientationcancel);
        tvorientationcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tvprotrait.setOnClickListener(this);
        tvlandscape.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListenerorientation) {
            itemClickListenerorientation = (ItemClickListenerorientation) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemClickListenerorientation = null;
    }

    @Override
    public void onClick(View view) {
        TextView tvorientation = (TextView) view;
        itemClickListenerorientation.onItemClickorientation(tvorientation.getText().toString());
        dismiss();
    }

    public interface ItemClickListenerorientation {
        void onItemClickorientation(String orientation);
    }
}