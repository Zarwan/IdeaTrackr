package com.zarwanhashem.ideatrackr;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

import static com.zarwanhashem.ideatrackr.MainActivity.IDEA_DETAILS_KEY;
import static com.zarwanhashem.ideatrackr.MainActivity.IDEA_ID_KEY;
import static com.zarwanhashem.ideatrackr.MainActivity.IDEA_TITLE_KEY;

/**
 * Used to provide the ListView of ideas on the MainActivity
 */
public class IdeaAdapter extends ArrayAdapter<Button> {
    private static List<List<String>> ideaData;

    public IdeaAdapter(Context context, int resource, List<Button> ideas, List<List<String>> ideaData) {
        super(context, resource, ideas);
        IdeaAdapter.ideaData = ideaData;
    }

    @Override
    public View getView(final int POSITION, View convertView, final ViewGroup PARENT) {
        View view = super.getView(POSITION, convertView, PARENT);
        Button idea = (Button) view.findViewById(R.id.idea_button);
        idea.setText(ideaData.get(POSITION).get(0));

        idea.setOnClickListener(new View.OnClickListener() {

            //onClick of ideas takes them to the edit idea page
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditIdeaPageActivity.class);
                intent.putExtra(IDEA_TITLE_KEY, ideaData.get(POSITION).get(0));
                intent.putExtra(IDEA_DETAILS_KEY, ideaData.get(POSITION).get(1));
                intent.putExtra(IDEA_ID_KEY, POSITION);
                PARENT.getContext().startActivity(intent);
            }
        });
        return view;
    }
}
