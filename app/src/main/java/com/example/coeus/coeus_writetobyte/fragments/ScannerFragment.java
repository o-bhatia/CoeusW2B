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
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import proccessingPackage.IO;
import proccessingPackage.IO.*;
import proccessingPackage.CloudVision;
import proccessingPackage.CreatePDF;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnScannerFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScannerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String EXTRA_IMAGE_PATH = "extraImagePath";

    public static final int EDITING_SCREEN_REQUEST_CODE = 0;
    public static final int CAMERA_REQUEST_CODE = 1;

    private Button captureButton;
    private ImageView showPictureImageView;
    private Bitmap capturedImage;
    private Bitmap compressedImage;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String visionText = null;
    String mCurrentPhotoPath;

    private OnScannerFragmentInteractionListener scannerFragmentInteractionListener;

    public ScannerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ScannerFragment newInstance() {
        ScannerFragment fragment = new ScannerFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (scannerFragmentInteractionListener != null) {
//            scannerFragmentInteractionListener.onScannerFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        captureButton = view.findViewById(R.id.capture_button);
        captureButton.setOnClickListener(new View.OnClickListener() {

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
                return image;
            }

            @Override
            public void onClick(View view) {

                String captureButtonText = captureButton.getText().toString();

                if (captureButtonText.equals(getString(R.string.take_picture))) {

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePicture.resolveActivity(getContext().getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(getContext(),
                                    "com.example.coeus.coeus_writetobyte.myfileprovider",
                                    photoFile);
                            File f = new File(mCurrentPhotoPath);
                            Log.d("File", Long.toString(f.length()));
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePicture, CAMERA_REQUEST_CODE);

                            //Log.d("PATH", photoURI.getPath());
                        }

                    }

                } else if (captureButtonText.equals(getString(R.string.done))) {

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

        showPictureImageView = view.findViewById(R.id.show_picture_image_view);
        showPictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something when user click to captured image
            }
        });
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        scannerFragmentInteractionListener = null;
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
    public interface OnScannerFragmentInteractionListener {
        void onScannerFragmentInteraction();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

//            if (mCurrentPhotoPath != null) {
//
//                try {
//                    InputStream is = new FileInputStream(mCurrentPhotoPath);
//                    byte[] inputData = IO.getBytes(is);
//                    visionText = CloudVision.readText(inputData, true, this.getContext());
//                    Log.d("Vision2", visionText);
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            }

            if (requestCode == CAMERA_REQUEST_CODE) {

                if (mCurrentPhotoPath != null) {

                try {
                    InputStream is = new FileInputStream(mCurrentPhotoPath);
                    byte[] inputData = IO.getBytes(is);
                    is.close();
                    capturedImage = BitmapFactory.decodeByteArray(inputData, 0, inputData.length);

                    if (capturedImage != null) {
                        compressedImage = Bitmap.createScaledBitmap(capturedImage, 200, 200, false);
                        showPictureImageView.setImageBitmap(compressedImage);
                        captureButton.setText(R.string.done);
                        visionText = CloudVision.readText(inputData, true, this.getContext());
                        Log.d("Vision2", visionText);

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
//                Bundle bundle = data.getExtras();
//                if (bundle != null) {
//                    capturedImage = (Bitmap) bundle.get("data");
//                    if (capturedImage != null) {
//                        showPictureImageView.setImageBitmap(Bitmap.createScaledBitmap(capturedImage, 200, 200, false));
//                        captureButton.setText(R.string.done);
//                        visionText = CloudVision.readText(capturedImage, true, this.getContext());
//                        Log.d("Vision", visionText);
//                        Log.d("Vision", "finished");
//                    }
                }

            } else if (requestCode == EDITING_SCREEN_REQUEST_CODE) {
                scannerFragmentInteractionListener.onScannerFragmentInteraction();
            }

        }

    }


