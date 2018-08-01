package com.code.chaux.mibot

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.net.URL
import  android.util.Log;

public   class DownLoadImageTask(internal val url: String) : AsyncTask<String, Void, Bitmap>() {
    override fun doInBackground(vararg urls: String): Bitmap {
       // val urlOfImage = urls[0]

            val inputStream = URL(url).openConnection().getInputStream()
            return BitmapFactory.decodeStream(inputStream)

    }
    override fun onPostExecute(result: Bitmap)  {
        if(result!=null){
            Log.e("tag","todo salio bieen ")
            // Display the downloaded image into image view
          //  Toast.makeText(imageViews.context,"download success",Toast.LENGTH_SHORT).show()

           // imageViews.setImageBitmap(result)
        }else{
            Log.e("tag","algo salio mal ")
           // Toast.makeText(imageViews.context,"Error downloading",Toast.LENGTH_SHORT).show()
        }
    }

    interface OnBitmapDownloadedListener {
        fun setBitmap(url: String):Bitmap
    }
}