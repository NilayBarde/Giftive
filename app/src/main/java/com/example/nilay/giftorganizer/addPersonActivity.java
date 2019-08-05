package com.example.nilay.giftorganizer;

import android.app.DatePickerDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nilay.giftorganizer.Objects.CalendarEvent;
import com.example.nilay.giftorganizer.Objects.Person;
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

import java.util.ArrayList;
import java.util.Calendar;

public class addPersonActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener { ;

    private AdView mAdView;

    private Button addBtn;
    private EditText personName;
    private EditText budget;
    private EditText eventDate;
    private EditText occasion;

    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private ArrayList<String> currPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        //        MobileAds.initialize(this, "ca-app-pub-1058895947598410/1802975649");
        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("5AF7DA78BC0D4FA32EC0E2C559B83CB8")
                .build();
        mAdView.loadAd(adRequest);


        getSupportActionBar().setTitle("Add a Person");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addBtn = findViewById(R.id.addBttn);
        personName = findViewById(R.id.personName);
        budget = findViewById(R.id.budget);
        eventDate = findViewById(R.id.eventDate);
        currPeople = new ArrayList<>();
        occasion = findViewById(R.id.occasion);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid());

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivityandPassValues();
            }
        });

        eventDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    Person pers = ds.getValue(Person.class);
                     currPeople.add(pers.getName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

    public void openMainActivityandPassValues() {

        if(personName.getText().toString().length() > 0) {
            if (!occasion.getText().toString().isEmpty()) {
                final Person person = new Person();
                person.setName(personName.getText().toString());

                if (eventDate.getText().toString().length() > 0) {
                    person.setDate(eventDate.getText().toString());
                }

                if (budget.getText().toString().isEmpty()) {
                    person.setBudget(0);
                } else {
                    person.setBudget(Double.parseDouble(budget.getText().toString()));
                }

                if (currPeople.contains(person.getName())) {
                    Toast.makeText(this, "Same name is already in the list", Toast.LENGTH_LONG).show();
                } else {
                    person.setOccasion(occasion.getText().toString());
//                    person.setReoccurring(reoccurringCheckBox);
                    addPerson(person);
                    CalendarEvent calendarEvent = new CalendarEvent();
                    calendarEvent.setName(personName.getText().toString());
                    calendarEvent.setEventName(occasion.getText().toString());
                    calendarEvent.setDate(eventDate.getText().toString());
//                    calendarEvent.setReoccurring(reoccurringCheckBox);
                    addEvent(calendarEvent);
                    finish();
                }
            }
            else {
                Toast.makeText(this, "Enter an occasion", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = month+1 + "/" + dayOfMonth + "/" + year;
        eventDate.setText(date);

    }

    private void addPerson(Person person) {
        databaseReference.child("PersonList").child(personName.getText().toString()).setValue(person);
    }

    private void addEvent(CalendarEvent event) {
        databaseReference.child("EventList").child(event.getName() + "'s Event").setValue(event);
    }
}
