package proccessingPackage;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.cloud.vision.v1p3beta1.AnnotateImageRequest;
import com.google.cloud.vision.v1p3beta1.AnnotateImageResponse;
import com.google.cloud.vision.v1p3beta1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1p3beta1.Block;
import com.google.cloud.vision.v1p3beta1.Image;
import com.google.cloud.vision.v1p3beta1.ImageAnnotatorClient;
import com.google.cloud.vision.v1p3beta1.ImageContext;
import com.google.cloud.vision.v1p3beta1.Page;
import com.google.cloud.vision.v1p3beta1.Paragraph;
import com.google.cloud.vision.v1p3beta1.Symbol;
import com.google.cloud.vision.v1p3beta1.TextAnnotation;
import com.google.cloud.vision.v1p3beta1.Word;
import com.google.protobuf.ByteString;
import com.google.cloud.vision.v1p3beta1.Feature;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BetterVision {

    private static final String CLOUD_VISION_API_KEY = "AIzaSyCI1Q11M6vxnMrZNal0NHw3wkFMx5qBRDE";

    public static String recog (final Bitmap bitmap, boolean handwritten) {

        List<AnnotateImageRequest> requests = new ArrayList<>();

        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imgBytes = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        ByteString byteString = ByteString.copyFrom(imgBytes);

        Image img = Image.newBuilder().setContent(byteString).build();

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

        Log.d("Vision", "starting request");
        //try-catch for actual request and reception of response
        String pageText = null;
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            Log.d("Vision", "response received");
            client.close();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return null;
                }

                //Getting Text Annotation
                //Will be taken in paragraph form so as to facilitate easy insert of images into document
                TextAnnotation annotation = res.getFullTextAnnotation();
                for (Page page : annotation.getPagesList()) {
                    pageText = "";
                    for (Block block : page.getBlocksList()) {
                        String blockText = "";
                        for (Paragraph para : block.getParagraphsList()) {
                            String paraText = "";
                            for (Word word : para.getWordsList()) {
                                String wordText = "";
                                for (Symbol symbol : word.getSymbolsList()) {
                                    wordText = wordText + symbol.getText();
                                }
                                paraText = String.format("%s %s", paraText, wordText);
                            }

                        }
                        pageText = pageText + blockText;
                    }
                }

                /*
                //Getting Text Annotation (outputs testing results in console)
                TextAnnotation annotation = res.getFullTextAnnotation();
                for (Page page : annotation.getPagesList()) {
                    String pageText = "";
                    for (Block block : page.getBlocksList()) {
                        String blockText = "";
                        for (Paragraph para : block.getParagraphsList()) {
                            String paraText = "";
                            for (Word word : para.getWordsList()) {
                                String wordText = "";
                                for (Symbol symbol : word.getSymbolsList()) {
                                    wordText = wordText + symbol.getText();
                                    out.format(
                                            "Symbol text: %s (confidence: %f)\n",
                                            symbol.getText(), symbol.getConfidence());
                                }
                                out.format("Word text: %s (confidence: %f)\n\n", wordText, word.getConfidence());
                                paraText = String.format("%s %s", paraText, wordText);
                            }
                            // Output Example using Paragraph:
                            out.println("\nParagraph: \n" + paraText);
                            out.format("Paragraph Confidence: %f\n", para.getConfidence());
                            blockText = blockText + paraText;

                            //add paragraph to list
                            Paragraphs.add(paraText);
                        }
                        pageText = pageText + blockText;
                    }
                }
                out.println("\nComplete annotation:");
                out.println(annotation.getText());
                */
            }
        } catch (Exception e) {e.printStackTrace();} //catch exception


        System.out.println(pageText);

        return pageText;

    }


    public static String recogV2 (Bitmap bitmap, boolean handwritten) {
        List<AnnotateImageRequest> requests = new ArrayList<>();

        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(requestInitializer);

        Vision vision = builder.build();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imgBytes = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        ByteString byteString = ByteString.copyFrom(imgBytes);

        Image img = Image.newBuilder().setContent(byteString).build();

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

        Log.d("Vision", "starting request");
        //try-catch for actual request and reception of response
        String pageText = null;
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            Log.d("Vision", "response received");
            client.close();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return null;
                }

                //Getting Text Annotation
                //Will be taken in paragraph form so as to facilitate easy insert of images into document
                TextAnnotation annotation = res.getFullTextAnnotation();
                for (Page page : annotation.getPagesList()) {
                    pageText = "";
                    for (Block block : page.getBlocksList()) {
                        String blockText = "";
                        for (Paragraph para : block.getParagraphsList()) {
                            String paraText = "";
                            for (Word word : para.getWordsList()) {
                                String wordText = "";
                                for (Symbol symbol : word.getSymbolsList()) {
                                    wordText = wordText + symbol.getText();
                                }
                                paraText = String.format("%s %s", paraText, wordText);
                            }

                        }
                        pageText = pageText + blockText;
                    }
                }

                /*
                //Getting Text Annotation (outputs testing results in console)
                TextAnnotation annotation = res.getFullTextAnnotation();
                for (Page page : annotation.getPagesList()) {
                    String pageText = "";
                    for (Block block : page.getBlocksList()) {
                        String blockText = "";
                        for (Paragraph para : block.getParagraphsList()) {
                            String paraText = "";
                            for (Word word : para.getWordsList()) {
                                String wordText = "";
                                for (Symbol symbol : word.getSymbolsList()) {
                                    wordText = wordText + symbol.getText();
                                    out.format(
                                            "Symbol text: %s (confidence: %f)\n",
                                            symbol.getText(), symbol.getConfidence());
                                }
                                out.format("Word text: %s (confidence: %f)\n\n", wordText, word.getConfidence());
                                paraText = String.format("%s %s", paraText, wordText);
                            }
                            // Output Example using Paragraph:
                            out.println("\nParagraph: \n" + paraText);
                            out.format("Paragraph Confidence: %f\n", para.getConfidence());
                            blockText = blockText + paraText;

                            //add paragraph to list
                            Paragraphs.add(paraText);
                        }
                        pageText = pageText + blockText;
                    }
                }
                out.println("\nComplete annotation:");
                out.println(annotation.getText());
                */
            }
        } catch (Exception e) {e.printStackTrace();} //catch exception


        System.out.println(pageText);

        return pageText;

    }

    /*
    Creates document from string of full text, preliminary application usage
    @params: String of the full text (no newline characeter), string of the file name, font, font size
     */
    public static PDDocument writeToDoc (String text, String fileName, PDFont font, float fontSize) {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        List<String> lines = new ArrayList<String>();
        String currentLine = "";

        PDRectangle mediaBox = page.getMediaBox();
        float margin = 72;
        float width = mediaBox.getWidth() - 2*margin;
        float startX = mediaBox.getLowerLeftX() + margin;
        float startY = mediaBox.getUpperRightY() - margin;
        int thisWidth = 0;
        float leading = 1.5f * fontSize;

        String[] words = text.split(" ");

        for (String word: words) {

            if (!currentLine.isEmpty()) { currentLine += " "; }

            try {thisWidth = (int) (fontSize*font.getStringWidth(currentLine + word)/1000);} catch (IOException e){}

            if (thisWidth > width) {
                lines.add(currentLine);

                currentLine = word;
            } else {
                currentLine += word;
            }
        }

        lines.add(currentLine);

        try {

            PDPageContentStream content = new PDPageContentStream(doc, page);

            content.beginText();
            content.setFont(font, fontSize);
            content.setLeading(leading);
            content.newLineAtOffset(startX, startY);



            for (String line : lines) {
                content.showText(line);
                content.newLineAtOffset(0, -leading);
            }

            content.endText();
            content.close();


        } catch (Exception e) {/*e.printStackTrace();*/}

        try {
            doc.save(fileName + ".pdf");
        } catch (IOException e) {}

        try {
            doc.close();
        } catch (IOException e) {}

        return doc;
    }
}


