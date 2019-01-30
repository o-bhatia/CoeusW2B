package proccessingPackage;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDDocumentCatalog;
import com.tom_roush.pdfbox.pdmodel.PDPage;
import com.tom_roush.pdfbox.pdmodel.PDPageContentStream;
import com.tom_roush.pdfbox.pdmodel.common.PDRectangle;
import com.tom_roush.pdfbox.pdmodel.encryption.AccessPermission;
import com.tom_roush.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import com.tom_roush.pdfbox.pdmodel.font.PDFont;
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font;
import com.tom_roush.pdfbox.pdmodel.graphics.image.JPEGFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.LosslessFactory;
import com.tom_roush.pdfbox.pdmodel.graphics.image.PDImageXObject;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDAcroForm;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDCheckbox;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDComboBox;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDField;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDListBox;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDRadioButton;
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDTextField;
import com.tom_roush.pdfbox.rendering.PDFRenderer;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreatePDF {

    /*
    Method to write text to a PDF document
    @Params:
        text: text to be written
        fileName: filename of document
        font: user's requested font
        fontSize: user's requested font size
        root: directory to save document
     */
    public static void write (String text, String fileName,  PDFont font, float fontSize, File root) {
        //initialize variables
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        List<String> lines = new ArrayList<>();
        String currentLine = "";

        //create page dimensions (ie margins, width for writing)
        PDPageContentStream contentStream;
        PDRectangle mediaBox = page.getMediaBox();
        float margin = 72;
        float width = mediaBox.getWidth() - 2 * margin;
        float startX = mediaBox.getLowerLeftX() + margin;
        float startY = mediaBox.getUpperRightY() - margin;
        int thisWidth = 0;
        float leading = 1.5f * fontSize; //spacing between lines

        //split text into words
        String[] words = text.split(" ");

        //create lines of an acceptable width
        for (String word : words) {

            if (!currentLine.isEmpty()) {
                currentLine += " ";
            }

            try {
                thisWidth = (int) (fontSize * font.getStringWidth(currentLine + word) / 1000);
            } catch (Exception e) {

            }

            if (thisWidth > width) {
                lines.add(currentLine);

                currentLine = word;
            } else {
                currentLine += word;
            }
        }

        lines.add(currentLine);


        try {
            // Define a content stream for adding to the PDF
            contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setLeading(leading);
            contentStream.newLineAtOffset(startX, startY);


            for (String line : lines) {

                //check if line has a newline character, which is unrecognized by this library
                if (!line.contains("\n")) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -leading);
                }  else {
                    String[] lineSections = text.split("\n");

                    for (String section : lineSections) {
                        contentStream.showText(section);
                        contentStream.newLineAtOffset(0, -leading);
                    }
                }
            }
            //close content stream
            contentStream.endText();
            contentStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        //save file to specified directory
        try {
            String path = root.getAbsolutePath() + "/";
            document.save(path + fileName + ".pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            document.close();
        } catch (IOException e) {
        }


    }
}
