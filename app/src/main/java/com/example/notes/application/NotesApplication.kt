package com.example.notes.application

import android.app.Application
import com.example.notes.utils.SharedPrefUtils

class NotesApplication:Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPrefUtils.initililizeSharedpreference(this)
    }
}