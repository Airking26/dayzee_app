package com.timenoteco.timenote.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import com.timenoteco.timenote.listeners.WebSearcherListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class SearchTask extends AsyncTask<String, Void, List<Bitmap>> {

    private WebSearcherListener delegate;
    private long firstItemID = 1;
    private boolean type = false;
    private android.content.Context context;

    public SearchTask(WebSearcherListener delegate) {
        this.delegate = delegate;
    }

    public void setFirstItemID(long firstItemID) {
        this.firstItemID = firstItemID;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public boolean isType() {
        return type;
    }

    public long getFirstItemID() {
        return firstItemID;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    @Override
    protected List<Bitmap> doInBackground(String... params) {
        Customsearch.Builder customSearch = new Customsearch.Builder(new NetHttpTransport(), new JacksonFactory(), null);
        customSearch.setApplicationName("Search");
        try {
            com.google.api.services.customsearch.Customsearch.Cse.List list = customSearch.build().cse().list(params[0]);
            list.setKey("AIzaSyBhM9HQo1fzDlwkIVqobfmrRmEMCWTU1CA");
            list.setCx("018194552039993531144:aj_el4m5plw");
            list.setStart(firstItemID);
            if (type) {
                list.setSearchType("image");
            }
            Search results = list.execute();
            //List<GObject> objects = new ArrayList<>();
            List<Bitmap> images = new ArrayList<>();
            if (results.getItems() != null)
                if (type) {
                    for (Result res : results.getItems()) {
                        if (res != null) {

                            Glide.with(context).asBitmap().load(Uri.parse(res.getLink())).into(new Target<Bitmap>() {
                                @Override
                                public void onLoadStarted(@androidx.annotation.Nullable Drawable placeholder) {

                                }

                                @Override
                                public void onLoadFailed(@androidx.annotation.Nullable Drawable errorDrawable) {

                                }

                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @androidx.annotation.Nullable Transition<? super Bitmap> transition) {
                                    images.add(resource);
                                }

                                @Override
                                public void onLoadCleared(@androidx.annotation.Nullable Drawable placeholder) {

                                }

                                @Override
                                public void getSize(@NonNull SizeReadyCallback cb) {

                                }

                                @Override
                                public void removeCallback(@NonNull SizeReadyCallback cb) {

                                }

                                @Override
                                public void setRequest(@androidx.annotation.Nullable Request request) {

                                }

                                @androidx.annotation.Nullable
                                @Override
                                public Request getRequest() {
                                    return null;
                                }

                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onStop() {

                                }

                                @Override
                                public void onDestroy() {

                                }
                            });

                            //Rect rect = new Rect();
                            //rect.contains(100, 100, 100, 100);
                            //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            //Bitmap bitmap = decodeSampledBitmapFromResource(new URL(res.getLink()), rect, 100, 100);
                            //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            //objects.add(new GObject(res.getTitle(), res.getImage().getThumbnailLink(), bitmap));
                        }
                    }
                } else {
                    for (Result result : results.getItems()) {
                        //if (result != null)
                            //objects.add(new GObject(result.getTitle(), result.getFormattedUrl()));
                    }
                }
            return images;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Bitmap> items) {
        super.onPostExecute(items);
        this.delegate.asyncComplete(items);
    }

    @Nullable
    public static Bitmap decodeSampledBitmapFromResource(URL url, Rect resId, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(url.openConnection().getInputStream(), resId, options);
            //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream(), resId, options);
            return bmp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}