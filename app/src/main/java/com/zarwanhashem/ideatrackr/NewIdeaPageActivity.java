package com.zarwanhashem.ideatrackr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class NewIdeaPageActivity extends AppCompatActivity {
    private static final String IDEA_TITLE_KEY = "title";
    private static final String IDEA_DETAILS_KEY = "details";
    private Context myContext;
    private SharedPreferences sharedPref;
    private static int id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_idea_page);
        myContext = getApplicationContext();

        EditText ideaTitle = (EditText)findViewById(R.id.ideaTitle);
        EditText ideaDetails = (EditText)findViewById(R.id.ideaDetails);
        sharedPref = myContext.getSharedPreferences("sharedPref", 0);

        setTitle("Create a New Idea");
        ideaTitle.setText("Title");
        ideaDetails.setText("Details");
        id++;
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
        SharedPreferences.Editor editor = sharedPref.edit();
        EditText ideaTitle = (EditText)findViewById(R.id.ideaTitle);
        EditText ideaDetails = (EditText)findViewById(R.id.ideaDetails);

        editor.putString(IDEA_TITLE_KEY, ideaTitle.getText().toString());
        editor.putString(IDEA_DETAILS_KEY, ideaDetails.getText().toString());
        editor.commit();

        Intent intent = new Intent(v.getContext(), MainActivity.class);
        intent.putExtra(IDEA_TITLE_KEY, ideaTitle.getText().toString());
        intent.putExtra(IDEA_DETAILS_KEY, ideaDetails.getText().toString());
        intent.putExtra("Edit", false);
        intent.putExtra("ID", id);
        startActivity(intent);
    }
}
