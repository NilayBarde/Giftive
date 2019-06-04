package com.example.nilay.giftorganizer;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nilay.giftorganizer.Objects.Gift;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class addGiftActivity extends AppCompatActivity {

    private Button addBtn;
    private EditText giftName;
    private EditText price;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference2;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gift);

        getSupportActionBar().setTitle("Add a Gift");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        name = b.getString("Name", "");

        database = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

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
            gift.setDate(Calendar.getInstance().getTime());
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
