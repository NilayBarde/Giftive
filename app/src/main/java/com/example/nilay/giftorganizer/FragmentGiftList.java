package com.example.nilay.giftorganizer;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.example.nilay.giftorganizer.addPersonActivity.MY_CUSTOM_FRAGMENT_KEY;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGiftList extends Fragment {


    public FragmentGiftList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment_gift_list, container, false);

        ArrayList<String> giftPeople = new ArrayList<String>();

        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");
        giftPeople.add("Nilay");



        ListView listView = (ListView) view.findViewById(R.id.listview);

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_list_item_1, giftPeople);

        listView.setAdapter(listViewAdapter);

         //Inflate the layout for this fragment
        return view;

    }






}
