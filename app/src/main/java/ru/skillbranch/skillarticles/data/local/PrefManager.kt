package ru.skillbranch.skillarticles.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

//@SuppressLint("RestrictedApi")
class PrefManager(context: Context){
    internal val preferences: SharedPreferences by lazy  { PreferenceManager(context).sharedPreferences }

    //var storedBoolean by PrefDelegate(false)

}