package com.mycompaney.hlag.foodtools2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class RecipesListFrag extends android.support.v4.app.Fragment {

    View view;
    public SQLiteDatabase myDB;
    public static ArrayList <Recipe> reciList = new ArrayList<>();
    public static RecipeAdapt listAdapt;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recipes_list_frag, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view_useless, @Nullable Bundle savedInstanceState) {
        myDB = getContext().openOrCreateDatabase("Recip.db", MODE_PRIVATE, null);
        myDB.execSQL("CREATE TABLE IF NOT EXISTS reci (id INTEGER primary key, name TEXT, shop TEXT, content TEXT, date INTEGER, time INTEGER, prt REAL)");

        reciList = getFrmDb("select id, name, shop, content, date, time, prt from reci ORDER BY date DESC;", null);

        listAdapt = new <Recipe> RecipeAdapt(view.getContext(), reciList);
        ListView reciList = view.findViewById(R.id.reci_list);
        reciList.setAdapter(listAdapt);
        reciList.setOnItemClickListener((adapterView, view, i, l) -> {
            Recipe reci = (Recipe) adapterView.getItemAtPosition(i);
            Intent recipeIntent = new Intent(getContext(), RecipeActivity.class);
            recipeIntent.putExtra("reci", reci);
            startActivity(recipeIntent);
        });

        EditText searchBox = view.findViewById(R.id.reci_search_box);
        searchBox.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable mEdit)
            {
                listAdapt.filter(mEdit.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        ImageButton addBtn = view.findViewById(R.id.reci_add_btn);
        addBtn.setOnClickListener(view -> {
            //insert new reci
            long time = new Date().getTime();
            Log.w(TAG, "onClick: " + myDB);
            myDB.execSQL("INSERT INTO reci (id, name, shop, content, date, time, prt) VALUES (NULL, '', '', '',"+
                    time +
                    ",0, 1)");

            //find and open it
            Recipe reciGot = getFrmDb("select id, name, shop, content, date, time, prt from reci WHERE date=?",new String[] {Long.toString(time)}).get(0);
            Intent recipeIntent = new Intent(getContext(), RecipeActivity.class);
            recipeIntent.putExtra("reci", reciGot);
            recipeIntent.putExtra("edit", true);
            startActivity(recipeIntent);

        });

    }

    public ArrayList<Recipe> getFrmDb(String sql, String[] args){
        Cursor cursor = myDB.rawQuery(sql,args);
        ArrayList<Recipe>getReciList = new ArrayList<>();
        while(cursor.moveToNext()) {
            Recipe reci = new Recipe(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getLong(4),
                    cursor.getLong(5),
                    cursor.getDouble(6)
            );
            getReciList.add(reci);
        }
        cursor.close();
        return getReciList;
    }


    public void onPause() {
        myDB.close();
        super.onPause();
    }

    public void onResume() {
        myDB = getContext().openOrCreateDatabase("Recip.db", MODE_PRIVATE, null);

        listAdapt.update(getFrmDb("select id, name, shop, content, date, time, prt from reci ORDER BY date DESC;", null));
        listAdapt.notifyDataSetChanged();
        super.onResume();
    }
}
