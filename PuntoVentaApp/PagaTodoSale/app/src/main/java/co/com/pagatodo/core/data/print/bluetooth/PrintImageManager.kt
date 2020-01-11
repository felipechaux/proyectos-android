package co.com.pagatodo.core.data.print.bluetooth

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint

class PrintImageManager {
    var canvas: Canvas? = null
    var paint: Paint? = null
    var bm: Bitmap? = null
    var width: Int = 0
    var length = 0.0f
    var bitbuf: ByteArray? = null

    fun getLength(): Int {
        return this.length.toInt() + 20
    }

    fun initCanvas(w: Int) {
        val h = 10 * w
        this.bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444)
        this.canvas = Canvas(this.bm!!)
        this.canvas?.drawColor(-1)
        this.width = w
        this.bitbuf = ByteArray(this.width / 8)
    }

    fun initPaint() {
        this.paint = Paint()
        this.paint?.isAntiAlias = true
        this.paint?.color = -16777216
        this.paint?.style = Paint.Style.STROKE
    }

    fun printImage(): ByteArray {
        val nbm = Bitmap.createBitmap(this.bm!!, 0, 0, this.width, this.getLength())
        val imgbuf = ByteArray(this.width / 8 * this.getLength() + 8)
        imgbuf[0] = 29
        imgbuf[1] = 118
        imgbuf[2] = 48
        imgbuf[3] = 0
        imgbuf[4] = (this.width / 8).toByte()
        imgbuf[5] = 0
        imgbuf[6] = (this.getLength() % 256).toByte()
        imgbuf[7] = (this.getLength() / 256).toByte()
        var s = 7

        for (i in 0 until this.getLength()) {
            var k: Int
            k = 0
            while (k < this.width / 8) {
                val c0 = nbm.getPixel(k * 8 + 0, i)
                val p0: Byte
                if (c0 == -1) {
                    p0 = 0
                } else {
                    p0 = 1
                }

                val c1 = nbm.getPixel(k * 8 + 1, i)
                val p1: Byte
                if (c1 == -1) {
                    p1 = 0
                } else {
                    p1 = 1
                }

                val c2 = nbm.getPixel(k * 8 + 2, i)
                val p2: Byte
                if (c2 == -1) {
                    p2 = 0
                } else {
                    p2 = 1
                }

                val c3 = nbm.getPixel(k * 8 + 3, i)
                val p3: Byte
                if (c3 == -1) {
                    p3 = 0
                } else {
                    p3 = 1
                }

                val c4 = nbm.getPixel(k * 8 + 4, i)
                val p4: Byte
                if (c4 == -1) {
                    p4 = 0
                } else {
                    p4 = 1
                }

                val c5 = nbm.getPixel(k * 8 + 5, i)
                val p5: Byte
                if (c5 == -1) {
                    p5 = 0
                } else {
                    p5 = 1
                }

                val c6 = nbm.getPixel(k * 8 + 6, i)
                val p6: Byte
                if (c6 == -1) {
                    p6 = 0
                } else {
                    p6 = 1
                }

                val c7 = nbm.getPixel(k * 8 + 7, i)
                val p7: Byte
                if (c7 == -1) {
                    p7 = 0
                } else {
                    p7 = 1
                }

                val value = p0 * 128 + p1 * 64 + p2 * 32 + p3 * 16 + p4 * 8 + p5 * 4 + p6 * 2 + p7.toInt()
                this.bitbuf!![k] = value.toByte()
                ++k
            }

            k = 0
            while (k < this.width / 8) {
                ++s
                imgbuf[s] = this.bitbuf!![k]
                ++k
            }
        }

        return imgbuf
    }
}
