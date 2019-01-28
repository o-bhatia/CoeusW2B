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
import com.example.coeus.coeus_writetobyte.models.GMailUser;
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

public class EditingActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing);

        realm = Realm.getDefaultInstance();

        showCapturedImage();

        GMailUser gMailUser = PreferenceManager.loadObject(this);
        String personEmail = gMailUser.getPersonEmail();
        TextView emailTextView = findViewById(R.id.email);
        emailTextView.setText(personEmail);

        visionText = getVisionText();

        Log.d("Vision", "(2) Text read:\n" + visionText);

        fileNameEditText = findViewById(R.id.title);
        fileNameEditText.setSingleLine(true);

        initSpinners();

        Button doneButton = findViewById(R.id.done);
        doneButton.setOnClickListener(doneButtonClickListener);

    }

    private void showCapturedImage() {

        Intent incomingIntent = getIntent();
        capturedImagePath = incomingIntent.getStringExtra(ScannerFragment.EXTRA_IMAGE_PATH);
        ImageHelper imageHelper = new ImageHelper(this);
        capturedImage = imageHelper.loadImageFromStorage(capturedImagePath);
        ImageView showPictureImageView = findViewById(R.id.show_picture_image_view);
        showPictureImageView.setImageBitmap(Bitmap.createScaledBitmap(capturedImage, 200, 200, false));

    }

    private String getVisionText() {

        Intent incomingIntent = getIntent();
        String text = incomingIntent.getStringExtra("visionText");
        return text;
    }

    View.OnClickListener doneButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {



                    int filesCount = PreferenceManager.getFilesCount(getApplicationContext());
                    filesCount++;
                    PreferenceManager.saveFilesCount(getApplicationContext(), filesCount);

                    fileName = fileNameEditText.getText().toString();
                    if (fileName.isEmpty()) {
                        fileName = "Document" + String.valueOf(filesCount);
                    }

                    PDFParams params = getPDFParams();

                    PDFont font = params.font;
                    int fontSize = params.textSize;

                    File root = IO.getExternalStorageDir(getApplicationContext());
                    PDFBoxResourceLoader.init(getApplicationContext());
                    try {
                        CreatePDF.write(visionText, fileName, font, fontSize, root);
                        realm.beginTransaction();

                        ScannedDataFile scannedDataFile = realm.createObject(ScannedDataFile.class); // Create a new object
                        scannedDataFile.setFileName(fileName);

                        Date currentDate = Calendar.getInstance().getTime();
                        String formattedDate = DateFormat.getDateInstance(DateFormat.SHORT).format(currentDate);
                        scannedDataFile.setCreationDate(formattedDate);
                        scannedDataFile.setFilePath(capturedImagePath);

                        realm.commitTransaction();

                        Log.i("Success", "2");

                        (EditingActivity.this).setResult(RESULT_OK);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra(OPEN_MY_FILES_FRAG, R.id.nav_my_files);
                        startActivity(intent);
                        capturedImage.recycle();
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Urecognizable symbol found, returning to home screen", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        capturedImage.recycle();
                        finish();
                    }
                    Log.i("Success", "1");


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    };

    private void initSpinners(){
        initFontsSpinner();
        initTextSizesSpinner();

    }

    private void initFontsSpinner(){

        fontsSpinner = findViewById(R.id.spinner_font);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> fontsAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.fonts_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        fontsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        fontsSpinner.setAdapter(fontsAdapter);
        fontsSpinner.setSelection(DEFAULT_POSITION);

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

        textSizesSpinner = findViewById(R.id.spinner_text_size);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> textSizesAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.text_sizes_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        textSizesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        textSizesSpinner.setAdapter(textSizesAdapter);
        textSizesSpinner.setSelection(DEFAULT_POSITION + 1);

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

    public PDFParams getPDFParams () {

        return new PDFParams(fontsSpinner.getSelectedItem().toString(), textSizesSpinner.getSelectedItem().toString());
    }

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


            Log.d("font", Integer.toString(textSize));
            Log.d("font", text);

        }
    }

}
