package com.example.coeus.coeus_writetobyte.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.coeus.coeus_writetobyte.R;
import com.example.coeus.coeus_writetobyte.managers.PreferenceManager;
import com.example.coeus.coeus_writetobyte.models.GmailUser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

/**
 * Description: This class contains all methods that define the
 * Login Activity, done using the Google Sign-in Client.
 *
 * Author: Ojas Bhatia
 *
 * Last updated: January 10, 2019
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //declaring Google Sign In components

    private static final int RC_SIGN_IN = 99;

    private GoogleSignInClient googleSignInClient;

    //onCreate method initializes toolbar, title display and configures sign-in request
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);

        //Configure sign-in to request the user's ID, email address, and basic
        //profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Set the dimensions of the sign-in button.
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(this);

    }

    //onStart checks if user is signed in already
    @Override
    protected void onStart() {

        super.onStart();

        //Check for existing Google Sign In account, if the user is already signed in
        //the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);

    }

    //method opens signIn method (sign in intent) if user presses sign in button
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sign_in_button) {
            signIn();
        }

    }

    //method starts intent for Google Sign-in
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //method deals with sign in result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            //The Task returned from this call is always completed, so there is no need to attach
            //a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    //method updates UI if user signs in successfully
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            //Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            //The ApiException status code indicates the detailed failure reason.
            //Refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }

    }

    //private method updates Strings containing user's google
    //login info to be displayed on other activities; and method
    //launches Main Activity after user successfully signs in
    private void updateUI(GoogleSignInAccount account) {

        if (account != null) {

            //if user signs in with Google account, set each
            //associated String with respective information
            String personName = account.getDisplayName();
            String personGivenName = account.getGivenName();
            String personFamilyName = account.getFamilyName();
            String personEmail = account.getEmail();
            String personId = account.getId();
            Uri personPhoto = account.getPhotoUrl();

            //if user has a photo, get uri
            String uriString;
            if (personPhoto != null) {
                uriString = personPhoto.toString();
            } else {
                uriString = "";
            }

            //create new Gmail user with updated Strings
            GmailUser gmailUser = new GmailUser(personName, personGivenName, personFamilyName,
                    personEmail, personId, uriString);

            //saving user's information in PreferenceManager
            PreferenceManager.saveObject(this, gmailUser);

            //launching MainActivity
            Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivityIntent);

        }

    }

}
