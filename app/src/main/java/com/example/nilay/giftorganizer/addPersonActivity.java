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

public class addPersonActivity extends AppCompatActivity {

    public static final String MY_CUSTOM_FRAGMENT_KEY = "CUSTOM TEXT";

    Button addBtn;
    EditText personName;
    EditText budget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        getSupportActionBar().setTitle("Add a Person");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addBtn = findViewById(R.id.addBttn);
        personName = findViewById(R.id.personName);
        budget = findViewById(R.id.budget);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCustomFragment();
            }
        });
    }

    private Fragment createCustomFragment() {
        Bundle bundle = new Bundle();
        bundle.putString(MY_CUSTOM_FRAGMENT_KEY, "Hello There");

        FragmentGiftList fragmentGiftList = new FragmentGiftList();
        fragmentGiftList.setArguments(bundle);
        return fragmentGiftList;
    }



}
