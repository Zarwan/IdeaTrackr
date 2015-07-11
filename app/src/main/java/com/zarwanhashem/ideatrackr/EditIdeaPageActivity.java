package com.zarwanhashem.ideatrackr;

import android.content.Intent;
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
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_idea_page);

        Intent intent = getIntent();
        EditText ideaTitle = (EditText)findViewById(R.id.ideaTitle);
        EditText ideaDetails = (EditText)findViewById(R.id.ideaDetails);

        if (intent != null) {
            setTitle(intent.getStringExtra(IDEA_TITLE_KEY));
            ideaTitle.setText(intent.getStringExtra(IDEA_TITLE_KEY));
            ideaDetails.setText(intent.getStringExtra(IDEA_DETAILS_KEY));
            id = intent.getIntExtra(IDEA_ID_KEY, -1);

        } else {
            setTitle("ERROR: Title not found");
            ideaTitle.setText("ERROR: Title not found");
            ideaDetails.setText("ERROR: Details not found");
            id = -1;
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
        EditText ideaTitle = (EditText)findViewById(R.id.ideaTitle);
        EditText ideaDetails = (EditText)findViewById(R.id.ideaDetails);

        Intent intent = new Intent(v.getContext(), MainActivity.class);
        intent.putExtra(IDEA_TITLE_KEY, ideaTitle.getText().toString());
        intent.putExtra(IDEA_DETAILS_KEY, ideaDetails.getText().toString());
        intent.putExtra(IDEA_EDIT_KEY, true);
        intent.putExtra(IDEA_ID_KEY, id);
        startActivity(intent);
    }
}
