package com.manddprojectconsultant.screencam;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
public class ResolutionSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener {
    TextView tv1080p, tv720p, tv480p, tv240p, tv144p,cancel;
    private ItemClickListener mListener;

    public ResolutionSheetDialog(){}

    public static ResolutionSheetDialog newInstance() {
        return new ResolutionSheetDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme);
        return inflater.inflate(R.layout.fragment_resolution_sheet_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        itemclick();
    }

    private void itemclick() {
        tv1080p.setOnClickListener(this);
        tv720p.setOnClickListener(this);
        tv480p.setOnClickListener(this);
        tv240p.setOnClickListener(this);
        tv144p.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ItemClickListener) {
            mListener = (ItemClickListener) context;
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

    private void init(View view) {
        tv1080p = view.findViewById(R.id.tv1080P);
        tv720p = view.findViewById(R.id.tv720P);
        tv480p = view.findViewById(R.id.tv480P);
        tv240p = view.findViewById(R.id.tv240p);
        tv144p = view.findViewById(R.id.tv144p);

        cancel=view.findViewById(R.id.button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


    }

    @Override
    public void onClick(View view) {
        TextView tvresolutionnumber = (TextView) view;
        mListener.onItemClick(tvresolutionnumber.getText().toString());
        dismiss();
    }

    public interface ItemClickListener {
        void onItemClick(String item);
    }
}