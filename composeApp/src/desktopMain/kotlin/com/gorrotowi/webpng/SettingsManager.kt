package com.gorrotowi.webpng

import java.util.prefs.Preferences

object SettingsManager {
    private val prefs = Preferences.userNodeForPackage(SettingsManager::class.java)
    private const val KEY_SAVE_PATH = "save_path"

    fun getSavePath(): String? {
        return prefs.get(KEY_SAVE_PATH, null)
    }

    fun setSavePath(path: String) {
        prefs.put(KEY_SAVE_PATH, path)
    }
}
