package com.waracle.androidtest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Fragment is responsible for loading in some JSON and
 * then displaying a list of cakes with images.
 * Fix any crashes
 * Improve any performance issues
 * Use good coding practices to make code more secure
 */
public final class PlaceholderFragment extends ListFragment implements TaskCallbacks {

    private static final String TAG = PlaceholderFragment.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_NETWORK = 1;
    private static String JSON_URL = "https://gist.githubusercontent.com/hart88/198f29ec5114a3ec3460/" +
            "raw/8dd19a88f9b8d24c23d9960f3300d0c917a4f07c/cake.json";


    private TaskCallbacks mCallbacks;
    private NetworkingTask mTask = null;
    private LruCache<String, Bitmap> mMemoryCache;

    public PlaceholderFragment() { /**/ }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //retain fragment
        setRetainInstance(true);
        // I would normaly use this lib : http://square.github.io/retrofit

        int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.INTERNET);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            startLoadinTask();
        } else {
            askForNetworkingPermission();
        }


        if (mMemoryCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;

            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
                }
            };
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    private void askForNetworkingPermission() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_NETWORK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_NETWORK: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLoadinTask();
                } else {
                    // permission denied, show empty table message and/or some kind of error
                }
                return;
            }
        }
    }

    private void startLoadinTask() {
        if (mTask == null) {
            mTask = new NetworkingTask();
            mTask.execute();
        }
    }

    private JSONArray loadData() throws IOException, JSONException {
        URL url = new URL(JSON_URL);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        StringBuilder result = new StringBuilder();
        try {
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return new JSONArray(result.toString());
            }
        } finally {
            urlConnection.disconnect();
        }

        return new JSONArray();
    }

    /**
     * Returns the charset specified in the Content-Type of this header,
     * or the HTTP default (ISO-8859-1) if none can be found.
     */
    public static String parseCharset(String contentType) {
        if (contentType != null) {
            String[] params = contentType.split(",");
            for (int i = 1; i < params.length; i++) {
                String[] pair = params[i].trim().split("=");
                if (pair.length == 2) {
                    if (pair[0].equals("charset")) {
                        return pair[1];
                    }
                }
            }
        }
        return "UTF-8";
    }


    @Override
    public void onCancelled() {
        //display empty table message and/or error

    }

    @Override
    public void onPostExecute(final ArrayList<ReceiptObjectModel> array) {
        setListAdapter(new MyAdapter(array));

    }

    @Override
    public void onPostExecute(final Bitmap bitmap, final ImageView imageView) {
        imageView.setImageBitmap(bitmap);
    }


    private final class MyAdapter extends BaseAdapter {

        // Can you think of a better way to represent these items???
        private ArrayList<ReceiptObjectModel> mItems;

        public MyAdapter(ArrayList<ReceiptObjectModel> items) {
            mItems = items;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(final int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ViewHolder root;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_layout, parent, false);

                root = new ViewHolder();
                root.title = (TextView) convertView.findViewById(R.id.title);
                root.image = (ImageView) convertView.findViewById(R.id.image);
                root.desc = (TextView) convertView.findViewById(R.id.desc);
                convertView.setTag(root);
            } else {
                root = (ViewHolder) convertView.getTag();
            }

            if (root != null) {
                ReceiptObjectModel object = (ReceiptObjectModel) getItem(position);
                root.title.setText(object.getTitle());
                root.desc.setText(object.getDesc());
                // I would normaly use this lib : http://square.github.io/picasso/
                Bitmap bmp = mMemoryCache.get(object.getImage());
                if (bmp == null) {
                    ImageLoadingTask imageLoadingTask = new ImageLoadingTask();
                    imageLoadingTask.setImageViewRefference(root.image);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        imageLoadingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, object.getImage());
                    } else {
                        imageLoadingTask.execute(object.getImage());
                    }
                } else {
                    root.image.setImageBitmap(bmp);
                }
            }

            return convertView;
        }

        private final class ViewHolder {
            TextView title;
            TextView desc;
            ImageView image;
        }

    }

    private class NetworkingTask extends AsyncTask<Void, Void, ArrayList<ReceiptObjectModel>> {


        @Override
        protected ArrayList<ReceiptObjectModel> doInBackground(Void... ignore) {
            return loadAndConvertJsonArray();
        }


        private ArrayList<ReceiptObjectModel> loadAndConvertJsonArray() {
            try {
                ArrayList<ReceiptObjectModel> list = new ArrayList<>();
                JSONArray jsonArray = loadData();
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(new ReceiptObjectModel(jsonArray.getJSONObject(i)));
                }
                return list;
            } catch (IOException | JSONException e) {
                Log.e(TAG, e.getMessage());
                // returns empty array to prevent the app from crashing
                return new ArrayList<>();
            }
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) {
                mCallbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(final ArrayList<ReceiptObjectModel> array) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(array);
            }
        }
    }


    private class ImageLoadingTask extends AsyncTask<String, Void, Bitmap> {

        private ImageLoader mImageLoader;
        private ImageView mImageViewRefference;

        public void setImageViewRefference(ImageView mImageViewRefference) {
            this.mImageViewRefference = mImageViewRefference;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mImageLoader = new ImageLoader();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return loadBitmap(strings[0]);
        }

        private Bitmap loadBitmap(String url) {
            try {
                Bitmap bmp = mImageLoader.load(url);
                mMemoryCache.put(url, bmp);
                return bmp;
            } catch (InvalidParameterException e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            if (mCallbacks != null) {
                mCallbacks.onCancelled();
            }
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            if (mCallbacks != null) {

                mCallbacks.onPostExecute(bitmap, mImageViewRefference);
            }
            mImageLoader = null;
            mImageViewRefference = null;

        }


    }

}