package com.paisaflow.app.data.local

import android.content.Context
import com.paisaflow.app.i18n.AppLanguage

/** Tiny persisted settings (language, first-run). No secrets here. */
class Prefs(context: Context) {
    private val sp = context.applicationContext.getSharedPreferences("paisaflow", Context.MODE_PRIVATE)
    var language: AppLanguage
        
        set(v) = sp.edit().putString("lang", v.code).apply()
    var onboarded: Boolean

        set(v) = sp.edit().putBoolean("onboarded", v).apply()
}
