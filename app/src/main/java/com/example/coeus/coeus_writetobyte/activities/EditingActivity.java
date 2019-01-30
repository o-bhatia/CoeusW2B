package com.example.coeus.coeus_writetobyte.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coeus.coeus_writetobyte.R;
import com.example.coeus.coeus_writetobyte.fragments.ScannerFragment;
import com.example.coeus.coeus_writetobyte.managers.PreferenceManager;
import com.example.coeus.coeus_writetobyte.models.GmailUser;
import com.example.coeus.coeus_writetobyte.realm.ScannedDataFile;
import com.example.coeus.coeus_writetobyte.utils.ImageHelper;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import proccessingPackage.CreatePDF;
import proccessingPackage.IO;

/**
 * Description: This class contains all methods that define the
 * Editing Activity. It also contains the method to get user OCR
 * preferences (font and text size).
 *
 * Author: Ojas Bhatia and Aumio Islam
 *
 * Last updated: January 10, 2019
 */


public class EditingActivity extends AppCompatActivity {

    //declaring variables and objects used within this class
    private String capturedImagePath;
    private Realm realm;
    String visionText = null;
    EditText fileNameEditText;
    String fileName;
    public static final int DEFAULT_POSITION = 0;
    public static final String OPEN_MY_FILES_FRAG = "open";
    Spinner textSizesSpinner;
    Spinner fontsSpinner;
    Bitmap capturedImage;

    //onCreate method handles the declaration of all necessary elements of Editing Activity
    //when it is opened
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);

        //realm object initialized
        realm = Realm.getDefaultInstance();

        //displaying captured image in frame
        showCapturedImage();

        //displaying user email on Editing screen
        GmailUser gMailUser = PreferenceManager.loadObject(this);
        String personEmail = gMailUser.getPersonEmail();
        TextView emailTextView = findViewById(R.id.email);
        emailTextView.setText(personEmail);

        //calling CloudVision class to scan text
        visionText = getVisionText();

        Log.d("Vision", "(2) Text read:\n" + visionText);

        //editing title of document
        fileNameEditText = findViewById(R.id.title);
        fileNameEditText.setSingleLine(true);

        //initializing spinners to choose font and text size
        initSpinners();

        //setting Done button
        Button doneButton = findViewById(R.id.done);
        doneButton.setOnClickListener(doneButtonClickListener);

    }

    //method displays captured image (scaled version) in blank frame
    private void showCapturedImage() {

        Intent incomingIntent = getIntent();
        capturedImagePath = incomingIntent.getStringExtra(ScannerFragment.EXTRA_IMAGE_PATH);
        ImageHelper imageHelper = new ImageHelper(this);
        capturedImage = imageHelper.loadImageFromStorage(capturedImagePath);
        ImageView showPictureImageView = findViewById(R.id.show_picture_image_view);
        showPictureImageView.setImageBitmap(Bitmap.createScaledBitmap(capturedImage, 200, 200, false));

    }

    //method to get text scanned from image from previous activity
    private String getVisionText() {

        Intent incomingIntent = getIntent();
        String text = incomingIntent.getStringExtra("visionText");
        return text;
    }

    View.OnClickListener doneButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {


                    //getting number of documents created already
                    int filesCount = PreferenceManager.getFilesCount(getApplicationContext());
                    filesCount++;
                    PreferenceManager.saveFilesCount(getApplicationContext(), filesCount);

                    //getting filename
                    fileName = fileNameEditText.getText().toString();
                    if (fileName.isEmpty()) {
                        fileName = "Document" + String.valueOf(filesCount);
                    }

                    //getting values from spinners
                    PDFParams params = getPDFParams();

                    PDFont font = params.font;
                    int fontSize = params.textSize;

                    //getting folder directory
                    File root = IO.getExternalStorageDir(getApplicationContext());
                    PDFBoxResourceLoader.init(getApplicationContext());
                    try {
                        //create doc
                        CreatePDF.write(visionText, fileName, font, fontSize, root);

                        //saving data with realm
                        realm.beginTransaction();

                        ScannedDataFile scannedDataFile = realm.createObject(ScannedDataFile.class); // Create a new object
                        scannedDataFile.setFileName(fileName);

                        Date currentDate = Calendar.getInstance().getTime();
                        String formattedDate = DateFormat.getDateInstance(DateFormat.SHORT).format(currentDate);
                        scannedDataFile.setCreationDate(formattedDate);
                        scannedDataFile.setFilePath(capturedImagePath);

                        realm.commitTransaction();



                        (EditingActivity.this).setResult(RESULT_OK);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(OPEN_MY_FILES_FRAG, R.id.nav_my_files);
                        startActivity(intent);
                        capturedImage.recycle();
                        finish();
                    } catch (Exception e) {
                        //if CreatePDF fails
                        Toast.makeText(getApplicationContext(), "Urecognizable symbol found, returning to home screen", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        capturedImage.recycle();
                        finish();
                    }



            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

    //method calls methods of spinners to initialize them
    private void initSpinners(){
        initFontsSpinner();
        initTextSizesSpinner();

    }

    //method initializes fonts spinner
    private void initFontsSpinner(){

        fontsSpinner = findViewById(R.id.spinner_font);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> fontsAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.fonts_array, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        fontsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        fontsSpinner.setAdapter(fontsAdapter);
        //setting default position of spinner to 0 (first choice is Times New Roman)
        fontsSpinner.setSelection(DEFAULT_POSITION);

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

        textSizesSpinner = findViewById(R.id.spinner_text_size);
        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> textSizesAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.text_sizes_array, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        textSizesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        textSizesSpinner.setAdapter(textSizesAdapter);
        //setting default position to 1 (second choice is 10 sp)
        textSizesSpinner.setSelection(DEFAULT_POSITION + 1);

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
    //method to get values of spinners
    public PDFParams getPDFParams () {

        return new PDFParams(fontsSpinner.getSelectedItem().toString(), textSizesSpinner.getSelectedItem().toString());
    }

    //class that handles taking inputs in spinners and converting them to values for CreatePDF class
    private static class PDFParams {
        int textSize;
        PDFont font;

        PDFParams (String text, String size) {
            this.textSize = Integer.parseInt(size);

            switch (text) {

                case "Times New Roman":
                    this.font = PDType1Font.TIMES_ROMAN;
                    break;

                case "Helvetica":
                    this.font = PDType1Font.HELVETICA;
                    break;

                case "Courier":
                    this.font = PDType1Font.COURIER;
                    break;
            }


        }
    }

}
