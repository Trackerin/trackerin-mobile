package com.example.trackerinmobile.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secret_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun saveUserName(name: String) {
        sharedPreferences.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, null)
    }

    fun saveOccupation(occupation: String) {
        sharedPreferences.edit().putString(KEY_OCCUPATION, occupation).apply()
    }

    fun getOccupation(): String? {
        return sharedPreferences.getString(KEY_OCCUPATION, "College Student, 4th Semester")
    }

    fun saveSpecialization(spec: String) {
        sharedPreferences.edit().putString(KEY_SPECIALIZATION, spec).apply()
    }

    fun getSpecialization(): String? {
        return sharedPreferences.getString(KEY_SPECIALIZATION, "Fullstack Developer")
    }

    fun setQuizCompleted(milestoneId: Int, isCompleted: Boolean) {
        sharedPreferences.edit().putBoolean("quiz_completed_$milestoneId", isCompleted).apply()
    }

    fun isQuizCompleted(milestoneId: Int): Boolean {
        return sharedPreferences.getBoolean("quiz_completed_$milestoneId", false)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).remove(KEY_USER_NAME)
            .remove(KEY_OCCUPATION).remove(KEY_SPECIALIZATION).apply()
    }

    fun getRecentSearches(): List<String> {
        val raw = sharedPreferences.getString("recent_searches", null)
        if (raw == null) {
            return listOf("Python Crash Course", "Agile Methodologies", "Figma Prototyping")
        }
        return raw.split("|||").filter { it.isNotEmpty() }
    }

    fun saveRecentSearch(search: String) {
        val current = getRecentSearches().toMutableList()
        current.remove(search)
        current.add(0, search)
        val limited = current.take(5)
        val raw = limited.joinToString("|||")
        sharedPreferences.edit().putString("recent_searches", raw).apply()
    }


    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_OCCUPATION = "user_occupation"
        private const val KEY_SPECIALIZATION = "user_specialization"
    }
}

