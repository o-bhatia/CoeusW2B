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
    Creates document from string of full text, preliminary application usage
    @params: String of the full text (no newline character), string of the file name, font, font size

    public static PDDocument writeToDoc (String text, String fileName, PDFont font, float fontSize) {
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        doc.addPage(page);
        List<String> lines = new ArrayList<>();
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


        } catch (Exception e) {e.printStackTrace();}

        try {
            doc.save(fileName + ".pdf");
        } catch (IOException e) {}

        try {
            doc.close();
        } catch (IOException e) {}

        Log.d("PDF", "Document created");

        return doc;
    }
    */

    public static void write (String text, String fileName,  PDFont font, float fontSize, File root) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        List<String> lines = new ArrayList<>();
        String currentLine = "";

        PDPageContentStream contentStream;
        PDRectangle mediaBox = page.getMediaBox();
        float margin = 72;
        float width = mediaBox.getWidth() - 2 * margin;
        float startX = mediaBox.getLowerLeftX() + margin;
        float startY = mediaBox.getUpperRightY() - margin;
        int thisWidth = 0;
        float leading = 1.5f * fontSize;

        Log.d("TEST", "formatting text");
        String[] words = text.split(" ");

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

        Log.d("TEST", "Starting stream");

        try {
            // Define a content stream for adding to the PDF
            contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(font, fontSize);
            contentStream.setLeading(leading);
            contentStream.newLineAtOffset(startX, startY);


            for (String line : lines) {

                Log.d("TEST", line);

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

            contentStream.endText();
            contentStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String path = root.getAbsolutePath() + "/";
            document.save(path + fileName + ".pdf");
            Log.d("PDF", "document saved to " + path + fileName + ".pdf");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            document.close();
        } catch (IOException e) {
        }

        Log.d("PDF", "Document created");

    }
}
