package com.anne.paint_xfermaode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

open class XfermodeEraserView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    lateinit var mPaint: Paint
    lateinit var mDstBmp: Bitmap, mSrcBmp:Bitmap, mTxtBmp :Bitmap
    private Path mPath;

    init {

    }
}