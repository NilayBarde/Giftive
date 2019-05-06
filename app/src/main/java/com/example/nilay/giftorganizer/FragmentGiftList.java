package com.example.nilay.giftorganizer;


import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.ChildrenNode;

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

    private ArrayList<Person> giftPeople;

    private ArrayAdapter<Person> adapter;
    private ListView listView;

    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private Person currPerson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment_gift_list, container, false);

        currPerson = new Person();
         giftPeople = new ArrayList<Person>();
         listView = (ListView) view.findViewById(R.id.listview);
         database = FirebaseDatabase.getInstance();
         databaseReference = database.getReference("GiftGivingList");

         adapter = new PersonBudgetListViewAdapter(getActivity(), R.layout.adapter_view_layout, giftPeople);
         listView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
                //Log.d("ADebugTag", "Value: " + giftPeople.get(0).getName());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         //Inflate the layout for this fragment
        return view;

    }

    private void showData(DataSnapshot dataSnapshot) {

        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            currPerson = ds.getValue(Person.class);
            giftPeople.add(currPerson);

        }
        listView.setAdapter(adapter);
    }
}
