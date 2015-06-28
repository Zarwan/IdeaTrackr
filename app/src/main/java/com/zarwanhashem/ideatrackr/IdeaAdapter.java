package com.zarwanhashem.ideatrackr;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

public class IdeaAdapter extends ArrayAdapter<Button> {
    private List<String> titles;
    private List<String> details;

    public IdeaAdapter(Context context, int resource, List<Button> ideas, List<String> titles, List<String> details) {
        super(context, resource, ideas);
        this.titles = titles;
        this.details = details;
    }

    @Override
    public View getView(final int POSITION, View convertView, final ViewGroup PARENT) {
        View view = super.getView(POSITION, convertView, PARENT);
        Button idea = (Button) view.findViewById(R.id.idea_button);
        idea.setText(titles.get(POSITION));

        idea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NewIdeaPageActivity.class);
                intent.putExtra("title", titles.get(POSITION));
                intent.putExtra("details", details.get(POSITION));
                PARENT.getContext().startActivity(intent);
            }
        });
        return view;
    }
}
