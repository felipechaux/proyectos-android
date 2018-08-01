package com.code.chaux.mibot

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.github.bassaer.chatmessageview.model.ChatUser
import com.github.bassaer.chatmessageview.model.Message
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.core.FuelManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import  android.util.Log;
import android.text.Html
import java.net.URL


class MainActivity : Activity()  {

    companion object {
        private const val ACCESS_TOKEN = "7a13facd828c41f181af2f415899a98d"
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FuelManager.instance.baseHeaders = mapOf(
                "Authorization" to "Bearer $ACCESS_TOKEN"
        )

        FuelManager.instance.basePath =
                "https://api.dialogflow.com/v1/"

        FuelManager.instance.baseParams = listOf(
                "v" to "20170712",                  // latest protocol
                "sessionId" to UUID.randomUUID(),   // random ID
                "lang" to "es"                      // English language
        )

        val human = ChatUser(
                1,
                "Yo",
                BitmapFactory.decodeResource(resources,
                        R.drawable.alien)
        )

        val agent = ChatUser(
                2,
                "Agente",
                BitmapFactory.decodeResource(resources,
                        R.drawable.chaticon)
        )

        my_chat_view.setOnClickSendButtonListener(View.OnClickListener {
            my_chat_view.send(Message.Builder()
                    .setUser(human)
                    .setText(my_chat_view.inputText)
                    .build()
            )

            Fuel.get("/query",
                    listOf("query" to my_chat_view.inputText))
                    .responseJson { _, _, result ->
                        val reply = result.get().obj()
                                .getJSONObject("result")
                                .getJSONObject("fulfillment")
                                .getString("speech")



                        val picture = BitmapFactory.decodeResource(resources, R.drawable.giphy)

                        Log.e("tag","message prueba json "+result.get().content)
                        Log.e("tag","message prueba json22 ")

                        if(result.get().obj().getJSONObject("result").getJSONObject("fulfillment").getString("messages").contains("image")){

                            Log.e("tag","si contiene url image y es : "+result.get().obj().getJSONObject("result").getJSONObject("fulfillment").getJSONArray("messages"))

                            val url=result.get().obj().getJSONObject("result").getJSONObject("fulfillment").getJSONArray("messages").getJSONObject(0).getJSONObject("image").getString("url")

                            val textImage=result.get().obj().getJSONObject("result").getJSONObject("fulfillment").getJSONArray("messages").getJSONObject(0).getString("formattedText")

                            Log.e("tag","texto imagen --> "+textImage)

                            Log.e("tag","URL --> "+url)

                            my_chat_view.send(Message.Builder()
                                    .setRight(true)
                                    .setUser(agent)
                                    .setText(textImage)
                                    .build()
                            )
                            my_chat_view.send(Message.Builder()
                                    .setRight(true)
                                    .setUser(agent)
                                    .setPicture(DownLoadImageTask(url).execute().get())
                                    .setType(Message.Type.PICTURE)
                                    .build()

                            )

                        }

                        else
                        {

                            my_chat_view.send(Message.Builder()
                                    .setRight(true)
                                    .setUser(agent)
                                    .setText(reply)
                                    .build()
                            )

                            Log.e("tag","NO contiene url image ")
                        }
                    }


        })

    }




}
