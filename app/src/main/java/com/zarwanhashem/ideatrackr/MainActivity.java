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

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static List<Button> ideas = new ArrayList<Button>();
    private static List<String> titles = new ArrayList<String>();
    private static List<String> details = new ArrayList<String>();
    private static SharedPreferences sharedPref;
    private Context myContext;
    private ListView ideasListView;
    private IdeaAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myContext = getApplicationContext();

        ideasListView = (ListView) findViewById(R.id.ideasListView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("title") && intent.hasExtra("newIdea")) {

            if (intent.getStringExtra("newIdea").equals("true")) {
                ideas.add(new Button(myContext));
                titles.add(intent.getStringExtra("title"));
                details.add(intent.getStringExtra("details"));
            }
        }



        listAdapter = new IdeaAdapter(myContext, R.layout.idea_button, ideas, titles, details);
        ideasListView.setAdapter(listAdapter);
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
