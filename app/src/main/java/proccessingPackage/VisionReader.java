package proccessingPackage;

import com.google.cloud.vision.v1p3beta1.AnnotateImageRequest;
import com.google.cloud.vision.v1p3beta1.AnnotateImageResponse;
import com.google.cloud.vision.v1p3beta1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1p3beta1.Block;
import com.google.cloud.vision.v1p3beta1.Feature;
import com.google.cloud.vision.v1p3beta1.Feature.Type;
import com.google.cloud.vision.v1p3beta1.Image;
import com.google.cloud.vision.v1p3beta1.ImageAnnotatorClient;
import com.google.cloud.vision.v1p3beta1.ImageContext;
import com.google.cloud.vision.v1p3beta1.Page;
import com.google.cloud.vision.v1p3beta1.Paragraph;
import com.google.cloud.vision.v1p3beta1.Symbol;
import com.google.cloud.vision.v1p3beta1.TextAnnotation;
import com.google.cloud.vision.v1p3beta1.Word;
import com.google.protobuf.ByteString;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.commons.text.WordUtils;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class VisionReader {

    /*
    Method that reads the documents, returns a list of the paragraphs in strings will be used in final implementation
    @params
    filePath: Location of image
    handwrittenDoc: boolean that dictates whether the image is handwritten or typed
     */

    public static List<String> read (String filePath, boolean handwrittenDoc) throws Exception {

        //initialize necessary data structure
        List<AnnotateImageRequest> requests = new ArrayList<>();
        List<String> Paragraphs = new ArrayList<>();

        //get image and write ByteString
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        //Create image from ByteString
        Image img = Image.newBuilder().setContent(imgBytes).build();

        //Add feature that tells machine that program is analysing a document
        Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();

        // Set the Language Hint codes for handwritten OCR if boolean was passed
        if (handwrittenDoc) {
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

        //try-catch for actual request and reception of response
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
            client.close();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    return null;
                }

                //Getting Text Annotation
                //Will be taken in paragraph form so as to facilitate easy insert of images into document
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
                                }
                                paraText = String.format("%s %s", paraText, wordText);
                            }

                            //add paragraph to list
                            Paragraphs.add(paraText);
                        }
                        pageText = pageText + blockText;
                    }
                }

            }
        } catch (Exception e) {/*e.printStackTrace();*/} //catch exception

        return Paragraphs;
    }

    /*
    Method that reads the documents, returns a full string of the text, to be used for preliminary application
    @params
    filePath: Location of image
    out: console for outputting errors (will be removed for final testing)
    handwrittenDoc: boolean that dictates whether the image is handwritten or typed
     */
    public static String read (String filePath, PrintStream out, boolean handwrittenDoc) throws Exception {

        //initialize necessary data structure
        List<AnnotateImageRequest> requests = new ArrayList<>();

        //get image and write ByteString
        ByteString imgBytes = ByteString.readFrom(new FileInputStream(filePath));

        //Create image from ByteString
        Image img = Image.newBuilder().setContent(imgBytes).build();

        //Add feature that tells machine that program is analysing a document
        Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();

        // Set the Language Hint codes for handwritten OCR if boolean was passed
        if (handwrittenDoc) {
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

        //try-catch for actual request and reception of response
        String pageText = null;
        try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
            BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();
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
        } catch (Exception e) {/*e.printStackTrace();*/} //catch exception

        return pageText;
    }

    /*
    Creates document from list of paragraphs and filename, final application usage
    @param: list of paragraphs, string for file name
     */
    public static PDDocument writeToDoc (List<String> Paragraphs, String fileName) {
       return null;
    }


    /*
    Creates document from string of full text, preliminary application usage
    @params: String of the full text, string of the file name
     */
    public static PDDocument writeToDoc_Depecrated (String text, String fileName) {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        String[] lines = null;

        lines = WordUtils.wrap(text, 100).split("\\r?\\n");

        try {

            PDPageContentStream content = new PDPageContentStream(doc, page);

            content.beginText();

            content.setFont(PDType1Font.TIMES_ROMAN, 10);
            content.setLeading(14.5f);
            content.newLineAtOffset(100,700);

            for (String line : lines) {
                content.showText(line);
                content.newLine();
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
