package proccessingPackage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class IO

{

    public static boolean folderCreated = false;

    public static File getExternalStorageDir(Context context) {
        // Get the directory in external storage of the app.

       File file = null;

        if(isExternalStorageWritable())

        {

            file = new File(context.getExternalFilesDir(
                    Environment.DIRECTORY_DOCUMENTS), "OCR Documents - Coeus");
            if (!file.mkdirs()) {
                System.out.println("Directory not created");
            }


        }

        return file;
    }


    //Function: IsExternalStorageWritable?
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean getFolderCreated ()

    {

        return folderCreated;

    }

    //converts inputstream of a file to a byte array
    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }



}
