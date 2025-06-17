package com.projects.aware.main

import android.app.Application
import com.projects.aware.data.AppContainer
import com.projects.aware.data.DefaultAppContainer
import com.projects.aware.data.model.MyObjectBox
import io.objectbox.BoxStore

class AwareApp: Application() {

    lateinit var boxStore: BoxStore
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        boxStore = MyObjectBox.builder()
            .androidContext(this)
            .build()

        container = DefaultAppContainer(applicationContext)
    }
}