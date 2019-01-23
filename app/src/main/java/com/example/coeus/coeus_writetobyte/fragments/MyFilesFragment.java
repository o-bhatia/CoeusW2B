package com.example.coeus.coeus_writetobyte.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coeus.coeus_writetobyte.R;
import com.example.coeus.coeus_writetobyte.activities.MainActivity;
import com.example.coeus.coeus_writetobyte.adapters.FilesInfoAdapter;
import com.example.coeus.coeus_writetobyte.realm.ScannedDataFile;
import com.example.coeus.coeus_writetobyte.utils.GlideApp;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyFilesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyFilesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFilesFragment extends Fragment {

    private static final String PHOTO_URI_STRING = "photoUriString";

    private String photoUriString;

    private OnFragmentInteractionListener mListener;

    private Realm realm;
    private RecyclerView filesInfoRecyclerView;
    private FilesInfoAdapter filesInfodapter;
    private RecyclerView.LayoutManager filesInfoLayoutManager;


    public MyFilesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param photoUriString Parameter 1.
     * @return A new instance of fragment MyFilesFragment.
     */
    public static MyFilesFragment newInstance(String photoUriString) {
        MyFilesFragment fragment = new MyFilesFragment();
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
        return inflater.inflate(R.layout.fragment_my_files, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        realm = Realm.getDefaultInstance();
        filesInfoRecyclerView = view.findViewById(R.id.files_recycler_view);
        setUpRecycleView(view.getContext());
        setUserDataToView(view);
        initSortSpinner();
        FloatingActionButton openMyFilesButton = view.findViewById(R.id.open_my_files);
        openMyFilesButton.setOnClickListener(openMyFilesClickListener);

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
        filesInfoRecyclerView.setAdapter(null);
        realm.close();
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

    private void setUpRecycleView(Context context) {

        RealmResults<ScannedDataFile> allFiles = realm.where(ScannedDataFile.class).findAll();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        filesInfoRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        filesInfoLayoutManager = new LinearLayoutManager(context);
        filesInfoRecyclerView.setLayoutManager(filesInfoLayoutManager);

        // specify an adapter (see also next example)

        filesInfodapter = new FilesInfoAdapter(context, allFiles);
        filesInfoRecyclerView.setAdapter(filesInfodapter);


        DividerItemDecoration decoration = new DividerItemDecoration(context, VERTICAL);
        filesInfoRecyclerView.addItemDecoration(decoration);

    }

    private void setUserDataToView(View view) {

        TextView userNameTextView = view.findViewById(R.id.username);
        userNameTextView.setText(MainActivity.personName);

        TextView userNameEmail = view.findViewById(R.id.email);
        userNameEmail.setText(MainActivity.personEmail);

        if ((photoUriString != null) && !photoUriString.isEmpty()) {

            ImageView profilePhotoImageView = view.findViewById(R.id.profile_photo);
            GlideApp.with(this)
                    .load(photoUriString)
                    .into(profilePhotoImageView);
        }

    }

    private void initSortSpinner() {

        Spinner sortSpinner = getView().findViewById(R.id.spinner_font);
        sortSpinner.setVisibility(View.GONE);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> sortTypesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_types_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sortTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sortSpinner.setAdapter(sortTypesAdapter);

        AdapterView.OnItemSelectedListener fontsItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        sortSpinner.setOnItemSelectedListener(fontsItemSelectedListener);

    }

    View.OnClickListener openMyFilesClickListener = v -> {
        openFiles();
    };

    public void openFiles() {
        int CHOOSE_FILE_REQUESTCODE = 1;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // special intent for Samsung file manager
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        // if you want any file type, you can skip next line
        sIntent.putExtra("CONTENT_TYPE", "*/*");
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;
        if (getActivity().getPackageManager().resolveActivity(sIntent, 0) != null) {
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "View file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
        } else {
            chooserIntent = Intent.createChooser(intent, "View file");
        }

        try {
            startActivityForResult(chooserIntent, CHOOSE_FILE_REQUESTCODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity().getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }

}
