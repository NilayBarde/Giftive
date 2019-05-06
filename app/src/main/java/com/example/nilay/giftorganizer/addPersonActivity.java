package com.example.nilay.giftorganizer;

import android.content.Intent;
import android.support.constraint.Placeholder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class addPersonActivity extends AppCompatActivity { ;

    public static final String MY_CUSTOM_FRAGMENT_KEY = "CUSTOM TEXT";

    private Button addBtn;
    private EditText personName;
    private EditText budget;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        getSupportActionBar().setTitle("Add a Person");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addBtn = findViewById(R.id.addBttn);
        personName = findViewById(R.id.personName);
        budget = findViewById(R.id.budget);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivityandPassValues();
            }
        });

    }

    public void openMainActivityandPassValues() {
        Intent intent = new Intent(this, MainActivity.class);

        if(personName.getText().toString().length() > 0) {

            Person person = new Person();
            person.setName(personName.getText().toString());
            if(budget.getText().toString().isEmpty()) {
                person.setBudget(0);
            }
            else {
                person.setBudget(Double.parseDouble(budget.getText().toString()));
            }
            databaseReference.child("GiftGivingList").child(personName.getText().toString()).setValue(person);
        }
//        }

        startActivity(intent);
    }

}
