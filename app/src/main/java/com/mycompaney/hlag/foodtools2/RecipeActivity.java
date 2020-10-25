package com.mycompaney.hlag.foodtools2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class RecipeActivity extends AppCompatActivity {

    android.support.v4.app.Fragment[] viewVar = {new RecipeVarsFrag(), new RecipeRecipeFrag()};  //swipe between
    android.support.v4.app.Fragment this_frag = viewVar[0];

    Handler handler;  //for timer
    SharedPreferences sp;  //recipePrefs

    public Recipe actReci; // recipe opened
    public double prtDoub;
    String shop;
    String this_content;
    double savedPrt;

    public boolean saveLock;
    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
    DecimalFormat format = (DecimalFormat)nf;

    EditText nameEdit;
    EditText timeEdit;
    EditText shopEdit;
    EditText contentEdit;
    ArrayList<EditText> editTexts;
    EditText prtEdit;


    //unmodified
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recipe, menu); // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    //unmodefied
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            this_frag = viewVar[position];
            return this_frag;
        }

        @Override
        public int getCount() {
            return 2; //2 pages
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        actReci = getIntent().getParcelableExtra("reci");
        savedPrt = actReci.prt;
        shop = actReci.shop;
        this_content = actReci.content;

        format.setDecimalSeparatorAlwaysShown(false);
        format.setGroupingUsed(false);

        sp = getSharedPreferences("recipePrefs", Activity.MODE_PRIVATE);
        editTexts = new ArrayList<EditText>();

        //default
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> onBackPressed());
    }

    //called from vars frag
    public void onFragmentCreated() {
        prtEdit = findViewById(R.id.prt);
        nameEdit = findViewById(R.id.recipe_name);
        timeEdit = findViewById(R.id.time);
        shopEdit = findViewById(R.id.ingredients);

        prtDoub = Double.longBitsToDouble(sp.getLong("prt", 1));

        editTexts.add(nameEdit);
        editTexts.add(timeEdit);
        editTexts.add(shopEdit);

        //sets editmode
        Boolean edit = getIntent().getBooleanExtra("edit", false);
        saveLock = !edit;
        setEdit(edit);
        ((ToggleButton) findViewById(R.id.editBtn)).setChecked(edit);

        prtEdit.setText(format.format(prtDoub));
        nameEdit.setText(actReci.name);
        timeEdit.setText(Long.toString(actReci.time));
        shopEdit.setText(toPrt(new StringBuffer(actReci.shop)));
    }

    public void onRecipeFragCreated() {
        contentEdit = findViewById(R.id.recipe);

        editTexts.add(contentEdit);
        contentEdit.setEnabled(nameEdit.isEnabled());
        contentEdit.setText(toPrt(new StringBuffer(actReci.content)));
    }


    //changes if the edittexts are editable
    public void setEdit(boolean edit) {
        for (EditText editText : editTexts) {
            editText.setEnabled(edit);
        }
    }

    //for the save
    public void timer() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                save();
                handler.postDelayed(this, 10000);
            }
        }, 10000);
    }


    public void save() {
        //don't save if not editable
        if(saveLock){return;}

        //adds editTexts contents to string arrlist
        ArrayList<String> textList = new ArrayList<>();
        for (EditText editText : editTexts) {
            textList.add(editText.getText().toString());
        }
        if (contentEdit == null) {
            textList.add(actReci.content);
        }

        //vars used for calcs
        this_content = textList.get(3);
        shop = textList.get(2);
        savedPrt = prtDoub;


        //save
        SQLiteDatabase myDB = getApplicationContext().openOrCreateDatabase("Recip.db", MODE_PRIVATE, null);
        ContentValues cv = new ContentValues();
        cv.put("name", textList.get(0));
        cv.put("shop", shop);
        cv.put("content", this_content);
        cv.put("date", new Date().getTime());
        try {
            cv.put("time", Integer.parseInt(textList.get(1)));
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),"Invalid time entered, not saved",Toast.LENGTH_LONG).show(); //invalid time
            return;
        }
        cv.put("prt", prtDoub);
        myDB.update("reci", cv, "id=" + actReci.id, null);
        myDB.close();


        //if backup
        if(getApplicationContext().getSharedPreferences("settings", Activity.MODE_PRIVATE).getBoolean("Backup to storage",false)){
            String dir = sp.getString("dir", "Default dir");

            File outFile = new File(dir + "/" + textList.get(0) + ".txt");
            FileOutputStream out;
            try {
                out = new FileOutputStream(outFile, false);
            }catch(Exception e){
                //dir not entered
                saveLock = true;
                Toast.makeText(getApplicationContext(), "Error:" + "\"" + e + "\"", Toast.LENGTH_LONG).show();
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                new DirDialog().show(getSupportFragmentManager(), "tag");
                return;
            }

            //saves
            String content = textList.get(0) + " Portions: " + prtDoub + "\n\n" + shop + "\n\n" + this_content;
            byte[] contents = content.getBytes();
            try {
                out.write(contents);
                out.flush();
                out.close();
            }catch(Exception e){ Toast.makeText(getApplicationContext(), "Copy to storage failed!!", Toast.LENGTH_LONG ).show();}
        }
    }


    public String toPrt(StringBuffer text){
        double workNum = prtDoub/savedPrt;
        boolean plusBefore;
        //goes through all letters looking for numbers
        for(int i = 0; i < text.length(); i++){
            char c = text.charAt(i);
            if((c < 58 && c > 47) || c == 46){ //number
                try{
                    plusBefore = (text.charAt(i - 1) == '+');}
                catch (IndexOutOfBoundsException e){plusBefore = false;}
                int start = i;
                //looks for number end
                for(; i < text.length(); i++) {
                    c = text.charAt(i);
                    if ((c < 48 || c > 57) && c != 46) { //not a number
                        break;
                    }
                }
                //removes + and ignores
                if(plusBefore){
                    text.deleteCharAt(start-1);
                    continue;
                }
                String strNum = text.substring(start, i);
                if(strNum.equals(".")){
                    i++;
                    continue;
                }
                double num= Double.parseDouble(strNum);
                num = num * workNum;
                //checks if should add
                try {
                    if (text.charAt(i) == '+') {
                        int start2 = i + 1;
                        //looks for numbers end
                        for (i = start2; i < text.length(); i++) {
                            c = text.charAt(i);
                            if (c < 48 || c > 57) {
                                break;
                            }
                        }
                        //adds it to num
                        double plus = Double.parseDouble(text.substring(start2, i));
                        num = num + plus;
                    }
                } catch(IndexOutOfBoundsException ignored){}
                int before = text.length();
                text.replace(start, i, format.format(num));
                //moves i to right place
                i += (text.length() - before);
            }
        }
        return text.toString();
    }



    //recalculates
    public void updateContent(){
        shopEdit.setText(toPrt(new StringBuffer(shop)));
        try{
            contentEdit.setText(toPrt(new StringBuffer(this_content)));}catch(Exception ignored){}
    }

    //shows non-calc note
    public void editContent(){
        shopEdit.setText(shop);
        try{
            contentEdit.setText(this_content);}catch(Exception ignored){}
            prtEdit.setText(format.format(savedPrt));
    }


    public void onPause() {
        save();
        handler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    public void onResume() {
        timer();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        save();
        super.onBackPressed();
        finish();
    }
}
