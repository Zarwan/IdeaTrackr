package com.zarwanhashem.ideatrackr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.zarwanhashem.ideatrackr.MainActivity.CURR_IDEA_KEY;
import static com.zarwanhashem.ideatrackr.MainActivity.IDEA_DELETE_KEY;
import static com.zarwanhashem.ideatrackr.MainActivity.IDEA_EDIT_KEY;
import static com.zarwanhashem.ideatrackr.MainActivity.IDEA_ID_KEY;

/**
 * Page where users edit existing ideas
 */
public class EditIdeaPageActivity extends AppCompatActivity {
    private int id;
    private Idea idea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_idea_page);

        Intent intent = getIntent();
        EditText ideaTitle = (EditText)findViewById(R.id.ideaTitle);
        EditText ideaDetails = (EditText)findViewById(R.id.ideaDetails);

        if (intent != null) {
            idea = getIdea(intent);
            setTitle(idea.getTitle());
            ideaTitle.setText(idea.getTitle());
            ideaDetails.setText(idea.getDetails());
            id = intent.getIntExtra(IDEA_ID_KEY, -1);

        } else {
            throw new RuntimeException(
                    "Recieved a null intent when navigating to the edit idea page");
            //this should never happen
        }
    }

    private Idea getIdea(Intent intent) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(intent.getStringExtra(CURR_IDEA_KEY), Idea.class);
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
        idea.setTitle(ideaTitle.getText().toString());
        idea.setDetails(ideaDetails.getText().toString());

        Intent intent = new Intent(v.getContext(), MainActivity.class);
        String jsonIdea = new Gson().toJson(idea);
        intent.putExtra(CURR_IDEA_KEY, jsonIdea);
        intent.putExtra(IDEA_EDIT_KEY, true);
        intent.putExtra(IDEA_ID_KEY, id);
        intent.putExtra(IDEA_DELETE_KEY, false);
        startActivity(intent);
    }

    public void onDeleteIdeaButtonClick(View v) {
        final Intent intent = new Intent(v.getContext(), MainActivity.class);

        //Add confirmation dialog to delete idea button
        new AlertDialog.Builder(this)
                .setTitle("Delete Idea")
                .setMessage("Are you sure? You cannot restore a deleted idea.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        intent.putExtra(IDEA_EDIT_KEY, true);
                        intent.putExtra(IDEA_ID_KEY, id);
                        intent.putExtra(IDEA_DELETE_KEY, true);
                        id--;
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
}
