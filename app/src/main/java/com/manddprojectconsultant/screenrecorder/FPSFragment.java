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
public class FPSFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    TextView tv60, tv40, tv30, tv25, tv15, tv5, tvcan;
    private ItemClickListenerfps itemClickListenerfps;

    public FPSFragment() {
    }

    public FPSFragment newInstance() {
        return new FPSFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_f_p_s, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        tvcan = view.findViewById(R.id.tvCan);
        tvcan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tv60 = view.findViewById(R.id.tv60);
        tv40 = view.findViewById(R.id.tv40);
        tv30 = view.findViewById(R.id.tv30);
        tv25 = view.findViewById(R.id.tv25);
        tv15 = view.findViewById(R.id.tv15);
        tv5 = view.findViewById(R.id.tv5);
        tv60.setOnClickListener(this);
        tv40.setOnClickListener(this);
        tv30.setOnClickListener(this);
        tv25.setOnClickListener(this);
        tv15.setOnClickListener(this);
        tv5.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FPSFragment.ItemClickListenerfps) {
            itemClickListenerfps = (FPSFragment.ItemClickListenerfps) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        itemClickListenerfps = null;
    }

    @Override
    public void onClick(View view) {
        TextView tvfps = (TextView) view;
        itemClickListenerfps.onItemClickfps(tvfps.getText().toString());
        dismiss();
    }

    public interface ItemClickListenerfps {
        void onItemClickfps(String fps);
    }
}