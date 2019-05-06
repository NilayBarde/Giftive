package com.example.nilay.giftorganizer;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private FloatingActionButton floatingActionButton;


    private FragmentSchedule scheduleFragment;
    public FragmentGiftList giftListFragment;
    private FragmentShop shopFragment;

    private ViewPager viewPager;
    private MenuItem prevMenuItem;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainFrame = (FrameLayout) findViewById(R.id.mainFrame);
        mMainNav = (BottomNavigationView) findViewById(R.id.mainNav);

        floatingActionButton = findViewById(R.id.fab_1);

        scheduleFragment = new FragmentSchedule();
        giftListFragment = new FragmentGiftList();
        shopFragment = new FragmentShop();

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        databaseReference = FirebaseDatabase.getInstance().getReference();



        floatingActionButton.setOnClickListener(    new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddPersonActivity();

            }
        });


        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.nav_giftList:
                        setFragment(giftListFragment);
                        return true;

                    case R.id.nav_shop:
                        setFragment(shopFragment);
                        return true;

                    case R.id.nav_schedule:
                        setFragment(scheduleFragment);
                        return true;

                    default:
                        return false;


                }
            }

        });

        mMainNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.nav_giftList:
                                viewPager.setCurrentItem(0);
                                break;

                            case R.id.nav_shop:
                                viewPager.setCurrentItem(1);
                                break;

                            case R.id.nav_schedule:
                                viewPager.setCurrentItem(2);
                                break;

                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    mMainNav.getMenu().getItem(0).setChecked(false);

                mMainNav.getMenu().getItem(position).setChecked(true);
                prevMenuItem = mMainNav.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);

            }


    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.mainFrame, fragment);
        fragmentTransaction.commit();

    }

    public void openAddPersonActivity() {
        Intent intent = new Intent(this, addPersonActivity.class);
        startActivity(intent);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(giftListFragment);
        adapter.addFragment(shopFragment);
        adapter.addFragment(scheduleFragment);
        viewPager.setAdapter(adapter);
    }
}
