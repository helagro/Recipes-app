package com.mycompaney.hlag.foodtools2;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class DirDialog extends AppCompatDialogFragment {

    Button select, cancel;
    File baseDir = new File("/");
    File thisDir;
    File oldDir = baseDir;
    ArrayAdapter<File> dirAdapt;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //dialog stuff
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.dir_chooser_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);


        //adapter to put style in list elements only!
        ArrayList<File> files = new ArrayList<>();
        files.add(baseDir);
        dirAdapt = new ArrayAdapter<File>(Objects.requireNonNull(getContext()), R.layout.row_layout_dir, files) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflator = LayoutInflater.from(getContext());
                View view = inflator.inflate(R.layout.row_layout_dir, parent, false);
                File file = getItem(position);
                if (file != null) {
                    ((TextView)view.findViewById(R.id.dir_row_text)).setText(file.getName());
                }
                return view;
            }
        };

        //setup listview
        ListView lstView = view.findViewById(R.id.dir_listview);
        listView.setAdapter(dirAdapt);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            final TextView textView = view.findViewById(R.id.dir_textview);
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                File file = (File) adapterView.getItemAtPosition(i);
                textView.setText(file.getName());
                dirAdapt.clear();
                try {
                    dirAdapt.addAll(getSubDirs(file.getAbsolutePath()));
                } catch (NullPointerException e) {
                    Toast.makeText(getContext(), "Access denied", Toast.LENGTH_LONG).show();
                }
            }
        });

        //go to parent btn
        Button back = view.findViewById(R.id.dir_back);
        back.setOnClickListener(view1 -> {
            //gets the dir that's viewed
            thisDir = getThisDir();

            if (thisDir.equals(baseDir)) {
                Toast.makeText(getContext(), "You cannot go further back", Toast.LENGTH_LONG).show();
                return;
            }
            dirAdapt.clear();
            dirAdapt.addAll(getSubDirs(thisDir.getParent()));
        });

        //ok
        select = view.findViewById(R.id.dir_select);
        select.setOnClickListener(view12 -> {
            SharedPreferences sp = getActivity().getSharedPreferences("recipePrefs", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("dir", getThisDir().getAbsolutePath());
            editor.apply();
            dismiss();
        });

        //cancel
        cancel = view.findViewById(R.id.dir_cancel);
        cancel.setOnClickListener(view13 -> Toast.makeText(getContext(), "You need to choose a folder", Toast.LENGTH_LONG).show());

        //dialog stuff
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }


    public File[] getSubDirs(String base) {
        oldDir = new File(base);
        return new File(base).listFiles(File::isDirectory);
    }

    public File getThisDir(){
        File dir;
        try { dir = new File(dirAdapt.getItem(0).getParent()); } catch (Exception e) {
            dir = oldDir;
        }
        return dir;
    }
}
