package com.alphadvlpr.infiniteminds.navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.alphadvlpr.infiniteminds.R;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This class manages the MainActivity.
 *
 * @author AlphaDvlpr.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * The listener for the NavigationBar. Depending on the clicked item it will load a fragment or another.
     */
    protected BottomNavigationView.OnNavigationItemSelectedListener navigationItemReselectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.menuHome:
                    selectedFragment = new HomeFragment();
                    break;
                case R.id.menuTrend:
                    selectedFragment = new TrendingFragment();
                    break;
                case R.id.menuSearch:
                    selectedFragment = new SearchFragment();
                    break;
                case R.id.menuInfo:
                    selectedFragment = new InfoFragment();
                    break;
                case R.id.menuUsers:
                    selectedFragment = new UsersFragment();
                    break;
            }

            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();

            return true;
        }
    };

    /**
     * This method initializes all the views on this Activity.
     *
     * @param savedInstanceState The previous saved state of the activity.
     * @author AlphaDvlpr.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemReselectedListener);

        MobileAds.initialize(this, "ca-app-pub-2122172706327985~8237512049");

        Intent intent = getIntent();
        String target = intent.getStringExtra("target");

        if (target != null) {
            Fragment selectedFragment = null;

            switch (target) {
                case "TRENDING":
                    selectedFragment = new TrendingFragment();
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                    break;
                case "SEARCH":
                    selectedFragment = new SearchFragment();
                    bottomNavigationView.getMenu().getItem(1).setChecked(true);
                    break;
                case "HOME":
                    selectedFragment = new HomeFragment();
                    bottomNavigationView.getMenu().getItem(2).setChecked(true);
                    break;
                case "INFO":
                    selectedFragment = new InfoFragment();
                    bottomNavigationView.getMenu().getItem(3).setChecked(true);
                    break;
                case "USERS":
                    selectedFragment = new UsersFragment();
                    bottomNavigationView.getMenu().getItem(4).setChecked(true);
                    break;
            }

            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
            bottomNavigationView.getMenu().getItem(2).setChecked(true);
        }
    }
}
