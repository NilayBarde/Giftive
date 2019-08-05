package com.example.nilay.giftorganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addGiftActivity extends AppCompatActivity {

    private AdView mAdView;

    private Button addBtn;
    private EditText giftName;
    private EditText price;

    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gift);

//        // MobileAds.initialize(this, "ca-app-pub-1058895947598410/1802975649");
//        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("5AF7DA78BC0D4FA32EC0E2C559B83CB8")
//                .build();
//        mAdView.loadAd(adRequest);


        getSupportActionBar().setTitle("Add a Gift");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        name = b.getString("Name", "");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        addBtn = findViewById(R.id.addBttn);
        giftName = findViewById(R.id.giftName);
        price = findViewById(R.id.price);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGiftListAndPassValues();
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    public void openGiftListAndPassValues() {
        if(giftName.getText().toString().length() > 0) {
            Gift gift = new Gift();
            gift.setName(giftName.getText().toString());
            gift.setBought(false);
            if(TextUtils.isEmpty(price.getText().toString())) {
                gift.setPrice(0.00);
            }
            else {
                gift.setPrice(Double.parseDouble(price.getText().toString()));
            }

            databaseReference.child(user.getUid()).child("GiftsList").child(name + " Gifts").child(gift.getName()).setValue(gift);
            finish();

        }
        else if(TextUtils.isEmpty(giftName.getText().toString())) {
            Toast.makeText(this, "Enter a gift", Toast.LENGTH_SHORT).show();
        }
    }
}
