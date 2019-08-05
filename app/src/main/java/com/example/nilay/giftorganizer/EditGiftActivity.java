package com.example.nilay.giftorganizer;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nilay.giftorganizer.Objects.Gift;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditGiftActivity extends AppCompatActivity {

    private AdView mAdView;

    private EditText nameEditText;
    private EditText priceEditText;
    private Button updateButton;

    private String GiftName;
    private String PersonName;
    private Double price;
    private Gift currGift;

    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_gift);

//        //        MobileAds.initialize(this, "ca-app-pub-1058895947598410/1802975649");
//        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("5AF7DA78BC0D4FA32EC0E2C559B83CB8")
//                .build();
//        mAdView.loadAd(adRequest);


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

        getSupportActionBar().setTitle("Edit " + GiftName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid());

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nameEditText.getText().toString().isEmpty()) {
                    updateGiftData();
                    openActivity();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please enter a gift", Toast.LENGTH_SHORT).show();
                }

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
