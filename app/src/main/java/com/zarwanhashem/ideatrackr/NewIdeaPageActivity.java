package com.zarwanhashem.ideatrackr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import static com.zarwanhashem.ideatrackr.MainActivity.CURR_IDEA_KEY;
import static com.zarwanhashem.ideatrackr.MainActivity.IDEA_EDIT_KEY;
import static com.zarwanhashem.ideatrackr.MainActivity.IDEA_ID_KEY;

/**
 * Page where users create new ideas
 */
public class NewIdeaPageActivity extends AppCompatActivity {
    private static SharedPreferences sharedPref;
    private static int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_idea_page);
        Context myContext = getApplicationContext();

        EditText ideaTitle = (EditText)findViewById(R.id.ideaTitle);
        EditText ideaDetails = (EditText)findViewById(R.id.ideaDetails);
        sharedPref = myContext.getSharedPreferences("sharedPref", 0);

        //Invalid ID sent
        if (id == -1 && sharedPref.contains(IDEA_ID_KEY)) {
            id = sharedPref.getInt(IDEA_ID_KEY, -1);
        }

        //Set default text
        setTitle("Create a New Idea");
        ideaTitle.setText("Title");
        ideaDetails.setText("Details");

        id++; //update button ID (index used to refer to it in arrays)
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_idea_page, menu);
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

    public void onSaveIdeaButtonClick(View v) {

        //Save new idea data to sharedPreferences
        SharedPreferences.Editor editor = sharedPref.edit();
        EditText ideaTitle = (EditText)findViewById(R.id.ideaTitle);
        EditText ideaDetails = (EditText)findViewById(R.id.ideaDetails);
        editor.putInt(IDEA_ID_KEY, -1);
        editor.apply();

        //Package information into an intent to go back to the main page
        Intent intent = new Intent(v.getContext(), MainActivity.class);
        Gson gson = new Gson();
        Idea newIdea = new Idea(ideaTitle.getText().toString(), ideaDetails.getText().toString());
        String newIdeaJson = gson.toJson(newIdea);
        intent.putExtra(CURR_IDEA_KEY, newIdeaJson);
        intent.putExtra(IDEA_EDIT_KEY, false);
        intent.putExtra(IDEA_ID_KEY, id);
        startActivity(intent);
    }
}
