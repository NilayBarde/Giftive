package com.example.nilay.giftorganizer.Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nilay.giftorganizer.EditPersonActivity;
import com.example.nilay.giftorganizer.GiftItems;
import com.example.nilay.giftorganizer.MainActivity;
import com.example.nilay.giftorganizer.Objects.Person;
import com.example.nilay.giftorganizer.CustomAdapters.PersonBudgetListViewAdapter;
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
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private Person currPerson;
    private static final String TAG = "FRAGMENT_GIFTLIST";
    private AlertDialog.Builder builder;
    private TextView instructions;
    private String name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragment_gift_list, container, false);

         currPerson = new Person();
         giftPeople = new ArrayList<Person>();
         instructions = view.findViewById(R.id.instructions);
         listView = (ListView) view.findViewById(R.id.listview);
         database = FirebaseDatabase.getInstance();
         firebaseAuth = FirebaseAuth.getInstance();
         user = firebaseAuth.getCurrentUser();
         databaseReference = database.getReference(user.getUid() + "/PersonList");
         builder = new AlertDialog.Builder(view.getContext());
         adapter = new PersonBudgetListViewAdapter(getActivity(), R.layout.adapter_view_layout, giftPeople);
         listView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                giftPeople.clear();
                showData(dataSnapshot);
                if(giftPeople.size() > 0) {
                    instructions.setText("");
                }
                listView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                name = giftPeople.get(position).getName();
                Intent intent = new Intent(getActivity(), GiftItems.class);
                Bundle bundle = new Bundle();
                bundle.putString("Name", name);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        builder.setTitle("Confirm");

        registerForContextMenu(listView);

         //Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId()== R.id.listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            String [] menuItems = getResources().getStringArray(R.array.menu);
            for(int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }


        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        //Log.d("ADebugTag", "Value: " + menuItemIndex);
        String [] menuItems = getResources().getStringArray(R.array.menu);
        String menuItemName = menuItems[menuItemIndex];
        if(menuItemName.equals("Delete")) {
            builder.setMessage("Are you sure you want to delete " + giftPeople.get(info.position).getName() + " and all of their gifts?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(), String.format("%s was deleted from your gift list", giftPeople.get(info.position).getName()), Toast.LENGTH_LONG).show();
                    deletePersonFromListViewDatabase(giftPeople.get(info.position).getName());
                    deleteGiftsFromListViewDatabase(giftPeople.get(info.position).getName());
                    deleteEventsFromDatabase(giftPeople.get(info.position).getName());
                    giftPeople.remove(info.position);
                    dialog.dismiss();
                    if(giftPeople.size() == 0) {
                        instructions.setText("There are no people in your list yet!\n\n Add a person using the button below!");
                    }
                    listView.setAdapter(adapter);
                }
            });

            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    // Do nothing
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            Intent intent = new Intent(getActivity(), EditPersonActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("Name", giftPeople.get(info.position).getName());
            bundle.putDouble("Budget", giftPeople.get(info.position).getBudget());
            intent.putExtras(bundle);
            startActivity(intent);
        }
        return true;
    }

    private void showData(DataSnapshot dataSnapshot) {

        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            currPerson = ds.getValue(Person.class);
            giftPeople.add(currPerson);
        }
        Collections.sort(giftPeople);

    }

    private void deleteEventsFromDatabase(String name) {
        database.getReference().child(user.getUid()).child("EventList").child(name + "'s Event").removeValue();

    }

    private void deleteGiftsFromListViewDatabase(String name){
        database.getReference().child(user.getUid()).child("GiftsList").child(name + " Gifts").removeValue();
    }

    private void deletePersonFromListViewDatabase(String name) {
        database.getReference().child(user.getUid()).child("PersonList").child(name).removeValue();

    }

}
