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


public class HomeFragment extends Fragment {

    private static final String PHOTO_URI_STRING = "photoUriString";

    private String photoUriString;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        setUserDataToView(view);

    }

    private void setUserDataToView(View view) {

        TextView userNameTextView = view.findViewById(R.id.username);
        userNameTextView.setText(MainActivity.personName);

        TextView userNameEmail = view.findViewById(R.id.email);
        userNameEmail.setText(MainActivity.personEmail);

        if ( ( photoUriString != null ) && !photoUriString.isEmpty()) {

            ImageView profilePhotoImageView = view.findViewById(R.id.profile_photo);
            GlideApp.with(this)
                    .load(photoUriString)
                    .into(profilePhotoImageView);
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
}
