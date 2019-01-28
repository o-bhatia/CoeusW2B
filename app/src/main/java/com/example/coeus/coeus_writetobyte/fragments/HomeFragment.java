package com.example.coeus.coeus_writetobyte.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.coeus.coeus_writetobyte.R;
import com.example.coeus.coeus_writetobyte.activities.MainActivity;
import com.example.coeus.coeus_writetobyte.utils.GlideApp;

/**
 * Description: This class defines all methods associated with the Home fragment.
 *
 * Author: Ojas Bhatia
 *
 * Date: January 10, 2019
 */

public class HomeFragment extends Fragment {

    //declaring components for home fragment

    //arguments for initialization and display of user profile picture
    private static final String PHOTO_URI_STRING = "photoUriString";

    private String photoUriString;

    //interaction listener
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        //Required empty public constructor
    }

    /**
     * Factory method creates a new instance of
     * this fragment using the provided parameters.
     *
     * @param photoUriString Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(String photoUriString) {

        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(PHOTO_URI_STRING, photoUriString);
        fragment.setArguments(args);
        return fragment;
    }

    //onCreate method called after newInstance (gets arguments)
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photoUriString = getArguments().getString(PHOTO_URI_STRING);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    //method calls method which user data to Home view
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        setUserDataToView(view);

    }

    //method sets user Google info (name, email, photo) to Home page
    private void setUserDataToView(View view) {

        //setting user data TextViews

        TextView userNameTextView = view.findViewById(R.id.username);
        userNameTextView.setText(MainActivity.personName);

        TextView userNameEmail = view.findViewById(R.id.email);
        userNameEmail.setText(MainActivity.personEmail);

        //if user has a Google profile pic, it is displayed;
        //else, the default Mat. Design icon is displayed
        if ((photoUriString != null ) && !photoUriString.isEmpty()) {

            ImageView profilePhotoImageView = view.findViewById(R.id.profile_photo);
            GlideApp.with(this)
                    .load(photoUriString)
                    .into(profilePhotoImageView);
        }

    }

    //onAttach must be called to associate fragment with running activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    //onDetach must be called to dissociate fragment with running activity
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //declaring interface
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
