package com.dayzeeco.dayzee.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import java.io.File
import java.io.FileOutputStream
import java.lang.Integer.min

object ImageCompressor {

    @Throws(Exception::class)
    fun compressBitmap(context: Context, originalImageFile: File, cb: ((File) -> Unit)? = null) {
        val bitmap = updateDecodeBounds(originalImageFile)
        val file = context.getFileStreamPath(originalImageFile.name)
        val fOut = FileOutputStream(file!!)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut)
        fOut.flush() // Not really required
        fOut.close() // do not forget to close the stream
        bitmap.recycle() //recycle the bitmap
        cb?.invoke(file)
    }

    /**
     * This compress the original file.
     */
    @Throws(Exception::class)
    fun compressCurrentBitmapFile(originalImageFile: File) {
        val bitmap = updateDecodeBounds(originalImageFile)
        val fOut = FileOutputStream(originalImageFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut)
        fOut.flush() // Not really required
        fOut.close() // do not forget to close the stream
        bitmap.recycle() //recycle the bitmap
    }

    /**
     * Measure decodeBounds of the bitmap from given File.
     */
    private fun updateDecodeBounds(imageFile: File): Bitmap {
        return BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(imageFile.absolutePath, this)
            val sampleHeight = if (outWidth > outHeight) 900 else 1100
            val sampleWidth = if (outWidth > outHeight) 1100 else 900
            /**
             * You can tweak the sizes 900, 1100.
             * The bigger the number is, the more details you can keep.
             * The lesser, the lesser quality of details.
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                inSampleSize = min(outWidth / sampleWidth, outHeight / sampleHeight)
            }
            inJustDecodeBounds = false
            BitmapFactory.decodeFile(imageFile.absolutePath, this)
        }
    }
}