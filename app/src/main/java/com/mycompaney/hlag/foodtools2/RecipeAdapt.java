package com.mycompaney.hlag.foodtools2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class RecipeAdapt extends ArrayAdapter<Recipe> {
    public RecipeAdapt(@NonNull Context context, ArrayList<Recipe> arrlistNote) {
        super(context, R.layout.row_layout_notes, arrlistNote);
        og = (ArrayList<Recipe>) arrlistNote.clone();
    }

    public ArrayList<Recipe> og;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflator = LayoutInflater.from(getContext());
        View view = inflator.inflate(R.layout.row_layout_notes, parent, false);

        final Recipe recipe = getItem(position);
        TextView textView = view.findViewById(R.id.note_list_item_text);
        textView.setText(recipe.name);

        //option btn
        ImageButton imgBtn = view.findViewById(R.id.note_list_item_button);
        imgBtn.setOnClickListener(view1 -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                SQLiteDatabase myDB = getContext().openOrCreateDatabase("Recip.db", MODE_PRIVATE, null);
                myDB.delete("reci", "id=" + recipe.id, null);
                remove(recipe);
                notifyDataSetChanged();

                if(getContext().getSharedPreferences("settings", Activity.MODE_PRIVATE).getBoolean("Backup to storage", false)){
                    try{
                        new File(getContext().getSharedPreferences("recipePrefs", Activity.MODE_PRIVATE).getString("dir", "") + "/" + recipe.name + ".txt").delete();
                    }catch(Exception e){
                        Log.w("delete: ", e);
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(view1.getContext());
            builder.setMessage("Are you sure you want to delete \"" + recipe.name + "\"?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", null).show();
        });

        return view;
    }

    //search function
    //check how many tectReci created
    public void filter(String input) {
        ArrayList<Recipe> equals = new ArrayList<>();
        ArrayList<Recipe> startsWith = new ArrayList<>();
        ArrayList<Recipe> contains = new ArrayList<>();
        ArrayList<Recipe> other = new ArrayList<>();

        for (Recipe testReci : og) {
            String name = testReci.name;
            if (name.equals(input)) {
                equals.add(testReci);
            } else if (name.startsWith(input)) {
                startsWith.add(testReci);
            } else if (name.contains(input)) {
                contains.add(testReci);
            } else {
                other.add(testReci);
            }
        }
        clear();
        addAll(equals);
        addAll(startsWith);
        addAll(contains);
        addAll(other);

        notifyDataSetChanged();
    }

    public void update(ArrayList<Recipe> arrlist) {
        clear();
        addAll(arrlist);
        og = arrlist;
    }
}
