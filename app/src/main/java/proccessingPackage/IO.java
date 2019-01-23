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

    private static FileOutputStream fos;

    //Function: create a file in a folder
//    public static boolean createFileInFolder(String fileName) {
//        if (isExternalStorageWritable()) {
//            String path = Environment.getExternalStorageDirectory() + "/" + "OCR Documents";
//            File folder = new File(path);
//            if (!folder.exists()) {
//                folder.mkdirs();
//                folderCreated = true;
//            }
//
//           File txtFile = new File(path, fileName);
//            try {
//                fos = new FileOutputStream(txtFile);
//                return true;
//            } catch (IOException e) {
//                System.out.println("failed");
//                return false;
//            }
//        } else
//            return false;
//    }

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

    public static Bitmap orientThumbnail(String filename, Bitmap bitmap) {

        Bitmap rotatedBitmap = null;

        try {
            ExifInterface ei = new ExifInterface(filename);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:

                default:
                    rotatedBitmap = bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotatedBitmap;

    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

}
