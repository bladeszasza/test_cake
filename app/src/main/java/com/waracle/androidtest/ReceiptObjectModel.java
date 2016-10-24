package com.waracle.androidtest;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by blade on 2016. 10. 21..
 */

public final class ReceiptObjectModel {


    private static final String TAG = ReceiptObjectModel.class.getSimpleName();
    private String mTitle;
    private String mDesc;
    private String mImage;


    public ReceiptObjectModel(final String title, final String desc, final String image) {
        this.mTitle = title;
        this.mDesc = desc;
        this.mImage = image;
    }

    public ReceiptObjectModel(final JSONObject jsonObject) {
        try {
            this.mTitle = jsonObject.getString("title");
            this.mDesc = jsonObject.getString("desc");
            this.mImage = jsonObject.getString("image");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            this.mTitle = "";
            this.mDesc = "";
            this.mImage = "";
        }
    }


    public String getTitle() {
        return mTitle != null ? mTitle : "";
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDesc() {
        return mDesc != null ? mDesc : "";
    }

    public void setDesc(String desc) {
        this.mDesc = desc;
    }

    public String getImage() {
        return mImage != null ? mImage : "";
    }

    public void setImage(String image) {
        this.mImage = image;
    }
}
