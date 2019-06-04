package com.example.nilay.giftorganizer.Fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nilay.giftorganizer.CalendarEvent;
import com.example.nilay.giftorganizer.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.EventListener;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSchedule extends Fragment {


    public FragmentSchedule() {
        // Required empty public constructor
    }

    private TextView monthYear;
    private CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseDatabase database;
    private Date date1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_schedule, container, false);

        monthYear = view.findViewById(R.id.monthYear);
        compactCalendar = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = database.getReference(user.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot dataSnapshot1 = dataSnapshot.child("EventList");
                for(DataSnapshot ds : dataSnapshot1.getChildren()) {
                    CalendarEvent ce = ds.getValue(CalendarEvent.class);
                    if(!(ce.getDate().isEmpty())) {
                        Long milli = calendarEventToEpoch(ce);
                        Event ev1 = new Event(Color.RED, milli, ce.getName() + "'s " + ce.getEventName());
                        compactCalendar.addEvent(ev1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getContext();
                int month = dateClicked.getMonth();
                int day = dateClicked.getDate();
                int year = dateClicked.getYear() + 1900;

                if(compactCalendar.getEvents(dateClicked).isEmpty()) {
                    Toast.makeText(context, "No Events", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, compactCalendar.getEvents(dateClicked).get(0).getData().toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                monthYear.setText(dateFormatMonth.format(firstDayOfNewMonth));

            }
        });


        return view;
    }

    private Long calendarEventToEpoch(CalendarEvent ce) {
        try {
            //Dates to compare
            String CurrentDate = "01/01/1970";
            String FinalDate = ce.getDate();

            Date date1;
            Date date2;

            SimpleDateFormat dates = new SimpleDateFormat("MM/dd/yyyy");

            //Setting dates
            date1 = dates.parse(CurrentDate);
            date2 = dates.parse(FinalDate);

            //Comparing dates
            long difference = Math.abs(date1.getTime() - date2.getTime());
            long differenceDates = difference / (24 * 60 * 60 * 1000);
            return differenceDates * 86400000L + 172800000;

        } catch (Exception exception) {
            return null;
        }
    }
}
