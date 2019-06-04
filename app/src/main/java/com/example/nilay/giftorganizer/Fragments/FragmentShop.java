package com.example.nilay.giftorganizer.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.nilay.giftorganizer.Objects.Gift;
import com.example.nilay.giftorganizer.Objects.Person;
import com.example.nilay.giftorganizer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentShop extends Fragment {


    public FragmentShop() {
        // Required empty public constructor
    }

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHash;
    private TextView instructions;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private ArrayList<String> nameList;
    private ArrayList<Person> personList;
    private String selectedGift;
    private CheckBox bought;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_fragment_shop, container, false);

        listView = (ExpandableListView) view.findViewById(R.id.lvExp);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = database.getReference(user.getUid());
        instructions = view.findViewById(R.id.instructions);
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        listAdapter = new com.example.nilay.giftorganizer.CustomAdapters.ExpandableListAdapter(getContext(), listDataHeader, listHash);
        listView.setAdapter(listAdapter);
        nameList = new ArrayList<>();
        personList = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clearData();
                initData(dataSnapshot);
                instructionsTextChange();
                listView.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                bought = view.findViewById(R.id.bought);
                selectedGift = (String) listAdapter.getChild(groupPosition, childPosition);
                bought.setChecked(true);
                return true;
            }
        });

        return view;
    }

    private void clearData() {
        nameList.clear();
        personList.clear();
        listHash.clear();
        listDataHeader.clear();
    }

    private void instructionsTextChange() {
        if(listDataHeader.size() > 0) {
            instructions.setText("");
        }
        else {
            instructions.setText("There are no gifts in your list yet!\n\n Add a gift by tapping on a person's name in Gift List!");
        }
    }

    private void initData(DataSnapshot dataSnapshot) {
        int a = 0;
        DataSnapshot dataSnapshot3 = dataSnapshot.child("PersonList");
        DataSnapshot dataSnapshot2 = dataSnapshot.child("GiftsList");
        for(DataSnapshot ds : dataSnapshot3.getChildren()) {
            Person person = ds.getValue(Person.class);
            personList.add(person);
        }
        Collections.sort(personList);
        for(Person pers : personList) {
            nameList.add(pers.getName());
        }
        for(String name : nameList) {
            if(dataSnapshot2.child(name + " Gifts").exists()) {
                listDataHeader.add(name);
            }
        }

        for(String name : nameList) {
            DataSnapshot dataSnapshot4 = dataSnapshot2.child(name + " Gifts");
            ArrayList<String> list = new ArrayList<>();
            for(DataSnapshot ds2 : dataSnapshot4.getChildren()) {
                Gift gift = ds2.getValue(Gift.class);
                gift.setBought(false);
                list.add(gift.getName());
                listHash.put(listDataHeader.get(a), list);
            }
            a++;
        }
    }

}
