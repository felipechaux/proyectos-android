package com.sbpinilla.consultactivity

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.sbpinilla.consultactivity.data.database.AppDatabase

class AppApplication:Application(){

    override fun onCreate() {
        super.onCreate()


        instance = this

    }

    companion object {
        private lateinit var instance: AppApplication
        private var roomInstance: AppDatabase? = null

        fun getInstance(): AppApplication {
            return instance
        }

        fun getAppContext(): Context {
            return instance.applicationContext
        }

        fun getDatabaseInstance(): AppDatabase? {
            if (roomInstance == null) {
                roomInstance = Room.databaseBuilder( instance,
                    AppDatabase::class.java,
                    "app-database"
                ).allowMainThreadQueries().build()
            }
            return roomInstance
        }
    }

}