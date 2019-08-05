package com.example.nilay.giftorganizer.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nilay.giftorganizer.EditPersonActivity;
import com.example.nilay.giftorganizer.GiftItems;
import com.example.nilay.giftorganizer.MainActivity;
import com.example.nilay.giftorganizer.Objects.Gift;
import com.example.nilay.giftorganizer.Objects.Person;
import com.example.nilay.giftorganizer.CustomAdapters.PersonBudgetListViewAdapter;
import com.example.nilay.giftorganizer.R;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
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

    // Arraylist to contain all the people the user has added
    private ArrayList<Person> giftPeople;
    // Array Adapter to show data
    private ArrayAdapter<Person> adapter;
    // ListView of all the people
    private ListView listView;
    // Database reference for the list of people in Firebase
    private DatabaseReference databaseReference;
    // Database reference for the list of gifts for every person
    private DatabaseReference databaseReference2;
    // Current User logged in
    private FirebaseUser user;
    // Current person being retrieved from Firebase
    private Person currPerson;
    private static final String TAG = "FRAGMENT_GIFTLIST";
    // Alert Dialog when user wants to delete a person
    private AlertDialog.Builder builder;
    // Instructions for user when list is empty
    private TextView instructions;
    // Name of person that user clicks on to send to next activity
    private String name;
    // boolean to check if all the items for a current user have been bought, checks checkbox if true
    private boolean allBought;

    private boolean first;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.fragment_fragment_gift_list, container, false);
         final ProgressDialog progressDialog = new ProgressDialog(getContext());
         currPerson = new Person();
         giftPeople = new ArrayList<Person>();
         instructions = view.findViewById(R.id.instructions);
         listView = (ListView) view.findViewById(R.id.listview);
         user = FirebaseAuth.getInstance().getCurrentUser();
         databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid()).child("PersonList");
         databaseReference2 = FirebaseDatabase.getInstance().getReference(user.getUid()).child("GiftsList");
         builder = new AlertDialog.Builder(view.getContext());
         adapter = new PersonBudgetListViewAdapter(getActivity(), R.layout.adapter_view_layout, giftPeople);
         listView.setAdapter(adapter);
         first = true;
             // Progress dialog when data is being retrieved from Firebase
             progressDialog.setCanceledOnTouchOutside(false);
             progressDialog.setIndeterminate(true);
             progressDialog.setMessage("Loading Your Data...");
             if(first) {
                progressDialog.show();
             }

        // Update the list from Firebase whenever data under PersonList is changed in Firebase and on start
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                first = false;
                giftPeople.clear();
                getData(dataSnapshot);
                if(giftPeople.size() > 0) {
                    instructions.setText("");
                }
                listView.setAdapter(adapter);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*
            Checks the database for when all of the gifts have been bought for every person and
            changes the data in Firebase accordingly.
         */
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    allBought = true;
                    for(DataSnapshot ds2 : ds.getChildren()) {
                        Gift g = ds2.getValue(Gift.class);
                        if(!(g.isBought())) {
                            allBought = false;
                            break;
                        }
                    }
                    databaseReference.child(ds.getKey().substring(0, ds.getKey().length()-6)).child("allGiftsBought").setValue(allBought);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*
            When the user clicks on an item in the list it will take them to the GiftItems activity along with the
            name of the person that was clicked on saved in a bundle.
         */
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

    /*
        Creates the menu for when there is a long press hold on a particular item on the list.
        Menu of: Edit, Delete
     */
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

    /*
        Decides what happens when a user selects a particular item in the context menu.
        When the user clicks on delete, they will be asked if they are sure they want to delete this person,
        if Yes is selected it will be deleted from the list and the database
        if No is selected nothing will happen.
        When the user clicks on edit, they will be sent to the EditPerson activity with their name and budget
        sent in a bundle.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String [] menuItems = getResources().getStringArray(R.array.menu);
        String menuItemName = menuItems[menuItemIndex];
        if(menuItemName.equals("Delete")) {
            builder.setMessage("Are you sure you want to delete " + giftPeople.get(info.position).getName() + " and all of their gifts?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    String name = giftPeople.get(info.position).getName();
                    giftPeople.remove(info.position);
                    Toast.makeText(getActivity(), String.format("%s was deleted from your gift list", name), Toast.LENGTH_LONG).show();
                    deletePersonFromListViewDatabase(name);
                    deleteGiftsFromListViewDatabase(name);
                    deleteEventsFromDatabase(name);
//                    dialog.dismiss();
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

    // Gets all the data from Firebase and adds it to a list. Then sorts this list based on timestamp.
    private void getData(DataSnapshot dataSnapshot) {
        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            currPerson = ds.getValue(Person.class);
            giftPeople.add(currPerson);
        }
        Collections.sort(giftPeople);
    }

    // Deletes data from under the Events in Firebase
    private void deleteEventsFromDatabase(String name) {
        FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("EventList").child(name + "'s Event").removeValue();

    }

    // Deletes data from under GiftsList in Firebase
    private void deleteGiftsFromListViewDatabase(String name){
        FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("GiftsList").child(name + " Gifts").removeValue();
    }

    // Deletes data from PersonList in Firebase
    private void deletePersonFromListViewDatabase(String name) {
        FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("PersonList").child(name).removeValue();

    }

}
