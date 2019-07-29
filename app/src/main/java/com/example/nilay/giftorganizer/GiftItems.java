package com.example.nilay.giftorganizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nilay.giftorganizer.Objects.Gift;
import com.example.nilay.giftorganizer.Objects.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class GiftItems extends AppCompatActivity {

    private AdView mAdView;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
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

        //        MobileAds.initialize(this, "ca-app-pub-1058895947598410/1802975649");
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("5AF7DA78BC0D4FA32EC0E2C559B83CB8")
                .build();
        mAdView.loadAd(adRequest);


        currPerson = new Person();
        currGift = new Gift();
        giftItems = new ArrayList<Gift>();
        listView = findViewById(R.id.giftlistview);
        instructionsGift = findViewById(R.id.instructionsGifts);

        Bundle b = getIntent().getExtras();
        name = b.getString("Name", "");
        builder = new AlertDialog.Builder(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid()).child("GiftsList").child(name + " Gifts");
        databaseReference2 = FirebaseDatabase.getInstance().getReference(user.getUid()).child("PersonList").child(name).child("bought");
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
                DecimalFormat df = new DecimalFormat("#0.00");
                text1.setText(giftItems.get(position).getName());
                text2.setText("$" + df.format(giftItems.get(position).getPrice()));
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
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                return true;
        }

    private void showData(DataSnapshot dataSnapshot) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                currGift = ds.getValue(Gift.class);
                giftItems.add(currGift);
            }
            Collections.sort(giftItems);
            giftSum = 0;
            if (!(giftItems.isEmpty())) {
                for (Gift gift : giftItems) {
                    giftSum = giftSum + gift.getPrice();
                }
                instructionsGift.setText("");
            }

            databaseReference2.setValue(giftSum);

        }

    private void deleteGiftsFromListViewDatabase(String name, String gift){
        FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("GiftsList").child(name + " Gifts").child(gift).removeValue();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }


    private void openGiftList() {
        Intent intent = new Intent(this, addGiftActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Name", name);
        intent.putExtras(bundle);
        startActivity(intent);

    }

}
