package com.zwc.viewdialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.AnimRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;


import com.google.common.base.Preconditions;

/**
 * Created by Android Studio.
 * User: zuoweichen
 * Date: 2020/5/26
 * Time: 11:37
 */

public class ViewDialog extends ViewBackLayer {

    private final Activity mActivity;
    private final ViewGroup mContentParentView;

    private View mContentView;
    private Animation mContentViewShowAnimation;
    private Animation mContentViewHideAnimation;

    private ViewDialog(Activity activity, View decorView, ViewGroup parentView, ViewGroup contentParentView) {
        super(WindowBackStackDispatcher.from(activity.getWindow()), decorView, parentView);
        mActivity = activity;
        mContentParentView = contentParentView;
        decorView.setOnClickListener(v -> {
            BackStackLog.v("decor view onClick");
            onBackPressed();
        });
    }

    public Activity getActivity() {
        return mActivity;
    }

    public ViewGroup getContentParentView() {
        return mContentParentView;
    }

    private void setContentView(@Nullable View contentView) {
        mContentParentView.removeAllViews();
        if (contentView != null) {
            mContentParentView.addView(contentView);
        }
        mContentView = contentView;
    }

    @Nullable
    public View getContentView() {
        return mContentView;
    }

    private void setContentViewAnimator(Animation showAnimation, Animation hideAnimation) {
        mContentViewShowAnimation = showAnimation;
        mContentViewHideAnimation = hideAnimation;

        if (mContentViewHideAnimation != null) {
            mContentViewHideAnimation.setAnimationListener(new Animation.AnimationListener() {

                private boolean mCanceled;

                @Override
                public void onAnimationStart(Animation animation) {
                    mCanceled = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCanceled) {
                        BackStackLog.v("ViewDialog content view hide anim end with cancel");
                        return;
                    }

                    if (isShown()) {
                        BackStackLog.e("ViewDialog is shown after content view hide anim end");
                        return;
                    }

                    if (mContentView == null) {
                        BackStackLog.e("ViewDialog content view is null after content view hide anim end");
                        return;
                    }

                    mActivity.runOnUiThread(() -> detachViewFromParent());
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
    }

    @Override
    public void show() {
        if (isShown()) {
            BackStackLog.e("already shown");
            return;
        }

        clearContentViewAnimator();
        if (mContentView == null || mContentViewShowAnimation == null) {
            super.show();
            return;
        }

        super.show();
        mContentView.startAnimation(mContentViewShowAnimation);
    }

    @Override
    public void hide(boolean cancel) {
        if (!isShown()) {
            BackStackLog.e("not shown");
            return;
        }

        clearContentViewAnimator();
        if (mContentView == null || mContentViewHideAnimation == null) {
            super.hide(cancel);
            return;
        }

        super.hideInternal(cancel, false);
        mContentView.startAnimation(mContentViewHideAnimation);
    }

    private void clearContentViewAnimator() {
        mContentView.clearAnimation();
        if (mContentViewShowAnimation != null) {
            mContentViewShowAnimation.reset();
        }
        if (mContentViewHideAnimation != null) {
            mContentViewHideAnimation.reset();
        }
    }

    public static class Builder {

        private Activity mActivity;
        private int mDecorViewLayoutRes = R.layout.backstack_view_dialog_dim_background;
        private ViewGroup mParentView;

        private boolean mCancelable = true;
        private OnHideListener mOnHideListener;
        private OnShowListener mOnShowListener;

        private boolean mRequestSystemInsets = true;

        private OnAddToParentListener mOnAddToParentListener;
        private OnRemoveFromParentListener mOnRemoveFromParentListener;

        private OnBackPressedListener mOnBackPressedListener;

        @LayoutRes
        private int mContentViewLayoutRes;
        @AnimRes
        private int mContentViewShowAnimationRes;
        @AnimRes
        private int mContentViewHideAnimationRes;

        public Builder(Activity activity) {
            mActivity = activity;
        }

        public Builder setDecorView(@LayoutRes int layoutRes) {
            mDecorViewLayoutRes = layoutRes;
            return this;
        }

        public Builder dimBackground(boolean dim) {
            setDecorView(dim ? R.layout.backstack_view_dialog_dim_background : R.layout.backstack_view_dialog_background);
            return this;
        }

        public Builder setParentView(ViewGroup parentView) {
            mParentView = parentView;
            return this;
        }

        public Builder setContentView(@LayoutRes int layoutRes) {
            mContentViewLayoutRes = layoutRes;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public Builder setOnHideListener(OnHideListener onHideListener) {
            mOnHideListener = onHideListener;
            return this;
        }

        public Builder setOnShowListener(OnShowListener onShowListener) {
            mOnShowListener = onShowListener;
            return this;
        }

        public Builder setOnAddToParentListener(OnAddToParentListener onAddToParentListener) {
            mOnAddToParentListener = onAddToParentListener;
            return this;
        }

        public Builder setOnRemoveFromParentListener(OnRemoveFromParentListener onRemoveFromParentListener) {
            mOnRemoveFromParentListener = onRemoveFromParentListener;
            return this;
        }

        public Builder setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
            mOnBackPressedListener = onBackPressedListener;
            return this;
        }

        public Builder setContentViewShowAnimation(@AnimRes int animationRes) {
            mContentViewShowAnimationRes = animationRes;
            return this;
        }

        public Builder setContentViewHideAnimation(@AnimRes int animationRes) {
            mContentViewHideAnimationRes = animationRes;
            return this;
        }

        public Builder setRequestSystemInsets(boolean requestSystemInsets) {
            this.mRequestSystemInsets = requestSystemInsets;
            return this;
        }

        @SuppressLint("ResourceType")
        public Builder defaultAnimation() {
            setContentViewShowAnimation(R.anim.backstack_slide_in_from_bottom);
            setContentViewHideAnimation(R.anim.backstack_slide_out_to_bottom);
            return this;
        }

        public ViewDialog create() {
            Preconditions.checkArgument(mActivity != null, "Activity is null");
            Preconditions.checkArgument(mDecorViewLayoutRes > 0, "invalid decor view layout res %s", mDecorViewLayoutRes);
            Preconditions.checkArgument(mParentView != null, "parent view not set or null");

            View decorView = mActivity.getLayoutInflater().inflate(mDecorViewLayoutRes, mParentView, false);
            ViewGroup contentParentView = decorView.findViewById(R.id.content_parent);

            Preconditions.checkArgument(contentParentView != null, "content parent view not found,\ndecor view layout res must define one ViewGroup with id R.id.content_parent");

            View contentView = null;
            if (mContentViewLayoutRes != 0) {
                contentView = mActivity.getLayoutInflater().inflate(mContentViewLayoutRes, contentParentView, false);
            }

            ViewDialog viewDialog = new ViewDialog(mActivity, decorView, mParentView, contentParentView);
            viewDialog.setContentView(contentView);
            viewDialog.setCancelable(mCancelable);
            viewDialog.setOnHideListener(mOnHideListener);
            viewDialog.setOnShowListener(mOnShowListener);
            viewDialog.setOnBackPressedListener(mOnBackPressedListener);
            viewDialog.setOnAddToParentListener(mOnAddToParentListener);
            viewDialog.setOnRemoveFromParentListener(mOnRemoveFromParentListener);
            viewDialog.setRequestSystemInsets(mRequestSystemInsets);
            viewDialog.setContentViewAnimator(
                    mContentViewShowAnimationRes != 0 ? AnimationUtils.loadAnimation(mActivity, mContentViewShowAnimationRes) : null,
                    mContentViewHideAnimationRes != 0 ? AnimationUtils.loadAnimation(mActivity, mContentViewHideAnimationRes) : null);
            return viewDialog;
        }

    }

}