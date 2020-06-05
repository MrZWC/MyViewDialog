package com.zwc.viewdialog;

import java.util.LinkedList;

/**
 * Created by Android Studio.
 * User: zuoweichen
 * Date: 2020/5/26
 * Time: 11:38
 */
public class BackStack {
    public interface BackLayer {
        boolean onBackPressed();
    }

    private final LinkedList<BackLayer> mBackLayers = new LinkedList<>();

    public boolean onBackPressed() {
        BackLayer last = mBackLayers.peekLast();
        if (last != null) {
            return last.onBackPressed();
        }

        return false;
    }

    public boolean isEmpty() {
        return mBackLayers.isEmpty();
    }

    public void add(BackLayer backLayer) {
        mBackLayers.add(backLayer);
    }

    public void remove(BackLayer backLayer) {
        mBackLayers.remove(backLayer);
    }
}
