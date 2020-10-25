package com.mycompaney.hlag.foodtools2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsFrag extends android.support.v4.app.Fragment{
    public View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_frag, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String settings[] = {"Backup to storage"};
        ListView reciList = view.findViewById(R.id.settings_list);
        reciList.setAdapter(new SettingAdapt(getContext(), settings));
    }
}




class SettingAdapt extends ArrayAdapter{

    public SettingAdapt(@NonNull Context context, String arr[]) {
        super(context, R.layout.row_layout_settings, arr);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflator = LayoutInflater.from(getContext());
        View view = inflator.inflate(R.layout.row_layout_settings, parent, false);

        SharedPreferences sp = getContext().getSharedPreferences("settings", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        final String setting = (String) getItem(position);

        //Switch, puts bool in settingname sp
        Switch mySwitch = view.findViewById(R.id.settings_switch);
        mySwitch.setChecked(sp.getBoolean(setting, false));
        mySwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            editor.putBoolean(setting, b);
            editor.apply();
        });

        TextView textView = view.findViewById(R.id.settings__list_item_text);
        textView.setText(setting);

        return view;
    }
}
