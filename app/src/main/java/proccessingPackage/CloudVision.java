package proccessingPackage;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.vision.v1p3beta1.AnnotateImageRequest;
import com.google.cloud.vision.v1p3beta1.AnnotateImageResponse;
import com.google.cloud.vision.v1p3beta1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1p3beta1.Image;
import com.google.cloud.vision.v1p3beta1.ImageAnnotatorClient;
import com.google.cloud.vision.v1p3beta1.ImageAnnotatorSettings;
import com.google.cloud.vision.v1p3beta1.ImageContext;
import com.google.cloud.vision.v1p3beta1.TextAnnotation;
import com.google.protobuf.ByteString;
import com.google.cloud.vision.v1p3beta1.Feature;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class CloudVision {


    //method used during testing
//    public static String readText (Bitmap bitmap, boolean handwritten, Context context) {
//
//        //initialize necessary data structure
//        List<AnnotateImageRequest> requests = new ArrayList<>();
//
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] bytes = stream.toByteArray();
//        try {
//            String filename = "myfile";
//            FileOutputStream outputStream;
//
//            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
//            outputStream.write(bytes);
//            outputStream.close();
//
//            System.out.print(context.getFilesDir());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            stream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //get image and write ByteString
//        ByteString imgBytes = ByteString.copyFrom(bytes);
//
//        //Create image from ByteString
//        Image img = Image.newBuilder().setContent(imgBytes).build();
//
//        //Add feature that tells machine that program is analysing a document
//        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
//
//        // Set the Language Hint codes for handwritten OCR if boolean was passed
//        if (handwritten) {
//            ImageContext imageContext =
//                    ImageContext.newBuilder().addLanguageHints("en-t-i0-handwrit").build();
//
//            //initializing the request (for handwritten documents)
//            AnnotateImageRequest request =
//                    AnnotateImageRequest.newBuilder()
//                            .addFeatures(feat)
//                            .setImage(img)
//                            .setImageContext(imageContext)
//                            .build();
//            requests.add(request);
//        }
//
//        //initializing the request (for non-handwritten documents)
//        AnnotateImageRequest request =
//                AnnotateImageRequest.newBuilder()
//                        .addFeatures(feat)
//                        .setImage(img)
//                        .build();
//        requests.add(request);
//
//        MyTaskParams params = new MyTaskParams(requests, context);
//
//        String text = null;
//
//        try {
//            text = new readTask().execute(params).get();
//        } catch (Exception e) {e.printStackTrace();}
//
//        return text;
//
//    }

    /*
    Method used to call Google Vision API and read text from passed image
    @Params:
    bytes: byte array of image to be analyzed
    handwritten: if image contains handwritten text
    context: context of application, used to access assets manager
     */
    public static String readText (byte[] bytes, boolean handwritten, Context context) {

        //initialize necessary data structure
        List<AnnotateImageRequest> requests = new ArrayList<>();

        //get image and write ByteString
        ByteString imgBytes = ByteString.copyFrom(bytes);

        //Create image from ByteString
        Image img = Image.newBuilder().setContent(imgBytes).build();

        //Add feature that tells machine that program is analysing a document
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();

        // Set the Language Hint codes for handwritten OCR if boolean was passed
        if (handwritten) {
            ImageContext imageContext =
                    ImageContext.newBuilder().addLanguageHints("en-t-i0-handwrit").build();

            //initializing the request (for handwritten documents)
            AnnotateImageRequest request =
                    AnnotateImageRequest.newBuilder()
                            .addFeatures(feat)
                            .setImage(img)
                            .setImageContext(imageContext)
                            .build();
            requests.add(request);
        }

        //initializing the request (for non-handwritten documents)
        AnnotateImageRequest request =
                AnnotateImageRequest.newBuilder()
                        .addFeatures(feat)
                        .setImage(img)
                        .build();
        requests.add(request);

        //create parameters for asynchronous task
        MyTaskParams params = new MyTaskParams(requests, context);

        String text = null;

        try {
            //get result of task
            text = new readTask().execute(params).get();
        } catch (Exception e) {e.printStackTrace();}

       //return text from Google Vision
        return text;

    }

    /*
    Class that calls Vision API, parent class in AsyncTask
     */
    private static class readTask extends AsyncTask<MyTaskParams, Void, String> {

        //final name of json key
        static final String JSON_KEY = "ServiceAccountKey.json";

        @Override
        protected String doInBackground(MyTaskParams... params) {

            String pageText = null;

            //get requests and context passed in by params interface
            List<AnnotateImageRequest> requests = params[0].requests;
            Context context = params[0].context;

            //get asset manager
            AssetManager assetManager = context.getAssets();

            try {
                //open json key, create authentication credentials
                InputStream jsonPath = assetManager.open(JSON_KEY);
                ServiceAccountCredentials myCredentials = ServiceAccountCredentials.fromStream(jsonPath);
                ImageAnnotatorSettings imageAnnotatorSettings =
                        ImageAnnotatorSettings.newBuilder()
                                .setCredentialsProvider(FixedCredentialsProvider.create(myCredentials))
                                .build();

                //send request to Google Cloud
                try (ImageAnnotatorClient client = ImageAnnotatorClient.create(imageAnnotatorSettings)) {
                    BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                    List<AnnotateImageResponse> responses = response.getResponsesList();
                    client.close();

                    for (AnnotateImageResponse res : responses) {
                        if (res.hasError()) {
                            return null;
                        }

                        //handle response
                        TextAnnotation textAnnotation = res.getFullTextAnnotation();
                        pageText = textAnnotation.getText();
                    }
                } catch (IOException e) {e.printStackTrace();}


            } catch (IOException e) {
                e.printStackTrace();
            }
            //return response
            return pageText;
        }

    }

    //Class to facilitate the passing of multiple objects into the async task
    private static class MyTaskParams {
        List<AnnotateImageRequest> requests;
        Context context;

        MyTaskParams(List<AnnotateImageRequest> requests, Context context) {
            this.context = context;
            this.requests = requests;
        }
    }

}
