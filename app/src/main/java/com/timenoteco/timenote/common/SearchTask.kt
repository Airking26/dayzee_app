package com.timenoteco.timenote.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.AsyncTask
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.customsearch.Customsearch
import java.io.IOException
import java.net.URL



class SearchTask{

    enum class Result {
        SUCCESS,
        FAILURE
    }

    var firstItemID: Long = 1
    var isType = false
    private var context: Context? = null
    private val images: MutableList<String> = mutableListOf()

    fun setContext(context: Context?) {
        this.context = context
    }

    fun getImages(): MutableList<String> {
        return images
    }

    fun getImagesFromNet(query: String): Result {
        val customSearch = Customsearch.Builder(NetHttpTransport(), JacksonFactory(), null)
        customSearch.applicationName = "Search"
        try {
            val list =
                customSearch.build().cse().list(query)
            list.key = "AIzaSyBhM9HQo1fzDlwkIVqobfmrRmEMCWTU1CA"
            list.cx = "018194552039993531144:aj_el4m5plw"
            list.start = firstItemID
            if (isType) {
                list.searchType = "image"
            }
            val results = list.execute()
            if (results.items != null) if (isType) {
                for (res in results.items) {
                    if (res != null) {
                        val o = res
                        val p = results
                        images.add(res.link)
                        //Rect rect = new Rect();
                        //rect.contains(100, 100, 100, 100);
                        //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //Bitmap bitmap = decodeSampledBitmapFromResource(new URL(res.getLink()), rect, 100, 100);
                        //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        //objects.add(new GObject(res.getTitle(), res.getImage().getThumbnailLink(), bitmap));
                    }
                }
            }
            return Result.SUCCESS
        } catch (e: IOException) {
            e.printStackTrace()
            return Result.FAILURE
        }
    }

    companion object {
        fun decodeSampledBitmapFromResource(
            url: URL,
            resId: Rect?,
            reqWidth: Int,
            reqHeight: Int
        ): Bitmap? {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            try {
                BitmapFactory.decodeStream(url.openConnection().getInputStream(), resId, options)
                //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
                options.inJustDecodeBounds = false
                return BitmapFactory.decodeStream(
                    url.openConnection().getInputStream(),
                    resId,
                    options
                )
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null
        }

        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int
        ): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2
                while (halfHeight / inSampleSize > reqHeight
                    && halfWidth / inSampleSize > reqWidth
                ) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }
    }

}