package com.example.nilay.giftorganizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nilay.giftorganizer.Fragments.FragmentGiftList;
import com.example.nilay.giftorganizer.Objects.Gift;
import com.example.nilay.giftorganizer.Objects.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class GiftItems extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ArrayList<Gift> giftItems;
    private ArrayAdapter<Gift> adapter;
    private ListView listView;
    private String name;
    private Person currPerson;
    private Gift currGift;
    private double giftSum;
    private AlertDialog.Builder builder;
    private FloatingActionButton floatingActionButton;
    private TextView instructionsGift;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_items);

        currPerson = new Person();
        currGift = new Gift();
        giftItems = new ArrayList<Gift>();
        listView = findViewById(R.id.giftlistview);
        instructionsGift = findViewById(R.id.instructionsGifts);

        Bundle b = getIntent().getExtras();
        name = b.getString("Name", "");
        builder = new AlertDialog.Builder(this);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = database.getReference(user.getUid());
        floatingActionButton = findViewById(R.id.fabGift);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGiftList();
            }
        });


        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, giftItems) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(giftItems.get(position).getName());
                if(giftItems.get(position).getPrice() % 1 == 0) {
                    text2.setText("$" + giftItems.get(position).getPrice().toString() + "0");
                }
                else {
                    text2.setText("$" + giftItems.get(position).getPrice().toString());
                }
                return view;
            }
        };

        listView.setAdapter(adapter);

        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                giftItems.clear();
                showData(dataSnapshot);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            String [] menuItems = getResources().getStringArray(R.array.menu);
            for(int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
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
            builder.setMessage("Are you sure you want to delete " + giftItems.get(info.position).getName() + " from " + name + "'s gift list?");
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(this, String.format("%s was deleted from your gift list", giftItems.get(info.position).getName()), Toast.LENGTH_LONG).show();
                    deleteGiftsFromListViewDatabase(name, giftItems.get(info.position).getName());
                    giftItems.remove(info.position);
                    if(giftItems.size() == 0) {
                        instructionsGift.setText("There are no gifts in your list for this person yet!\n\n Add a person using the button below!");
                    }
                    dialog.dismiss();
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
            Intent intent = new Intent(this, EditGiftActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("PersonName", name);
            bundle.putString("GiftName", giftItems.get(info.position).getName());
            bundle.putDouble("Price", giftItems.get(info.position).getPrice());
            intent.putExtras(bundle);
            startActivity(intent);

        }
        listView.setAdapter(adapter);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
//        switch (item.getItemId()) {
//            default:
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                return true;
        }

    private void showData(DataSnapshot dataSnapshot) {
        DataSnapshot dataSnapshot2 = dataSnapshot.child("GiftsList" + "/" + name + " Gifts" + "/");

        for(DataSnapshot ds : dataSnapshot2.getChildren()) {
            currGift = ds.getValue(Gift.class);
            giftItems.add(currGift);
        }
        Collections.sort(giftItems);
        giftSum = 0;
        if(!(giftItems.isEmpty())) {
            for (Gift gift : giftItems) {
                giftSum = giftSum + gift.getPrice();
            }
            instructionsGift.setText("");
        }

        DataSnapshot dataSnapshot1 = dataSnapshot.child("PersonList" + "/" + name + "/");
            currPerson = dataSnapshot1.getValue(Person.class);
        if(!(currPerson == null)) {
            currPerson.setBought(giftSum);
            Log.d("SizeGift", "Value" + giftSum);
            databaseReference.child("PersonList").child(name).setValue(currPerson);

        }
    }

    private void deleteGiftsFromListViewDatabase(String name, String gift){
        database.getReference().child(user.getUid()).child("GiftsList").child(name + " Gifts").child(gift).removeValue();
    }

    private void openGiftList() {
        Intent intent = new Intent(this, addGiftActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Name", name);
        intent.putExtras(bundle);
        startActivity(intent);

    }

}
