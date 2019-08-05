package com.example.nilay.giftorganizer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

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
//import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private FloatingActionButton floatingActionButton;

    private FragmentSchedule scheduleFragment;
    public FragmentGiftList giftListFragment;
    private FragmentShop shopFragment;

    private ViewPager viewPager;
    private MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        MobileAds.initialize(this, "ca-app-pub-1058895947598410/1802975649");
//        MobileAds.initialize(this, "ca-app-pub-3940256099942544/6300978111");
//        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("5AF7DA78BC0D4FA32EC0E2C559B83CB8")
//                .build();
//        mAdView.loadAd(adRequest);

        mMainFrame = (FrameLayout) findViewById(R.id.mainFrame);
        mMainNav = (BottomNavigationView) findViewById(R.id.mainNav);
        floatingActionButton = findViewById(R.id.fab_1);
        scheduleFragment = new FragmentSchedule();
        giftListFragment = new FragmentGiftList();
        shopFragment = new FragmentShop();
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
                unregisterReceiver(this);
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
                FirebaseAuth.getInstance().signOut();
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("com.package.ACTION_LOGOUT");
                sendBroadcast(broadcastIntent);
                AuthUI.getInstance()
                        .signOut(getApplicationContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
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

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(giftListFragment);
        adapter.addFragment(shopFragment);
        adapter.addFragment(scheduleFragment);
        viewPager.setAdapter(adapter);
    }
}
