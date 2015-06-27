package com.zarwanhashem.ideatrackr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static List<Button> ideas = new ArrayList<Button>();
    private static SharedPreferences sharedPref;
    private Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RelativeLayout layout = new RelativeLayout(this);
        setContentView(layout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Intent intent = getIntent();

        Button newIdea = new Button(this);
        newIdea.setText("NEW IDEA");
        newIdea.setTranslationX(300);
        newIdea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NewIdeaPageActivity.class);
                startActivity(intent);
            }
        });
        layout.addView(newIdea);


        if (intent != null) {
            if (intent.hasExtra("title")) {
                Button button = new Button(this);
                button.setId(ideas.size() + 1000);
                button.setText(getIntent().getStringExtra("title"));
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                if (ideas.size() == 1000) {
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                } else {
                    params.addRule(RelativeLayout.BELOW, ideas.size() + 1000 - 1);
                }
                layout.addView(button, params);
                ideas.add(button);
            }
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


    public void onNewIdeaButtonClicked(View v) {
        Intent intent = new Intent(v.getContext(), NewIdeaPageActivity.class);
        startActivity(intent);
    }
}
