package com.zarwanhashem.ideatrackr;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;
import java.util.Map;

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

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), EditIdeaPageActivity.class);
                intent.putExtra("title", ideaData.get(POSITION).get(0));
                intent.putExtra("details", ideaData.get(POSITION).get(1));
                PARENT.getContext().startActivity(intent);
            }
        });
        return view;
    }
}
