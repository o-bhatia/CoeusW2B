package com.example.coeus.coeus_writetobyte.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.coeus.coeus_writetobyte.R;
import com.example.coeus.coeus_writetobyte.activities.EditingActivity;
import com.example.coeus.coeus_writetobyte.utils.ImageHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import proccessingPackage.IO;
import proccessingPackage.CloudVision;

import static android.app.Activity.RESULT_OK;

/**
 * Description: This class defines all methods associated with the Scanner fragment.
 *
 * Author: Ojas Bhatia
 *
 * Date: January 10, 2019
 */

public class ScannerFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String EXTRA_IMAGE_PATH = "extraImagePath";

    //declaring constants to open other activities
    public static final int EDITING_SCREEN_REQUEST_CODE = 0;
    public static final int CAMERA_REQUEST_CODE = 1;

    //declaring buttons and imageviews to be displayed on Scanner fragment
    private Button captureButton;
    private ImageView showPictureImageView;
    private Bitmap capturedImage;
    private Bitmap compressedImage;

   //declaring parameters for Scanner fragment
    private String mParam1;
    private String mParam2;
    String visionText = null;
    String mCurrentPhotoPath;

    private OnScannerFragmentInteractionListener scannerFragmentInteractionListener;

    public ScannerFragment() {
        // Required empty public constructor
    }

    //newInstance method called when Fragment is created
    public static ScannerFragment newInstance() {
        ScannerFragment fragment = new ScannerFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    //onCreate has to be called after newInstance (takes parameters of fragment to create it)
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scanner, container, false);
    }

   //method contains createImageFile function which stores the date, title and filepath of the image taken
   //and allows user to take picture
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        captureButton = view.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {

            //function creates
            private File createImageFile() throws IOException {
                // Create an image file name
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "PNG_" + timeStamp + "_";
                File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File image = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".png",         /* suffix */
                        storageDir      /* directory */
                );

                // Save a file: path for use with ACTION_VIEW intents
                mCurrentPhotoPath = image.getAbsolutePath();
                Log.d("Aumio", mCurrentPhotoPath);
                return image; //returns created file
            }

            //method contains conditional statements to allow the user to take a picture and proceed to Editing activity
            @Override
            public void onClick(View view) {

                String captureButtonText = captureButton.getText().toString();

                //if button is Take Picture button, the user will be allowed to take a picture
                if (captureButtonText.equals(getString(R.string.take_picture))) {

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePicture.resolveActivity(getContext().getPackageManager()) != null) {
                        //Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            //Error occurred while creating the File
                        }
                        //Continue only if the File was successfully created
                        if (photoFile != null) {
                            //must declare authority in order to generate Uri to take picture
                            Uri photoURI = FileProvider.getUriForFile(getContext(),
                                    "com.example.coeus.coeus_writetobyte.myfileprovider",
                                    photoFile);
                            File f = new File(mCurrentPhotoPath);
                            Log.d("File", Long.toString(f.length()));
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            //when blank file is created, the Camera intent is started
                            startActivityForResult(takePicture, CAMERA_REQUEST_CODE);

                        }

                    }

                }

                //if button is Done button, Coeus will proceed to Editing activity
                else if (captureButtonText.equals(getString(R.string.done))) {

                    ImageHelper imageHelper = new ImageHelper(getContext());
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    String imagePath = imageHelper.setFileName(timestamp + ".png").
                            setDirectoryName("images").
                            save(capturedImage);

                    Intent editingScreenIntent = new Intent(getContext(), EditingActivity.class);
                    editingScreenIntent.putExtra(EXTRA_IMAGE_PATH, imagePath);
                    editingScreenIntent.putExtra("visionText", visionText);
                    capturedImage.recycle();
                    compressedImage.recycle();
                    startActivityForResult(editingScreenIntent, EDITING_SCREEN_REQUEST_CODE );

                }

            }

        });

        //display image in frame
        showPictureImageView = view.findViewById(R.id.show_picture_image_view);
        showPictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    //onAttach must be called to associate fragment with running activity
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnScannerFragmentInteractionListener) {
            scannerFragmentInteractionListener = (OnScannerFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnScannerFragmentInteractionListener");
        }
    }

    //onDetach must be called to dissociate fragment with running activity
    @Override
    public void onDetach() {
        super.onDetach();
        scannerFragmentInteractionListener = null;
    }

    //method sets up interactive interface
    public interface OnScannerFragmentInteractionListener {
        void onScannerFragmentInteraction();
    }

    //method creates ByteStream using bitmap file path (if request code == 1) or
    //opens Editing screen if request code == 0
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == CAMERA_REQUEST_CODE) {

                if (mCurrentPhotoPath != null) {

                try {
                    InputStream stream = new FileInputStream(mCurrentPhotoPath);
                    byte[] inputData = IO.getBytes(stream);
                    //creating ByteStream from taken image
                    stream.close();
                    capturedImage = BitmapFactory.decodeByteArray(inputData, 0, inputData.length);

                    //if an image was taken, change Button text and create scaled bitmap (to be displayed)
                    if (capturedImage != null) {
                        compressedImage = Bitmap.createScaledBitmap(capturedImage, 200, 200, false);
                        showPictureImageView.setImageBitmap(compressedImage);
                        captureButton.setText(R.string.done);
                        visionText = CloudVision.readText(inputData, true, this.getContext());
                        Log.d("Vision2", visionText);

                    }


                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                }

            }

            }

            //condition opens Editing screen if request code == 0
            else if (requestCode == EDITING_SCREEN_REQUEST_CODE) {
                scannerFragmentInteractionListener.onScannerFragmentInteraction();
            }

        }

    }


