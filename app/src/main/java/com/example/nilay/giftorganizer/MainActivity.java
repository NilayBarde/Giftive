package com.example.nilay.giftorganizer;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.nilay.giftorganizer.AccountActivity.LoginActivity;
import com.example.nilay.giftorganizer.CustomAdapters.ViewPagerAdapter;
import com.example.nilay.giftorganizer.Fragments.FragmentGiftList;
import com.example.nilay.giftorganizer.Fragments.FragmentSchedule;
import com.example.nilay.giftorganizer.Fragments.FragmentShop;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private FloatingActionButton floatingActionButton;

    private FragmentSchedule scheduleFragment;
    public FragmentGiftList giftListFragment;
    private FragmentShop shopFragment;

    private ViewPager viewPager;
    private MenuItem prevMenuItem;

    private FirebaseAuth firebaseAuth;

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
        firebaseAuth = FirebaseAuth.getInstance();
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        getSupportActionBar().setTitle("Home");


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

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        }, intentFilter);

        setupViewPager(viewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.nav_sections, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.signout:
                Toast.makeText(this, "Signed Out", Toast.LENGTH_SHORT).show();
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.package.ACTION_LOGOUT");
                sendBroadcast(broadcastIntent);
                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
        }

        return true;
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
