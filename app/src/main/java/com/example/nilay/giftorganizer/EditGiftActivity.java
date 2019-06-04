package com.example.nilay.giftorganizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.nilay.giftorganizer.Objects.Gift;
import com.example.nilay.giftorganizer.Objects.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditGiftActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText priceEditText;
    private Button updateButton;

    private String GiftName;
    private String PersonName;
    private Double price;
    private Gift currGift;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gift);

        nameEditText = findViewById(R.id.editGiftName);
        priceEditText = findViewById(R.id.editPrice);
        updateButton = findViewById(R.id.updateBttnGift);

        Bundle b = getIntent().getExtras();
        PersonName = b.getString("PersonName", "");
        GiftName = b.getString("GiftName", "");
        price = b.getDouble("Price", 0);
        nameEditText.setText(GiftName);
        priceEditText.setText(price.toString());

        currGift = new Gift();
        progressDialog = new ProgressDialog(this);

        getSupportActionBar().setTitle("Edit " + GiftName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid());

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Updating...");
                progressDialog.show();
                updateGiftData();
                openActivity();
                progressDialog.dismiss();
            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataSnapshot1 = dataSnapshot.child("GiftsList").child(PersonName + " Gifts").child(GiftName);
                currGift = dataSnapshot1.getValue(Gift.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    public boolean onOptionsItemSelected(MenuItem item){
        openActivity();
        return true;
    }

    private void updateGiftData() {
        currGift.setName(nameEditText.getText().toString());
        currGift.setPrice(Double.parseDouble(priceEditText.getText().toString()));
        databaseReference.child("GiftsList").child(PersonName + " Gifts").child(GiftName).removeValue();
        databaseReference.child("GiftsList").child(PersonName + " Gifts").child(currGift.getName()).setValue(currGift);
    }

    private void openActivity() {
        Intent intent = new Intent(this, GiftItems.class);
        Bundle bundle = new Bundle();
        bundle.putString("Name", PersonName);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}
