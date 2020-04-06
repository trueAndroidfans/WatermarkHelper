package com.aokiji.watermarkhelper

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.tencent.mmkv.MMKV

class App : Application() {

    companion object {
        private lateinit var instance: App

        fun getInstance(): App = instance
    }


    override fun onCreate() {
        super.onCreate()

        instance = this

        init()
    }


    private fun init() {
        Logger.addLogAdapter(AndroidLogAdapter())

        MMKV.initialize(this)
    }
}