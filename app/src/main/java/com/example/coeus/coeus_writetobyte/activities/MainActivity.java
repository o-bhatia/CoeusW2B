package com.example.coeus.coeus_writetobyte.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.coeus.coeus_writetobyte.R;
import com.example.coeus.coeus_writetobyte.fragments.HomeFragment;
import com.example.coeus.coeus_writetobyte.fragments.MyFilesFragment;
import com.example.coeus.coeus_writetobyte.fragments.ScannerFragment;
import com.example.coeus.coeus_writetobyte.fragments.UserPreferencesFragment;
import com.example.coeus.coeus_writetobyte.fragments.WhatIsNewFragment;
import com.example.coeus.coeus_writetobyte.managers.PreferenceManager;
import com.example.coeus.coeus_writetobyte.models.GmailUser;
import com.example.coeus.coeus_writetobyte.utils.Constants;
import com.example.coeus.coeus_writetobyte.utils.OnSwipeTouchListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;

import proccessingPackage.IO;

/**
 * Description: This class contains all functional methods
 * used within Coeus, spanning across several activities,
 * including establishing the nav drawer and handling login
 * and logout requests for example.
 *
 * Author: Ojas Bhatia
 *
 * Last updated: January 10, 2019
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ScannerFragment.OnScannerFragmentInteractionListener {

    //declaring components for MainActivity

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private GoogleSignInClient mGoogleSignInClient;
    public static String personName;
    public static String personEmail;

    private String photoUriString;

    public static boolean permission;
    private final int MY_PERMISSIONS_REQUEST = 10;

    //onActivityResult deals with request codes relating to Camera and Editing screen and creates new instances of
    //the ScannerFragment if the request is valid
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ScannerFragment.CAMERA_REQUEST_CODE || requestCode == ScannerFragment.EDITING_SCREEN_REQUEST_CODE)
                && resultCode == RESULT_OK) {
            //create new instance of Scanner Fragment
            Fragment fragment = ScannerFragment.newInstance();
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    //onCreate method initializes main components of Coeus,
    //including toolbars, Google Sign-in client, drawer layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //action bar contains nav drawer icon for easier access
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.icon_baseline_menu_black_48dp);
        //title not shown in action bar
        actionbar.setDisplayShowTitleEnabled(false);

        //Configure sign-in to request the user's ID, email address, and basic
        //profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //accessing GmailUser info from Preference manager to be displayed on various pages
        GmailUser gMailUser = PreferenceManager.loadObject(this);
        personName = gMailUser.getPersonName();
        personEmail = gMailUser.getPersonEmail();
        photoUriString = gMailUser.getUriString();

        //toggling nav drawer to be opened or closed upon pressing icon
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //setting nav drawer imageicon to image button
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        ImageButton imageButton = headerView.findViewById(R.id.imageButton3);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        navigationView.setNavigationItemSelectedListener(this);

        //display HomeFragment by default on MainActivity launch
        MenuItem homeItem = navigationView.getMenu().getItem(Constants.POSITION_HOME);
        selectDrawerItem(homeItem);

        //changing colour of logout button in nav drawer
        MenuItem logoutItem = navigationView.getMenu().getItem(Constants.POSITION_LOGOUT);
        setTextColorForMenuItem(logoutItem, R.color.colorPrimary);

        View contentFrame = findViewById(R.id.content_frame);
        contentFrame.setOnTouchListener(new OnSwipeTouchListener(this) {

            //method deals with swipe action on home page to open scanner fragment
            @Override
            public void onSwipeLeft() {

                MenuItem scannerItem = navigationView.getMenu().getItem(Constants.POSITION_SCANNER);
                selectDrawerItem(scannerItem);

            }
        });

        checkPermission();

        //conditional statement to create instance of MyFiles fragment
        if (goMyFiles(getIntent())) {

            Fragment fragment = null;

            try {
                fragment = MyFilesFragment.newInstance(photoUriString);

            } catch (Exception e) {
                Log.e("CREATE_FRAGMENT_ERROR", e.getMessage());
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            //beginTransaction and commit are used to start the MyFiles intent
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            MenuItem myFilesItem = navigationView.getMenu().getItem(Constants.POSITION_MY_FILES);
            selectDrawerItem(myFilesItem);

        }

        //Creates folder in app's External Storage
        if (!IO.getFolderCreated()) {
            runFolderCreate();
        }

    }

    //method allows user to press anywhere on screen or on menu icon and close drawer
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    //method returns boolean to check if MyFiles is open
    private boolean goMyFiles(Intent intent) {
        return intent.hasExtra(EditingActivity.OPEN_MY_FILES_FRAG);
    }

    //method deals with user selection of nav menu item by calling selectDrawerItem method
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        selectDrawerItem(menuItem);
        return true;
    }

    //method assigns fragmentClass (to create new fragment) based on which menu item was selected
    public void selectDrawerItem(MenuItem menuItem) {
        //Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_my_files:
                fragmentClass = MyFilesFragment.class;
                break;
            case R.id.nav_scanner:
                fragmentClass = ScannerFragment.class;
                break;
            case R.id.nav_user_preferences:
                fragmentClass = UserPreferencesFragment.class;
                break;
            case R.id.nav_what_is_new:
                fragmentClass = WhatIsNewFragment.class;
                break;
            case R.id.nav_log_out:
                signOut();
                fragmentClass = HomeFragment.class;
                break;
            //default page is Home Page
            default:
                fragmentClass = HomeFragment.class;
                break;
        }

        //try catch is used to display user profile info in each of the following fragments
        try {
            if (fragmentClass.equals(HomeFragment.class)) {
                fragment = HomeFragment.newInstance(photoUriString);
            } else if (fragmentClass.equals(UserPreferencesFragment.class)) {
                fragment = UserPreferencesFragment.newInstance(photoUriString);
            } else if (fragmentClass.equals(MyFilesFragment.class)) {
                fragment = MyFilesFragment.newInstance(photoUriString);
            } else {
                fragment = (Fragment) fragmentClass.newInstance();
            }
        } catch (Exception e) {
            Log.e("CREATE_FRAGMENT_ERROR", e.getMessage());
        }

        //Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        //Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        //Set action bar title
        setTitle(menuItem.getTitle());
        //Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    //method deals with logout functionality
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        revokeAccess();
                    }
                });
    }

    //method prevents user from accessing Coeus if they do not sign in successfully
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }

    //method assigns a colour (based on a predefined theme in values (XML folder) to each menu item
    private void setTextColorForMenuItem(MenuItem menuItem, @ColorRes int colour) {
        SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, colour)),
                0, spanString.length(), 0);
        menuItem.setTitle(spanString);
    }

    //method deals with selection of MyFiles on nav drawer
    @Override
    public void onScannerFragmentInteraction() {
        MenuItem myFilesItem = navigationView.getMenu().getItem(Constants.POSITION_MY_FILES);
        selectDrawerItem(myFilesItem);
    }

    //method runs the folderCreate method in the IO class and displays a message with the directory
    public void runFolderCreate() {

        File createdDir = IO.getExternalStorageDir(getApplicationContext());
        Toast.makeText(this, createdDir.getAbsolutePath(), Toast.LENGTH_LONG).show();

    }

    //method checks permission to read and write to user's device
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);

        } else {
            permission = true;
        }

    }

    //method handles permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission = true;
                } else {
                    permission = false;
                }

            }
        }
    }

}