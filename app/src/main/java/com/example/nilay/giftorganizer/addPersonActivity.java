package com.example.nilay.giftorganizer;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nilay.giftorganizer.Objects.Person;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class addPersonActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener { ;

    private Button addBtn;
    private EditText personName;
    private EditText budget;
    private EditText eventDate;
    private EditText occasion;
    private CheckBox reoccurring;
    private boolean reoccurringCheckBox;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ArrayList<String> currPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        getSupportActionBar().setTitle("Add a Person");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reoccurringCheckBox = false;
        addBtn = findViewById(R.id.addBttn);
        personName = findViewById(R.id.personName);
        budget = findViewById(R.id.budget);
        eventDate = findViewById(R.id.eventDate);
        currPeople = new ArrayList<>();
        occasion = findViewById(R.id.occasion);
        reoccurring = findViewById(R.id.reoccurringEvent);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivityandPassValues();
            }
        });

        reoccurring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reoccurring.isChecked()) {
                    reoccurringCheckBox = true;
                }
                else {
                    reoccurringCheckBox = false;
                }
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
                DataSnapshot ds = dataSnapshot.child(user.getUid()).child("PersonList");
                for(DataSnapshot dataSnapshot1 : ds.getChildren()) {
                    Person pers = dataSnapshot1.getValue(Person.class);
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
                person.setDate(Calendar.getInstance().getTime());

                if (eventDate.getText().toString().length() > 0) {
                    person.setBirthday(eventDate.getText().toString());
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
                    person.setReoccurring(reoccurringCheckBox);
                    addPerson(person);
                    CalendarEvent calendarEvent = new CalendarEvent();
                    calendarEvent.setName(personName.getText().toString());
                    calendarEvent.setEventName(occasion.getText().toString());
                    calendarEvent.setDate(eventDate.getText().toString());
                    calendarEvent.setReoccurring(reoccurringCheckBox);
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
        databaseReference.child(user.getUid()).child("PersonList").child(personName.getText().toString()).setValue(person);
    }

    private void addEvent(CalendarEvent event) {
        databaseReference.child(user.getUid()).child("EventList").child(event.getName() + "'s Event").setValue(event);
    }
}
