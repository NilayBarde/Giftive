package com.example.nilay.giftorganizer.CustomAdapters;

import android.content.Context;

import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.nilay.giftorganizer.Objects.Person;
import com.example.nilay.giftorganizer.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PersonBudgetListViewAdapter extends ArrayAdapter<Person> {

    public Context mContext;

    int mResource;

    private String name;
    private CheckBox allGiftsBought;
    private ArrayList<Person> objects;


    public PersonBudgetListViewAdapter(Context context, int resource, ArrayList<Person> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DecimalFormat df = new DecimalFormat("#0.00");
        name = getItem(position).getName();
        Double budget = getItem(position).getBudget();
        Double bought = getItem(position).getBought();

        if(LayoutInflater.from(mContext) != null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            allGiftsBought = convertView.findViewById(R.id.bought);

            allGiftsBought.setChecked(objects.get(position).isAllGiftsBought());

            TextView tvName = (TextView) convertView.findViewById(R.id.textView1);
            TextView tvBudget = (TextView) convertView.findViewById(R.id.textView2);

            tvName.setText(name);
            tvBudget.setText("$" + df.format(bought) + " / $" + df.format(budget));
        }

        return convertView;

    }
}
