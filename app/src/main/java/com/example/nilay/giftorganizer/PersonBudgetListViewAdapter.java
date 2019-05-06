package com.example.nilay.giftorganizer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PersonBudgetListViewAdapter extends ArrayAdapter<Person> {

    public Context mContext;

    int mResource;

    public PersonBudgetListViewAdapter(Context context, int resource, ArrayList<Person> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String name = getItem(position).getName();
        Double budget = getItem(position).getBudget();

        Person person = new Person();
        person.setName(name);
        person.setBudget(budget);

        if(LayoutInflater.from(mContext) != null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);

            TextView tvName = (TextView) convertView.findViewById(R.id.textView1);
            TextView tvBudget = (TextView) convertView.findViewById(R.id.textView2);

            tvName.setText(name);
            tvBudget.setText("0/" + budget.toString());
        }

        return convertView;

    }
}
