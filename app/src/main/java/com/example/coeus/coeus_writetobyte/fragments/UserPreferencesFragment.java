package com.example.coeus.coeus_writetobyte.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.coeus.coeus_writetobyte.R;
import com.example.coeus.coeus_writetobyte.activities.MainActivity;
import com.example.coeus.coeus_writetobyte.utils.GlideApp;

/**
 * Description: This class defines all methods associated with the UserPreferences fragment.
 *
 * Author: Ojas Bhatia
 *
 * Date: January 10, 2019
 */

public class UserPreferencesFragment extends Fragment {

    //fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PHOTO_URI_STRING = "photoUriString";

    //declaring components of UserPreferencesFragment
    private String photoUriString;

    private OnFragmentInteractionListener mListener;

    public UserPreferencesFragment() {
        // Required empty public constructor
    }

    /**
     * Factory method creates a new instance of
     * this fragment using the provided parameters.
     *
     * @param photoUriString Parameter 1.
     * @return A new instance of fragment UserPreferencesFragment.
     */
    public static UserPreferencesFragment newInstance(String photoUriString) {
        UserPreferencesFragment fragment = new UserPreferencesFragment();
        Bundle args = new Bundle();
        args.putString(PHOTO_URI_STRING, photoUriString);
        fragment.setArguments(args);
        return fragment;
    }

    //onCreate method called after newInstance method
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //checks if there are arguments for initialization
        if (getArguments() != null) {
            photoUriString = getArguments().getString(PHOTO_URI_STRING);
        }
    }

    //method initializes spinners and displays user Google profile info
    //(name, email, profile picture)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSpinners();

        TextView userNameTextView = getView().findViewById(R.id.username);
        userNameTextView.setText(MainActivity.personName);

        TextView userNameEmail = getView().findViewById(R.id.email);
        userNameEmail.setText(MainActivity.personEmail);

        //displays user's profile picture (if there is one); else displays default Mat. Design icon
        if ((photoUriString != null) && !photoUriString.isEmpty()) {

            ImageView profilePhotoImageView = getView().findViewById(R.id.profile_photo);
            GlideApp.with(this)
                    .load(photoUriString)
                    .into(profilePhotoImageView);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_preferences, container, false);
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

    //declaring interface with action listener
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //method initializes all spinners (drop down menus) by calling each spinner's respective method
    private void initSpinners(){
        initFontsSpinner();
        initTextSizesSpinner();
        initDocumentTypesSpinner();
    }

    //method initializes fonts spinner
    private void initFontsSpinner(){

        Spinner fontsSpinner = getView().findViewById(R.id.spinner_font);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> fontsAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.fonts_array, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        fontsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        fontsSpinner.setAdapter(fontsAdapter);

        AdapterView.OnItemSelectedListener fontsItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        //adding selection listener
        fontsSpinner.setOnItemSelectedListener(fontsItemSelectedListener);

    }

    //method initializes text sizes spinner
    private void initTextSizesSpinner(){

        Spinner textSizesSpinner = getView().findViewById(R.id.spinner_text_size);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> textSizesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.text_sizes_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        textSizesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        textSizesSpinner.setAdapter(textSizesAdapter);

        AdapterView.OnItemSelectedListener textSizesItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        //adding selection listener
        textSizesSpinner.setOnItemSelectedListener(textSizesItemSelectedListener);

    }

    //method initializes document types spinner
    private void initDocumentTypesSpinner(){

        Spinner documentTypesSpinner = getView().findViewById(R.id.spinner_document_type);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> documentTypesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.document_types_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        documentTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        documentTypesSpinner.setAdapter(documentTypesAdapter);

        AdapterView.OnItemSelectedListener documentTypesItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        //adding selection listener
        documentTypesSpinner.setOnItemSelectedListener(documentTypesItemSelectedListener);

    }

}
