package com.waracle.androidtest;

import android.graphics.Bitmap;
import android.widget.ImageView;


import java.util.ArrayList;

/**
 * Created by blade on 2016. 10. 21..
 */
public interface TaskCallbacks {
        void onCancelled();
        void onPostExecute(final ArrayList<ReceiptObjectModel> array);
        void onPostExecute(final Bitmap bitmap, final ImageView imageView);
}
