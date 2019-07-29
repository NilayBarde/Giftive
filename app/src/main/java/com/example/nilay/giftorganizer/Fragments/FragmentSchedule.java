package com.example.nilay.giftorganizer.Fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nilay.giftorganizer.Objects.CalendarEvent;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSchedule extends Fragment {


    public FragmentSchedule() {
        // Required empty public constructor
    }

    private ListView calendarEventListView;
    private ArrayAdapter<Event> calendarEventListViewAdapter;
    private TextView monthYear;
    private CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonthYear = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd YYYY", Locale.getDefault());
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private HashMap<String, ArrayList<Event>> allEvents;
    private ArrayList<Event> currMonthEvents;
    private ArrayList<CalendarEvent> calendarEventList;
    private TextView calendarList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_schedule, container, false);

        calendarEventListView = view.findViewById(R.id.calendarEventListView);
        allEvents = new HashMap<>();
        currMonthEvents = new ArrayList<>();
        calendarEventList = new ArrayList<>();

        calendarList = view.findViewById(R.id.calendarList);
        monthYear = view.findViewById(R.id.monthYear);
        compactCalendar = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        compactCalendar.setFirstDayOfWeek(Calendar.MONDAY);

        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid()).child("EventList");

        Calendar cal = Calendar.getInstance();
        String month_name = dateFormatMonthYear.format(cal.getTime());
        monthYear.setText(month_name);

        calendarEventListViewAdapter = new ArrayAdapter<Event>(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, currMonthEvents) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                Collections.sort(currMonthEvents, new Comparator<Event>() {
                    @Override
                    public int compare(Event e1, Event e2) {
                        if(e1.getTimeInMillis() < e2.getTimeInMillis()) {
                            return -1;
                        }
                        else if (e1.getTimeInMillis() == e2.getTimeInMillis()) {
                            return 0;
                        }
                        else {
                            return 1;
                        }
                    }
                });
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                DecimalFormat df = new DecimalFormat("#0.00");
                text1.setText(currMonthEvents.get(position).getData().toString());
                Date date = new Date(currMonthEvents.get(position).getTimeInMillis());
                text2.setText(dateFormat.format(date));
                return view;
            }
        };

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                calendarEventList.clear();
                compactCalendar.removeAllEvents();
                allEvents.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    CalendarEvent ce = ds.getValue(CalendarEvent.class);
                    if(!(ce.getDate().isEmpty())) {
                        ArrayList<Event> eventsListReoccuring = new ArrayList<>();
                        ArrayList<Event> eventsList = new ArrayList<>();
                        int year = Calendar.getInstance().get(Calendar.YEAR);
//                        if(ce.isReoccurring()) {
//                            calendarEventList.add((ce));
//                        }
//                        if(ce.isReoccurring() && !(ce.getDate().substring(ce.getDate().length()-4).equals(Integer.toString(year)))) {
//                            Long milliPlusOne = calendarEventToEpoch(ce) + 31556952000L * (year - Integer.parseInt(ce.getDate().substring(ce.getDate().length()-4)));
//                            Event ev1 = new Event(Color.RED, milliPlusOne, ce.getName() + "'s " + ce.getEventName());
//                            compactCalendar.addEvent(ev1);
//                            Date date = new Date(milliPlusOne);
//                            if(allEvents.containsKey(date.getMonth() + " " + date.getYear())) {
//                                eventsListReoccuring = allEvents.get(date.getMonth() + " " + date.getYear());
//                                eventsListReoccuring.add(ev1);
//                            }
//                            else {
//                                eventsListReoccuring.add(ev1);
//                            }
//                            allEvents.put(date.getMonth() + " " + date.getYear(), eventsListReoccuring);
//
//
//                        }
                        Long milli = calendarEventToEpoch(ce);
                        Log.d("EpochCheck", milli + "");
                        Event ev1 = new Event(Color.RED, milli, ce.getName() + "'s " + ce.getEventName());
                        compactCalendar.addEvent(ev1);
                        Date date = new Date(milli);
                        if(allEvents.containsKey(date.getMonth() + " " + date.getYear())) {
                            eventsList = allEvents.get(date.getMonth() + " " + date.getYear());
                            eventsList.add(ev1);
                        }
                        else {
                            eventsList.add(ev1);
                        }
                        allEvents.put(date.getMonth() + " " + date.getYear(), eventsList);

                    }
                }
                Calendar cal = Calendar.getInstance();
                Date d = new Date(cal.getTimeInMillis());
                if(allEvents.containsKey(d.getMonth() + " " + d.getYear())) {
                    for (Event ev : allEvents.get(d.getMonth() + " " + d.getYear())) {
                        currMonthEvents.add(ev);
                    }
                }
                Log.d("allEventsSize", d.getMonth() + "");
                Log.d("allEventsSize", allEvents.get(5) + "");
                Log.d("allEventsSize", currMonthEvents.size() + "");
//                Log.d("allEventsSize", allEvents.size() + "");

                if(!(currMonthEvents.isEmpty())) {
                    calendarList.setText("");
                }
                else {
                    calendarList.setText("There are no gifts to be bought for this month!\n\n Add a date to a person to add them to this list!");
                }

                calendarEventListView.setAdapter(calendarEventListViewAdapter);
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
                    if(compactCalendar.getEvents(dateClicked).size() > 1) {
                        Toast.makeText(context, compactCalendar.getEvents(dateClicked).size() + " events on this day", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, compactCalendar.getEvents(dateClicked).size() + " event on this day", Toast.LENGTH_SHORT).show();
                    }
                }
                Log.d("EVENTS", "" + compactCalendar.getEventsForMonth(dateClicked).size());
                Log.d("EVENTS", "" + dateClicked.getMonth());
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                currMonthEvents.clear();
                monthYear.setText(dateFormatMonthYear.format(firstDayOfNewMonth));
                Log.d("Nilay",""+ firstDayOfNewMonth.getYear());
//                if(calendarEventList.size() > 0) {
//                    if (firstDayOfNewMonth.getYear() + 1900 > Calendar.getInstance().get(Calendar.YEAR)) {
//                        for(CalendarEvent ce : calendarEventList) {
//                            Date ceDate = new Date(calendarEventToEpoch(ce));
//                            ArrayList<Event> reoccuringEventsList = new ArrayList<>();
//                            if(allEvents.containsKey(ceDate.getMonth() + " " + ceDate.getYear())) {
//                                if (!(allEvents.get(ceDate.getMonth() + " " + ceDate.getYear()).contains(ce))) {
//                                    reoccuringEventsList = allEvents.get(ceDate.getMonth() + " " + ceDate.getYear());
//                                    int year = Calendar.getInstance().get(Calendar.YEAR);
//                                    Long milliPlusOne = calendarEventToEpoch(ce) + 31556952000L * (year - Integer.parseInt(ce.getDate().substring(ce.getDate().length() - 4)));
//                                    Event ev1 = new Event(Color.RED, milliPlusOne, ce.getName() + "'s " + ce.getEventName());
//                                    compactCalendar.addEvent(ev1);
//                                    reoccuringEventsList.add(ev1);
//
//
//                                }
//                            }
//                            else {
//                                int year = Calendar.getInstance().get(Calendar.YEAR);
//                                Long milliPlusOne = calendarEventToEpoch(ce) + 31556952000L * (year - Integer.parseInt(ce.getDate().substring(ce.getDate().length() - 4)));
//                                Event ev1 = new Event(Color.RED, milliPlusOne, ce.getName() + "'s " + ce.getEventName());
//                                compactCalendar.addEvent(ev1);
//                                reoccuringEventsList.add(ev1);
//
//
//
//                            }
//                            allEvents.put(ceDate.getMonth() + " " + ceDate.getYear(), reoccuringEventsList);
//
//                        }
//
//
//                    }
//                }

//                if(calendarEventList.size() > 0) {
//                    if (firstDayOfNewMonth.getYear() + 1900 > Calendar.getInstance().get(Calendar.YEAR)) {
//                        int year = Calendar.getInstance().get(Calendar.YEAR);
//                        ArrayList<Event> eventsList = new ArrayList<>();
//                        for (CalendarEvent ce : calendarEventList) {
//                            Long milliPlusOne = calendarEventToEpoch(ce) + 31556952000L;
//                            Event ev1 = new Event(Color.RED, milliPlusOne, ce.getName() + "'s " + ce.getEventName());
//                            compactCalendar.addEvent(ev1);
//                            Date date = new Date(milliPlusOne);
//                            if (allEvents.containsKey(date.getMonth() + " " + date.getYear())) {
//                                eventsList = allEvents.get(date.getMonth() + " " + date.getYear());
//                                eventsList.add(ev1);
//                            } else {
//                                eventsList.add(ev1);
//                            }
//                            allEvents.put(date.getMonth() + " " + date.getYear(), eventsList);
//                        }
//                    }
//                }

                if(allEvents.containsKey(firstDayOfNewMonth.getMonth() + " " + firstDayOfNewMonth.getYear())) {
                    for (Event ev : allEvents.get(firstDayOfNewMonth.getMonth() + " " + firstDayOfNewMonth.getYear())) {
                        currMonthEvents.add(ev);
                    }
                }

                if(!(currMonthEvents.isEmpty())) {
                    calendarList.setText("");
                }
                else {
                    calendarList.setText("There are no gifts to be bought for this month!\n\n Add a date to a person to add them to this list!");
                }

                calendarEventListView.setAdapter(calendarEventListViewAdapter);
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
            return difference + 86400000L;

        } catch (Exception exception) {
            return null;
        }
    }
}
