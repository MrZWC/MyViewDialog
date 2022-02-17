package com.zwc.viewdialog;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.IdRes;
import androidx.core.view.ViewCompat;


import io.github.idonans.core.thread.Threads;

/**
 * Created by Android Studio.
 * User: zuoweichen
 * Date: 2020/5/26
 * Time: 11:38
 */
public class ViewBackLayer implements BackStack.BackLayer {

    protected final View mDecorView;
    protected final ViewGroup mParentView;
    protected final WindowBackStackDispatcher mDispatcher;

    protected boolean mCancelable = true;
    protected boolean mRequestSystemInsets = true;
    protected boolean mShown;

    public ViewBackLayer(WindowBackStackDispatcher dispatcher, View decorView, ViewGroup parentView) {
        mDispatcher = dispatcher;
        mDecorView = decorView;
        mParentView = parentView;
    }

    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
    }

    public void setRequestSystemInsets(boolean requestSystemInsets) {
        mRequestSystemInsets = requestSystemInsets;
    }

    public View getDecorView() {
        return mDecorView;
    }

    public ViewGroup getParentView() {
        return mParentView;
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return mDecorView.findViewById(id);
    }

    @Override
    public boolean onBackPressed() {
        if (mOnBackPressedListener != null) {
            if (mOnBackPressedListener.onBackPressed()) {
                return true;
            }
        }

        if (mCancelable) {
            hide(true);
        }
        return true;
    }

    public boolean isCancelable() {
        return mCancelable;
    }

    public boolean isRequestSystemInsets() {
        return mRequestSystemInsets;
    }

    public boolean isShown() {
        return mShown;
    }

    public void show() {
        showInternal(true);
    }

    protected void showInternal(boolean attach) {
        if (mShown) {
            BackStackLog.e("already shown");
            return;
        }

        mShown = true;
        mDispatcher.getBackStack().add(this);

        notifyShowListener();

        if (attach) {
            attachViewToParent();
        }
    }

    public void hide(boolean cancel) {
        hideInternal(cancel, true);
    }

    protected void hideInternal(boolean cancel, boolean detach) {
        if (!mShown) {
            BackStackLog.e("not shown");
            return;
        }

        mShown = false;
        mDispatcher.getBackStack().remove(this);

        notifyHideListener(cancel);

        if (detach) {
            detachViewFromParent();
        }
    }

    protected void attachViewToParent() {
        if (mParentView == null) {
            BackStackLog.e("parent view is null");
            return;
        }

        if (mDecorView.getParent() != null) {
            BackStackLog.e("decor view's parent is not null %s", mDecorView.getParent());
            return;
        }

        mParentView.addView(mDecorView);
        if (mRequestSystemInsets) {
            ViewCompat.requestApplyInsets(mParentView);
        }

        if (mOnAddToParentListener != null) {
            mOnAddToParentListener.onAddToParent();
        }
    }

    protected void detachViewFromParent() {
        final ViewParent parent = mDecorView.getParent();
        if (!(parent instanceof ViewGroup)) {
            BackStackLog.e("decor view's parent is not instance of ViewGroup %s", parent);
            return;
        }

        if (parent != mParentView) {
            BackStackLog.e("decor view's parent changed to another, require:%s, found:%s", mParentView, parent);
            return;
        }
        //线程安全
        Threads.postUi(new Runnable() {
            @Override
            public void run() {
                ((ViewGroup) parent).removeView(mDecorView);
                if (mRequestSystemInsets) {
                    ViewCompat.requestApplyInsets(mParentView);
                }
                if (mOnRemoveFromParentListener != null) {
                    mOnRemoveFromParentListener.onRemoveFromParent();
                }
            }
        });

    }

    public interface OnHideListener {
        void onHide(boolean cancel);
    }

    private OnHideListener mOnHideListener;

    public void setOnHideListener(OnHideListener onHideListener) {
        mOnHideListener = onHideListener;
    }

    protected void notifyHideListener(boolean cancel) {
        if (mOnHideListener != null) {
            mOnHideListener.onHide(cancel);
        }
    }

    public interface OnShowListener {
        void onShow();
    }

    private OnShowListener mOnShowListener;

    public void setOnShowListener(OnShowListener onShowListener) {
        mOnShowListener = onShowListener;
    }

    protected void notifyShowListener() {
        if (mOnShowListener != null) {
            mOnShowListener.onShow();
        }
    }

    public interface OnAddToParentListener {
        void onAddToParent();
    }

    private OnAddToParentListener mOnAddToParentListener;

    public void setOnAddToParentListener(OnAddToParentListener onAddToParentListener) {
        mOnAddToParentListener = onAddToParentListener;
    }

    public interface OnRemoveFromParentListener {
        void onRemoveFromParent();
    }

    private OnRemoveFromParentListener mOnRemoveFromParentListener;

    public void setOnRemoveFromParentListener(OnRemoveFromParentListener onRemoveFromParentListener) {
        mOnRemoveFromParentListener = onRemoveFromParentListener;
    }

    public interface OnBackPressedListener {
        boolean onBackPressed();
    }

    private OnBackPressedListener mOnBackPressedListener;

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        mOnBackPressedListener = onBackPressedListener;
    }

}
