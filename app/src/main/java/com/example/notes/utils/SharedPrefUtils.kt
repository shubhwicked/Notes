package com.example.notes.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient

object SharedPrefUtils {


        lateinit var sharedPrefs: SharedPreferences
        val signInCode = 100

         fun initililizeSharedpreference(context: Context){
            sharedPrefs = context.getSharedPreferences("notes_shared_prefs", Context.MODE_PRIVATE)
        }

}