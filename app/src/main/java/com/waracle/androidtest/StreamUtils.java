package com.waracle.androidtest;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Riad on 20/05/2015.
 */
public final class StreamUtils {
    private static final String TAG = StreamUtils.class.getSimpleName();
    private static final int BUFFER_SIZE = 1024;

    // Can you see what's wrong with this???
    public static byte[] readUnknownFully(InputStream stream) throws IOException {

        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int len = 0;
        while ((len = stream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
