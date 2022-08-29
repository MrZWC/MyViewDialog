package com.zwc.myviewdialog

import android.app.Activity
import android.view.ViewGroup
import android.view.Window
import com.zwc.viewdialog.ViewDialog

/**
 * ClassName TestDiaog
 * User: zuoweichen
 * Date: 2022/8/29 15:13
 * Description: 描述
 */
class TestDialog {
    private var mActivity: Activity
    private var mViewDialog: ViewDialog

    constructor(activity: Activity) {
        this.mActivity = activity
        mViewDialog = ViewDialog.Builder(activity)
            .setParentView(activity.findViewById(Window.ID_ANDROID_CONTENT) as ViewGroup)
            .setCancelable(true)
            .setContentView(R.layout.dialog_test_layout)
            .dimBackground(true)
            .create()
    }

    fun show() {
        mViewDialog.show()
    }

    fun hide() {
        mViewDialog.hide(false)
    }
}