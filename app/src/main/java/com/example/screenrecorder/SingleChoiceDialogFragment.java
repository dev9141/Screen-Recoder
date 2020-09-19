package com.example.screenrecorder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Arrays;

public class SingleChoiceDialogFragment extends DialogFragment {

    int position=0;  //default posotion
    String[] list={};
    String title="";
    String settingName="";
    String cutterntTvValue = "";

    public SingleChoiceDialogFragment(String[] list, String title, String cutterntTvValue) {
        this.list = list;
        this.title = title;
        this.settingName = title;
        this.cutterntTvValue = cutterntTvValue;
    }

    public interface SingleChoiceListner{
        void onPositiveButtonClicked(String[] list, int position, String settingName);
        void onNagativeButtonClicked();
    }
     SingleChoiceListner mListner;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListner= (SingleChoiceListner) context;
        } catch (Exception e) {
            throw new ClassCastException(getActivity().toString()+"SingleChoiceListner must be implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String aa = SPVariables.getString(title, getContext());
        int pos = Arrays.asList(list).indexOf(aa);
        builder.setTitle(title)
                .setSingleChoiceItems(list, pos, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        position=i;
                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListner.onPositiveButtonClicked(list,position,settingName);

//                        Toast.makeText(getContext(), list[position]+" Selected", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListner.onNagativeButtonClicked();

                    }
                });
        return builder.create();
//        return super.onCreateDialog(savedInstanceState);
    }
}
