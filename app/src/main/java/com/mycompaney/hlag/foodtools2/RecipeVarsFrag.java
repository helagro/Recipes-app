package com.mycompaney.hlag.foodtools2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DecimalFormat;

public class RecipeVarsFrag extends android.support.v4.app.Fragment {

    EditText prtEdit;
    ToggleButton editBtn;
    SharedPreferences sp;
    SharedPreferences.Editor editor;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_vars_frag, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Toast toast = Toast.makeText(getContext(), "Invalid portion number, old number used", Toast.LENGTH_SHORT);
        sp = getActivity().getSharedPreferences("recipePrefs", Activity.MODE_PRIVATE);
        editor = sp.edit();

        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);
        format.setGroupingUsed(false);

        ((RecipeActivity)getActivity()).onFragmentCreated();

        prtEdit = view.findViewById(R.id.prt);
        prtEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                double prtDoub;
                try{ prtDoub = Double.parseDouble(prtEdit.getText().toString());}catch (NumberFormatException e){
                    toast.show();
                    Log.w("tag", "afterTextChanged: " + prtEdit.getText().toString());
                    return;}
                editor.putLong("prt", Double.doubleToLongBits(prtDoub));
                editor.apply();
                ((RecipeActivity) getActivity()).prtDoub = prtDoub;
                if(editBtn.isChecked()){return;}
                ((RecipeActivity) getActivity()).updateContent();
            }
        });


        editBtn = view.findViewById(R.id.editBtn);
        editBtn.setOnClickListener(view1 -> {
            boolean edit = editBtn.isChecked();
            ((RecipeActivity) getActivity()).setEdit(edit);
            if(!edit){
                ((RecipeActivity) getActivity()).save();
                ((RecipeActivity) getActivity()).saveLock = true;
                ((RecipeActivity) getActivity()).updateContent();
            }else {
                ((RecipeActivity) getActivity()).saveLock = false;
                ((RecipeActivity) getActivity()).editContent();
            }

        });

    }

}
