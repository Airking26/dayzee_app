package com.timenoteco.timenote.listeners;

import android.graphics.Bitmap;

import java.util.List;

public interface WebSearcherListener {
    void asyncComplete(List<Bitmap> data);
}