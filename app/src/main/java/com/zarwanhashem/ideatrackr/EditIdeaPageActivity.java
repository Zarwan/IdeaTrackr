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

import static com.zarwanhashem.ideatrackr.MainActivity.IDEA_DETAILS_KEY;
import static com.zarwanhashem.ideatrackr.MainActivity.IDEA_ID_KEY;
import static com.zarwanhashem.ideatrackr.MainActivity.IDEA_TITLE_KEY;
import static com.zarwanhashem.ideatrackr.MainActivity.IDEA_EDIT_KEY;

public class EditIdeaPageActivity extends AppCompatActivity {
    private static SharedPreferences sharedPref;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_idea_page);
        Context myContext = getApplicationContext();

        Intent intent = getIntent();
        EditText ideaTitle = (EditText)findViewById(R.id.ideaTitle);
        EditText ideaDetails = (EditText)findViewById(R.id.ideaDetails);
        sharedPref = myContext.getSharedPreferences("sharedPref", 0);

        if (intent != null) {
            setTitle(intent.getStringExtra(IDEA_TITLE_KEY));
            ideaTitle.setText(intent.getStringExtra(IDEA_TITLE_KEY));
            ideaDetails.setText(intent.getStringExtra(IDEA_DETAILS_KEY));
            id = intent.getIntExtra(IDEA_ID_KEY, -1);
        } else {
            setTitle(sharedPref.getString(IDEA_TITLE_KEY, "ERROR: Title not found"));
            ideaTitle.setText(sharedPref.getString(IDEA_TITLE_KEY, "ERROR: Title not found"));
            ideaDetails.setText(sharedPref.getString(IDEA_DETAILS_KEY, "ERROR: Details not found"));
            id = sharedPref.getInt(IDEA_ID_KEY, -1);
        }
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


    public void onSaveIdeaButtonClick(View v) {
        SharedPreferences.Editor editor = sharedPref.edit();
        EditText ideaTitle = (EditText)findViewById(R.id.ideaTitle);
        EditText ideaDetails = (EditText)findViewById(R.id.ideaDetails);

        editor.putString(IDEA_TITLE_KEY, ideaTitle.getText().toString());
        editor.putString(IDEA_DETAILS_KEY, ideaDetails.getText().toString());
        editor.putInt(IDEA_ID_KEY, id);
        editor.apply();

        Intent intent = new Intent(v.getContext(), MainActivity.class);
        intent.putExtra(IDEA_TITLE_KEY, sharedPref.getString(IDEA_TITLE_KEY, "ERROR: Title not found"));
        intent.putExtra(IDEA_DETAILS_KEY, sharedPref.getString(IDEA_DETAILS_KEY, "ERROR: Details not found"));
        intent.putExtra(IDEA_EDIT_KEY, true);
        intent.putExtra(IDEA_ID_KEY, sharedPref.getInt(IDEA_ID_KEY, -1));
        startActivity(intent);
    }
}
