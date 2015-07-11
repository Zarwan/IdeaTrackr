package com.zarwanhashem.ideatrackr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static final String IDEA_TITLE_KEY = "Title";
    public static final String IDEA_DETAILS_KEY = "Details";
    public static final String IDEA_EDIT_KEY = "Edit";
    public static final String IDEA_ID_KEY = "ID";
    private final String IDEAS_KEY = "Ideas";
    private final String IDEA_DATA_KEY = "IdeaData";
    private static List<Button> ideas = new ArrayList<Button>();
    private static List<List<String>> ideaData = new ArrayList<List<String>>(); //each index is made up of a list; first index of inner list is title, second is details
    private static SharedPreferences sharedPref;
    private Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContext = getApplicationContext();

        sharedPref = myContext.getSharedPreferences("sharedPref", 0);
        ListView ideasListView = (ListView) findViewById(R.id.ideasListView);

        //Load from sharedPref if restoring session
        if (ideas.size() == 0 && sharedPref.contains(IDEAS_KEY)) {
            for (int i = 0; i < sharedPref.getInt(IDEAS_KEY, 0); i++) {
                ideas.add(new Button(myContext));
            }
            String dataValue = sharedPref.getString(IDEA_DATA_KEY, null);
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();
            ideaData = gson.fromJson(dataValue, ArrayList.class);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(IDEA_EDIT_KEY)) {

            if (intent.getBooleanExtra(IDEA_EDIT_KEY, false)) {
                int id = intent.getIntExtra(IDEA_ID_KEY, -1);
                ideaData.get(id).set(0, intent.getStringExtra(IDEA_TITLE_KEY));
                ideaData.get(id).set(1, intent.getStringExtra(IDEA_DETAILS_KEY));
            } else {
                List<String> data = new ArrayList<String>();
                data.add(intent.getStringExtra(IDEA_TITLE_KEY));
                data.add(intent.getStringExtra(IDEA_DETAILS_KEY));
                ideaData.add(data);
                ideas.add(new Button(myContext));
            }
        }

        IdeaAdapter listAdapter = new IdeaAdapter(myContext, R.layout.idea_button, ideas, ideaData);
        ideasListView.setAdapter(listAdapter);

        //Save update ideas to sharedPref
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(ideaData);
        editor.putString(IDEA_DATA_KEY, json);
        editor.putInt(IDEAS_KEY, ideas.size());
        editor.apply();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onNewIdeaButtonClicked(View v) {
        Intent intent = new Intent(v.getContext(), NewIdeaPageActivity.class);
        startActivity(intent);
    }
}
