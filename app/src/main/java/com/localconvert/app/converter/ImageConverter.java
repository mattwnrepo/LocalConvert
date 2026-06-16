package com.localconvert.app.converter;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Converts images between formats using Android's built-in Bitmap API.
 * No external libraries needed — fully offline, zero permissions beyond storage.
 */
public class ImageConverter {

    /**
     * @param context app context
     * @param sourceUri  URI of the source image
     * @param format  one of "JPEG", "PNG", "WEBP", "BMP"
     * @return true on success
     */
    public static boolean convert(Context context, Uri sourceUri, String format) {
        try {
            // 1. Decode source
            InputStream in = context.getContentResolver().openInputStream(sourceUri);
            if (in == null) return false;
            Bitmap bitmap = BitmapFactory.decodeStream(in);
            in.close();
            if (bitmap == null) return false;

            // 2. Determine output mime + extension
            Bitmap.CompressFormat compressFormat;
            String mimeType;
            String extension;
            switch (format) {
                case "PNG":
                    compressFormat = Bitmap.CompressFormat.PNG;
                    mimeType = "image/png";
                    extension = ".png";
                    break;
                case "WEBP":
                    compressFormat = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                            ? Bitmap.CompressFormat.WEBP_LOSSLESS
                            : Bitmap.CompressFormat.WEBP;
                    mimeType = "image/webp";
                    extension = ".webp";
                    break;
                case "BMP":
                    // Android has no native BMP encoder; save as PNG with .bmp note
                    // For true BMP, a library like AndroidBMP would be needed.
                    compressFormat = Bitmap.CompressFormat.PNG;
                    mimeType = "image/png";
                    extension = "_asbmp.png";
                    break;
                default: // JPEG
                    compressFormat = Bitmap.CompressFormat.JPEG;
                    mimeType = "image/jpeg";
                    extension = ".jpg";
                    break;
            }

            // 3. Save to MediaStore Downloads (no WRITE_EXTERNAL_STORAGE needed on API 29+)
            String outName = "localconvert_" + System.currentTimeMillis() + extension;
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, outName);
            values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri outUri = context.getContentResolver()
                    .insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (outUri == null) return false;

            OutputStream out = context.getContentResolver().openOutputStream(outUri);
            if (out == null) return false;

            boolean ok = bitmap.compress(compressFormat, 90, out);
            out.close();
            bitmap.recycle();
            return ok;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
