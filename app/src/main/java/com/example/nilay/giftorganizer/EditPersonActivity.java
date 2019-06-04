package com.example.nilay.giftorganizer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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

import java.util.ArrayList;
import java.util.Calendar;

public class EditPersonActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private EditText nameEditText;
    private EditText budgetEditText;
    private EditText birthdayEditText;
    private EditText occasionEditText;
    private CheckBox reoccurringEvent;
    private Button updateButton;

    private String name;
    private Double budget;
    private String occasion;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private Person currPerson;
    private Gift currGift;
    private ArrayList<Gift> giftList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);

        nameEditText = findViewById(R.id.editPersonName);
        budgetEditText = findViewById(R.id.editBudget);
        birthdayEditText = findViewById(R.id.EditEventDate);
        occasionEditText = findViewById(R.id.editOccasion);
        reoccurringEvent = findViewById(R.id.reoccurringEvent);
        updateButton = findViewById(R.id.updateBttnPerson);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid());

        currPerson = new Person();
        currGift = new Gift();
        giftList = new ArrayList<Gift>();

        Bundle b = getIntent().getExtras();
        name = b.getString("Name", "");
        budget = b.getDouble("Budget", 0);
        nameEditText.setText(name);
        budgetEditText.setText(budget.toString());

        getSupportActionBar().setTitle("Edit " + name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePeopleData();
                updateEvents();
                if(!(nameEditText.getText().toString().equals(name))) {
                    updateGiftListData();
                }
                openActivity();
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot ds = dataSnapshot.child("PersonList" + "/" + name + "/");
                currPerson = ds.getValue(Person.class);
                if(!(currPerson.getBirthday().equals("0"))) {
                    birthdayEditText.setText(currPerson.getBirthday());
                }
                occasion = currPerson.getOccasion();
                occasionEditText.setText(currPerson.getOccasion());
                    reoccurringEvent.setChecked(currPerson.isReoccurring());
                DataSnapshot dataSnapshot1 = dataSnapshot.child("GiftsList").child(name + " Gifts");

                for(DataSnapshot ds1 : dataSnapshot1.getChildren()) {
                    currGift = ds1.getValue(Gift.class);
                    giftList.add(currGift);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateEvents() {
        CalendarEvent currEvent = new CalendarEvent();
        currEvent.setDate(birthdayEditText.getText().toString());
        currEvent.setEventName(occasionEditText.getText().toString());
        currEvent.setName(nameEditText.getText().toString());
        currEvent.setReoccurring(reoccurringEvent.isChecked());
        databaseReference.child("EventList").child(name + "'s Event").removeValue();
        databaseReference.child("EventList").child(currEvent.getName() + "'s Event").setValue(currEvent);

    }

    private void updatePeopleData() {

        currPerson.setName(nameEditText.getText().toString());
        currPerson.setBudget(Double.parseDouble(budgetEditText.getText().toString()));
        currPerson.setOccasion(occasionEditText.getText().toString());
        if(birthdayEditText.getText().toString().isEmpty()) {
            currPerson.setBirthday("0");
        }
        else {
            currPerson.setBirthday(birthdayEditText.getText().toString());
        }
        currPerson.setReoccurring(reoccurringEvent.isChecked());
        databaseReference.child("PersonList").child(name).removeValue();
        databaseReference.child("PersonList").child(currPerson.getName()).setValue(currPerson);

    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    private void updateGiftListData() {
        for(Gift g : giftList) {
            databaseReference.child("GiftsList").child(currPerson.getName() + " Gifts").child(g.getName()).setValue(g);
        }
        databaseReference.child("GiftsList").child(name + " Gifts").removeValue();

    }

    private void openActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = month+1 + "/" + dayOfMonth + "/" + year;
        birthdayEditText.setText(date);

    }
}

