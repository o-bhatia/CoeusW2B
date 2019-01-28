package com.example.coeus.coeus_writetobyte.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.coeus.coeus_writetobyte.R;

/**
 * Description: This class defines all methods associated with the What's New fragment.
 *
 * Author: Ojas Bhatia
 *
 * Date: January 10, 2019
 */

public class WhatIsNewFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //declaring parameters and components for initialization
    private String mParam1;
    private String mParam2;

    //interaction listener
    private OnFragmentInteractionListener mListener;

    public WhatIsNewFragment() {
        //Required empty public constructor
    }

    /**
     * Factory method creates a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WhatIsNewFragment.
     */
    public static WhatIsNewFragment newInstance(String param1, String param2) {
        WhatIsNewFragment fragment = new WhatIsNewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    //onCreate method called after newInstance method (gets arguments for parameters)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_what_is_new, container, false);
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
        void onFragmentInteraction(Uri uri);
    }
}
