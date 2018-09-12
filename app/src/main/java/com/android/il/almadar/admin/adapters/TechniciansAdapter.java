package com.android.il.almadar.admin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.il.almadar.R;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Labon on 3/20/2016.
 */
public class TechniciansAdapter extends ArrayAdapter<ParseUser>{

    private Context context;
    private List<ParseUser> list;

    public TechniciansAdapter(Context context, int resource, List<ParseUser> objects) {
        super(context, resource, objects);
        this.context = context;
        this.list = objects;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.tech_list_item, parent, false);
        }

        TextView tvFullName, tvUsername;
        ParseUser user = list.get(position);
        tvFullName = (TextView) convertView.findViewById(R.id.listTechFullName);
        tvUsername = (TextView) convertView.findViewById(R.id.listTechUsername);
        tvFullName.setText(user.get("full_name").toString());
        tvUsername.setText(user.getUsername());

        return convertView;
    }

    @Override
    public ParseUser getItem(int position) {
        return this.list.get(position);
    }
}
