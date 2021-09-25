package com.anne.paint_xfermaode

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.os.Bundle;
import android.os.PersistableBundle
import android.view.View;


/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



 class XfermodeActivity() : GraphiocsActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(SampleView(this))

    }

    private class SampleView(context: Context?) : View(context) {
        private val mSrcB: Bitmap
        private val mDstB: Bitmap
        private val mBG // background checker-board pattern
                : Shader

        override fun onDraw(canvas: Canvas) {
            canvas.drawColor(Color.WHITE)
            val labelP = Paint(Paint.ANTI_ALIAS_FLAG)
            labelP.textAlign = Paint.Align.CENTER
            val paint = Paint()
            paint.isFilterBitmap = false
            canvas.translate(15f, 35f)
            var x = 0
            var y = 0
            for (i in sModes.indices) {
                // draw the border
                paint.style = Paint.Style.STROKE
                paint.shader = null
                canvas.drawRect(
                    x - 0.5f, y - 0.5f,
                    x + W + 0.5f, y + H + 0.5f, paint
                )

                // draw the checker-board pattern
                paint.style = Paint.Style.FILL
                paint.shader = mBG
                canvas.drawRect(
                    x.toFloat(),
                    y.toFloat(),
                    (x + W).toFloat(),
                    (y + H).toFloat(),
                    paint
                )

                // draw the src/dst example into our offscreen bitmap
//                val sc = canvas.saveLayer(
//                    x.toFloat(), y.toFloat(), (x + W).toFloat(), (y + H).toFloat(), null,
//                    Canvas.MATRIX_SAVE_FLAG or
//                            Canvas.CLIP_SAVE_FLAG or
//                            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG or
//                            Canvas.FULL_COLOR_LAYER_SAVE_FLAG or
//                            Canvas.CLIP_TO_LAYER_SAVE_FLAG
//                )
                val sc = canvas.saveLayer(
                    x.toFloat(), y.toFloat(), (x + W).toFloat(), (y + H).toFloat(), null,
                    Canvas.ALL_SAVE_FLAG
                )
                canvas.translate(x.toFloat(), y.toFloat())
                canvas.drawBitmap(mDstB, 0f, 0f, paint)
                paint.xfermode = sModes.get(i)
                canvas.drawBitmap(mSrcB, 0f, 0f, paint)
                paint.xfermode = null
                canvas.restoreToCount(sc)

                // draw the label
                canvas.drawText(
                    sLabels[i], (
                            x + W / 2).toFloat(), y - labelP.textSize / 2, labelP
                )
                x += W + 10

                // wrap around when we've drawn enough for one row
                if ((i % ROW_MAX) == ROW_MAX - 1) {
                    x = 0
                    y += H + 30
                }
            }
        }

        companion object {
            private val W = 64
            private val H = 64
            private val ROW_MAX = 4 // number of samples per row
            private val sModes = arrayOf<Xfermode>(
                PorterDuffXfermode(PorterDuff.Mode.CLEAR),
                PorterDuffXfermode(PorterDuff.Mode.SRC),
                PorterDuffXfermode(PorterDuff.Mode.DST),
                PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),
                PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
                PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
                PorterDuffXfermode(PorterDuff.Mode.DST_IN),
                PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),
                PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
                PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
                PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
                PorterDuffXfermode(PorterDuff.Mode.XOR),
                PorterDuffXfermode(PorterDuff.Mode.DARKEN),
                PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
                PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
                PorterDuffXfermode(PorterDuff.Mode.SCREEN)
            )
            private val sLabels = arrayOf(
                "Clear", "Src", "Dst", "SrcOver",
                "DstOver", "SrcIn", "DstIn", "SrcOut",
                "DstOut", "SrcATop", "DstATop", "Xor",
                "Darken", "Lighten", "Multiply", "Screen"
            )
        }

        init {
            mSrcB = makeSrc(W, H)
            mDstB = makeDst(W, H)

            // make a ckeckerboard pattern
            val bm = Bitmap.createBitmap(
                intArrayOf(
                    -0x1, -0x333334,
                    -0x333334, -0x1
                ), 2, 2,
                Bitmap.Config.RGB_565
            )
            mBG = BitmapShader(
                bm,
                Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT
            )
            val m = Matrix()
            m.setScale(6f, 6f)
            mBG.setLocalMatrix(m)
        }
    }

    companion object {
        // create a bitmap with a circle, used for the "dst" image
        fun makeDst(w: Int, h: Int): Bitmap {
            val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val c = Canvas(bm)
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.color = -0x33bc
            c.drawOval(RectF(0f, 0f, (w * 3 / 4).toFloat(), (h * 3 / 4).toFloat()), p)
            return bm
        }

        // create a bitmap with a rect, used for the "src" image
        fun makeSrc(w: Int, h: Int): Bitmap {
            val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val c = Canvas(bm)
            val p = Paint(Paint.ANTI_ALIAS_FLAG)
            p.color = -0x995501
            c.drawRect(
                (w / 3).toFloat(),
                (h / 3).toFloat(),
                (w * 19 / 20).toFloat(),
                (h * 19 / 20).toFloat(),
                p
            )
            return bm
        }
    }
}