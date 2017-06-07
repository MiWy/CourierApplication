package com.tryouts.courierapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.tryouts.courierapplication.firebaseservices.MyFirebaseMessagingService;
import com.tryouts.courierapplication.fragments.AboutFragment;
import com.tryouts.courierapplication.fragments.CurrentOrderFragment;
import com.tryouts.courierapplication.fragments.NewOrderFragment;
import com.tryouts.courierapplication.fragments.PreviousOrdersFragment;
import com.tryouts.courierapplication.fragments.PricelistFragment;
import com.tryouts.courierapplication.fragments.ProfileEditFragment;
import com.tryouts.courierapplication.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    private static final String PREFERENCES_NAME = "SharePref";
    private static final String PREFERENCES_TEXT_FIELD = "orderTimeStamp";
    private static final String PREFERENCES_EMPTY = "empty";
    private SharedPreferences mSharedPreferences;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set SharedPreferences
        mSharedPreferences = getSharedPreferences(PREFERENCES_NAME, Activity.MODE_PRIVATE);
        // Set a toolbar to replace the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Find drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Tie DrawerLayout events to the ActionBarToggle.
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);

        // Attach listener to drawer menuitems and handle user selections.
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.nvView);
        //View drawerHeader = nvDrawer.inflateHeaderView(R.layout.drawer_header);
        setupDrawerContent(nvDrawer);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {
                    if(getIntent().getExtras() == null) {
                        // Begin transaction
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        // Replace the contents of the container with the new fragment
                        ft.replace(R.id.fragment_placeholder, new NewOrderFragment());
                        // Complete the changes added above.
                        ft.commit();
                    } else {
                        Bundle extras = getIntent().getExtras();
                        if(extras.containsKey(MyFirebaseMessagingService.NOTIFICATION_TYPE)) {
                            String type = extras.getString(MyFirebaseMessagingService.NOTIFICATION_TYPE);
                            if (type.equals("new") || type.equals("taken")) {
                                saveData(extras.getString(MyFirebaseMessagingService.NOTIFICATION_TIMESTAMP));
                                Class fragmentClass = CurrentOrderFragment.class;
                                Fragment fragment = null;
                                try {
                                    fragment = (Fragment) fragmentClass.newInstance();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.fragment_placeholder, fragment);
                                ft.commit();
                            } else if(type.equals("finished")) {
                                saveData(PREFERENCES_EMPTY);
                                // Begin transaction
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                // Replace the contents of the container with the new fragment
                                ft.replace(R.id.fragment_placeholder, new ProfileFragment());
                                // Complete the changes added above.
                                ft.commit();
                            }
                        } else {
                            // Begin transaction
                            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            // Replace the contents of the container with the new fragment
                            ft.replace(R.id.fragment_placeholder, new NewOrderFragment());
                            // Complete the changes added above.
                            ft.commit();
                        }
                    }
                } else {
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        };
    }

    private void saveData(String data) {
        SharedPreferences.Editor preferencesEditor = mSharedPreferences.edit();
        preferencesEditor.putString(PREFERENCES_TEXT_FIELD, data);
        preferencesEditor.commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (FirebaseDatabase.getInstance() != null)
        {
            FirebaseDatabase.getInstance().goOnline();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(FirebaseDatabase.getInstance()!=null)
        {
            FirebaseDatabase.getInstance().goOffline();
        }
    }

    // Attach item selection listener to the drawer
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        selectDrawerItem(item);
                        return true;
                    }
                }
        );
    }

    // Creates a fragment and specifies it (according to selected menuItem), then shows it.
    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_profile:
                fragmentClass = ProfileFragment.class;
                break;
            case R.id.nav_profileedit:
                fragmentClass = ProfileEditFragment.class;
                break;
            case R.id.nav_pricelist:
                fragmentClass = PricelistFragment.class;
                break;
            case R.id.nav_about:
                fragmentClass = AboutFragment.class;
                break;
            case R.id.nav_neworder:
                fragmentClass = NewOrderFragment.class;
                break;
            case R.id.nav_previous_orders:
                fragmentClass = PreviousOrdersFragment.class;
                break;
            case R.id.nav_current_order:
                fragmentClass = CurrentOrderFragment.class;
                break;
            case R.id.nav_logout:
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                mAuth.signOut();
            default:
                fragmentClass = NewOrderFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, fragment).commit();

        // Highlight the selected item
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    /**
     *
     * Actionbar/toolbar and drawer relations established here.
     *
     */

    // Actionbar/Toolbar home/up button opens/close the drawer
    // Allowing ActionBarToggle to handle the events.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Ties actionbar/toolbar with drawerlayout
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    // Needed to show the hamburger icon
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
    }
}
