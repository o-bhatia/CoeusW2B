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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserPreferencesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserPreferencesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserPreferencesFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PHOTO_URI_STRING = "photoUriString";

    private String photoUriString;

    private OnFragmentInteractionListener mListener;

    public UserPreferencesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photoUriString = getArguments().getString(PHOTO_URI_STRING);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initSpinners();

        TextView userNameTextView = getView().findViewById(R.id.username);
        userNameTextView.setText(MainActivity.personName);

        TextView userNameEmail = getView().findViewById(R.id.email);
        userNameEmail.setText(MainActivity.personEmail);

        if ( ( photoUriString != null ) && !photoUriString.isEmpty()) {

            ImageView profilePhotoImageView = getView().findViewById(R.id.profile_photo);
            GlideApp.with(this)
                    .load(photoUriString)
                    .into(profilePhotoImageView);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_preferences, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initSpinners(){
        initFontsSpinner();
        initTextSizesSpinner();
        initDocumentTypesSpinner();
    }

    private void initFontsSpinner(){

        Spinner fontsSpinner = getView().findViewById(R.id.spinner_font);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> fontsAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.fonts_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        fontsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        fontsSpinner.setAdapter(fontsAdapter);

        AdapterView.OnItemSelectedListener fontsItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        fontsSpinner.setOnItemSelectedListener(fontsItemSelectedListener);

    }

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

        textSizesSpinner.setOnItemSelectedListener(textSizesItemSelectedListener);

    }

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

        documentTypesSpinner.setOnItemSelectedListener(documentTypesItemSelectedListener);

    }

}
