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
import com.example.coeus.coeus_writetobyte.models.GMailUser;
import com.example.coeus.coeus_writetobyte.utils.Constants;
import com.example.coeus.coeus_writetobyte.utils.OnSwipeTouchListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;

import proccessingPackage.IO;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ScannerFragment.OnScannerFragmentInteractionListener {

    private ImageButton button;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private GoogleSignInClient mGoogleSignInClient;
    public static String personName;
    public static String personEmail;

    private String photoUriString;

    public static boolean permission;
    private final int MY_PERMISSIONS_REQUEST = 10;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ScannerFragment.CAMERA_REQUEST_CODE || requestCode == ScannerFragment.EDITING_SCREEN_REQUEST_CODE)
                && resultCode == RESULT_OK) {
            Fragment fragment = ScannerFragment.newInstance();
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.icon_baseline_menu_black_48dp);
        actionbar.setDisplayShowTitleEnabled(false);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GMailUser gMailUser = PreferenceManager.loadObject(this);
        personName = gMailUser.getPersonName();
        personEmail = gMailUser.getPersonEmail();
        photoUriString = gMailUser.getUriString();

        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

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

        // We should display HomeFragment by default on MainActivity launch
        MenuItem homeItem = navigationView.getMenu().getItem(Constants.POSITION_HOME);
        selectDrawerItem(homeItem);

        MenuItem logoutItem = navigationView.getMenu().getItem(Constants.POSITION_LOGOUT);
        setTextColorForMenuItem(logoutItem, R.color.colorPrimary);


        View contentFrame = findViewById(R.id.content_frame);
        contentFrame.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {

                MenuItem scannerItem = navigationView.getMenu().getItem(Constants.POSITION_SCANNER);
                selectDrawerItem(scannerItem);

            }
        });

        checkPermission();

        if (goMyFiles(getIntent())) {

            Fragment fragment = null;

            try {
                fragment = MyFilesFragment.newInstance(photoUriString);

            } catch (Exception e) {
                Log.e("CREATE_FRAGMENT_ERROR", e.getMessage());
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
            MenuItem myFilesItem = navigationView.getMenu().getItem(Constants.POSITION_MY_FILES);
            selectDrawerItem(myFilesItem);

        }

        // Creates folder in app's Internal Storage
        if (!IO.getFolderCreated()) {
            runFolderCreate();
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    private boolean goMyFiles(Intent intent) {
        return intent.hasExtra(EditingActivity.OPEN_MY_FILES_FRAG);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        selectDrawerItem(menuItem);
        return true;
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
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

            default:
                fragmentClass = HomeFragment.class;
                break;
        }

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

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        drawerLayout.closeDrawers();
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        revokeAccess();
                    }
                });
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }

    private void setTextColorForMenuItem(MenuItem menuItem, @ColorRes int color) {
        SpannableString spanString = new SpannableString(menuItem.getTitle().toString());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, color)),
                0, spanString.length(), 0);
        menuItem.setTitle(spanString);
    }

    @Override
    public void onScannerFragmentInteraction() {
        MenuItem myFilesItem = navigationView.getMenu().getItem(Constants.POSITION_MY_FILES);
        selectDrawerItem(myFilesItem);
    }

    public void runFolderCreate() {

        File createdDir = IO.getExternalStorageDir(getApplicationContext());
        Toast.makeText(this, createdDir.getAbsolutePath(), Toast.LENGTH_LONG).show();

    }

    //Function: checkPermission
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST);

        } else {
            permission = true;
        }

    }

    //Function: Permission Request Results
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