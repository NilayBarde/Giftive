package com.example.nilay.giftorganizer.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
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
    private HashMap<String, List<Gift>> listHash;
    private TextView instructions;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private ArrayList<String> nameList;
    private ArrayList<Person> personList;
    private CheckBox bought;
    private boolean currBought;
    private boolean allBought;
    private boolean [] groupExpandedArray = new boolean[0];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_fragment_shop, container, false);

        listView = (ExpandableListView) view.findViewById(R.id.lvExp);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid());
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
                if(listHash.size() > 0) {
                    listView.setAdapter(listAdapter);
                }
                else {
                    listView.setAdapter((ExpandableListAdapter) null);
                }
                if(groupExpandedArray.length != 0) {
                    saveStates();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                bought = v.findViewById(R.id.shopBought);
                Gift g = listHash.get(listDataHeader.get(groupPosition)).get(childPosition);
                int numberOfGroups = listAdapter.getGroupCount();
                groupExpandedArray = new boolean[numberOfGroups];
                for (int i=0;i<numberOfGroups;i++)
                    groupExpandedArray[i] = listView.isGroupExpanded(i);
                databaseReference.child("GiftsList").child(listAdapter.getGroup(groupPosition) + " Gifts").child(listAdapter.getChild(groupPosition, childPosition).toString()).child("bought").setValue(!(g.isBought()));
                return true;
            }
        });

        return view;
    }

    private void saveStates() {
        if (listHash.size() > 0) {
            for (int i = 0; i < groupExpandedArray.length; i++)
                if (groupExpandedArray[i] == true)
                    listView.expandGroup(i);
        }
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
            ArrayList<Gift> list = new ArrayList<>();
//            allBought = true;
            for(DataSnapshot ds2 : dataSnapshot4.getChildren()) {
                Gift gift = ds2.getValue(Gift.class);
//                if(!(gift.isBought())) {
//                    allBought = false;
//                }
                list.add(gift);
                listHash.put(listDataHeader.get(a), list);
            }
//            if(!dataSnapshot4.exists()) {
//                allBought = false;
//            }
            a++;
//            databaseReference.child("PersonList").child(name).child("allGiftsBought").setValue(allBought);

        }

    }

}
