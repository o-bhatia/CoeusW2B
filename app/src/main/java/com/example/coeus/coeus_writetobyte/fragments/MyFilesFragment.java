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
 * Description: This class contains all methods that define the
 * My Files fragment, which is one of the primary pages in Coeus.
 * It also contains the method to open the user's external storage
 * app.
 *
 * Author: Ojas Bhatia
 *
 * Last updated: January 10, 2019
 */

public class MyFilesFragment extends Fragment {

    private static final String PHOTO_URI_STRING = "photoUriString";

    private String photoUriString;

    private OnFragmentInteractionListener mListener;

    //realm object is instantiated to store the file info
    private Realm realm;
    private RecyclerView filesInfoRecyclerView;
    //filesInfodapter is instantiated to display previously scanned files
    private FilesInfoAdapter filesInfodapter;
    private RecyclerView.LayoutManager filesInfoLayoutManager;


    public MyFilesFragment() {
        // Required empty public constructor
    }

    //newInstance method must be called before the onCreate method
    //unlike regular activities (as this is a fragment class)
    public static MyFilesFragment newInstance(String photoUriString) {
        MyFilesFragment fragment = new MyFilesFragment();
        Bundle args = new Bundle();
        //associating photo taken with string ID
        args.putString(PHOTO_URI_STRING, photoUriString);
        fragment.setArguments(args);
        return fragment;
    }

    //calling onCreate method is necessary (after newInstance)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //checks if the user has taken a picture and sets ID
        if (getArguments() != null) {
            photoUriString = getArguments().getString(PHOTO_URI_STRING);
        }
    }

    //this method inflates the layout for this fragment (to fit screen)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_files, container, false);
    }

    //onViewCreated method initializes views, spinners and onClickListeners
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

    //this method is called when the MyFiles fragment is associated with
    //the running activity (i.e. it "attaches" to the running activity)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    //this method is called prior to when MyFiles fragment is no longer associated
    //with the running activity (i.e. it "detaches" when the activity changes)
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        filesInfoRecyclerView.setAdapter(null);
        realm.close();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    //this private method establishes the view for MyFiles
    //RecyclerView is chosen because it is dynamic
    private void setUpRecycleView(Context context) {

        RealmResults<ScannedDataFile> allFiles = realm.where(ScannedDataFile.class).findAll();

        //setting is used because changes in content do
        //not change the layout size of the RecyclerView
        filesInfoRecyclerView.setHasFixedSize(true);

        //use a linear layout manager
        filesInfoLayoutManager = new LinearLayoutManager(context);
        filesInfoRecyclerView.setLayoutManager(filesInfoLayoutManager);

        //specify an adapter (see also next example)
        filesInfodapter = new FilesInfoAdapter(context, allFiles);
        filesInfoRecyclerView.setAdapter(filesInfodapter);

        //adding divider to view
        DividerItemDecoration decoration = new DividerItemDecoration(context, VERTICAL);
        filesInfoRecyclerView.addItemDecoration(decoration);

    }

    //this private method adds user's Google account
    //information to the fragment view
    private void setUserDataToView(View view) {

        TextView userNameTextView = view.findViewById(R.id.username);
        userNameTextView.setText(MainActivity.personName);

        TextView userNameEmail = view.findViewById(R.id.email);
        userNameEmail.setText(MainActivity.personEmail);

        //checks if user has a Google profile picture
        //(if not, the default Material Design icon is displayed)
        if ((photoUriString != null) && !photoUriString.isEmpty()) {

            ImageView profilePhotoImageView = view.findViewById(R.id.profile_photo);
            GlideApp.with(this)
                    .load(photoUriString)
                    .into(profilePhotoImageView);
        }

    }

    //This private method initializes the spinner (drop-down menu) for the Sort By spinner
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

            //This method would handle any selections made in the Sort By spinner by the user.
            //Presently empty due to the sort functionality not being implemented.
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            //This method would handle any defaults in the Sort By spinner.
            //Presently empty due to the sort functionality not being implemented.
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };

        sortSpinner.setOnItemSelectedListener(fontsItemSelectedListener);

    }

    View.OnClickListener openMyFilesClickListener = v -> {
        openFiles();
    };

    //this public method deals with opening Files app when the user presses
    //the View button so that they can view their scanned documents
    public void openFiles() {
        int CHOOSE_FILE_REQUESTCODE = 1;

        //intent created to open Files app
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        //category refers to the type of intent
        //in this case, the user will be opening and viewing data, so the category is OPENABLE
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        //special intent for Samsung file manager (different from other Android devices)
        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");
        sIntent.putExtra("CONTENT_TYPE", "*/*");
        sIntent.addCategory(Intent.CATEGORY_DEFAULT);

        Intent chooserIntent;

        //checks if device is Samsung (for special intent) and displays options accordingly
        if (getActivity().getPackageManager().resolveActivity(sIntent, 0) != null) {
            chooserIntent = Intent.createChooser(sIntent, "View file");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
        } else {
            chooserIntent = Intent.createChooser(intent, "View file");
        }

        try {
            startActivityForResult(chooserIntent, CHOOSE_FILE_REQUESTCODE);
        } catch (android.content.ActivityNotFoundException ex) {
            //if the user's device has no Files folder, then the following message will be displayed
            Toast.makeText(getActivity().getApplicationContext(), "No suitable File Manager was found.", Toast.LENGTH_SHORT).show();
        }
    }

}
