package com.example.trackerinmobile.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

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
        return sharedPreferences.getString(KEY_OCCUPATION, null)
    }

    fun saveSpecialization(spec: String) {
        sharedPreferences.edit().putString(KEY_SPECIALIZATION, spec).apply()
    }

    fun getSpecialization(): String? {
        return sharedPreferences.getString(KEY_SPECIALIZATION, null)
    }

    fun setQuizCompleted(milestoneId: Int, isCompleted: Boolean) {
        sharedPreferences.edit().putBoolean("quiz_completed_$milestoneId", isCompleted).apply()
    }

    fun isQuizCompleted(milestoneId: Int): Boolean {
        return sharedPreferences.getBoolean("quiz_completed_$milestoneId", false)
    }

    fun clearToken() {
        sharedPreferences.edit().clear().apply()
    }

    fun getRecentSearches(): List<String> {
        val raw = sharedPreferences.getString("recent_searches", null)
        if (raw == null) {
            return emptyList()
        }
        return raw.split("|||").filter { it.isNotEmpty() }
    }

    fun clearRecentSearches() {
        sharedPreferences.edit().remove("recent_searches").apply()
    }

    fun saveRecentSearch(search: String) {
        val current = getRecentSearches().toMutableList()
        current.remove(search)
        current.add(0, search)
        val limited = current.take(5)
        val raw = limited.joinToString("|||")
        sharedPreferences.edit().putString("recent_searches", raw).apply()
    }

    fun updateStreakAndActiveDays() {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = sdf.format(Date())
        
        val lastActive = sharedPreferences.getString("last_active_date", null)
        val activeDates = sharedPreferences.getStringSet("active_dates", mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        
        if (!activeDates.contains(todayStr)) {
            activeDates.add(todayStr)
            sharedPreferences.edit().putStringSet("active_dates", activeDates).apply()
        }

        if (lastActive == null) {
            sharedPreferences.edit()
                .putInt("current_streak", 1)
                .putString("last_active_date", todayStr)
                .apply()
            return
        }

        if (lastActive == todayStr) {
            return
        }

        try {
            val lastDate = sdf.parse(lastActive)
            val cal = Calendar.getInstance()
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = sdf.format(cal.time)

            if (lastActive == yesterdayStr) {
                val currentStreak = sharedPreferences.getInt("current_streak", 0)
                sharedPreferences.edit()
                    .putInt("current_streak", currentStreak + 1)
                    .putString("last_active_date", todayStr)
                    .apply()
            } else {
                sharedPreferences.edit()
                    .putInt("current_streak", 1)
                    .putString("last_active_date", todayStr)
                    .apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentStreak(): Int {
        return sharedPreferences.getInt("current_streak", 1)
    }

    fun getActiveDaysCount(): Int {
        val activeDates = sharedPreferences.getStringSet("active_dates", null)
        return activeDates?.size ?: 1
    }

    private fun getDayIndex(day: String): Int {
        return when (day) {
            "MON" -> 1
            "TUE" -> 2
            "WED" -> 3
            "THU" -> 4
            "FRI" -> 5
            "SAT" -> 6
            "SUN" -> 7
            else -> 8
        }
    }

    fun checkAndResetWeeklyActivity() {
        val cal = Calendar.getInstance()
        val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)
        val currentYear = cal.get(Calendar.YEAR)
        val storedWeek = sharedPreferences.getInt("stored_week_of_year", -1)
        val storedYear = sharedPreferences.getInt("stored_year", -1)

        if (storedWeek != currentWeek || storedYear != currentYear) {
            // Reset all weekday activities
            sharedPreferences.edit()
                .putInt("stored_week_of_year", currentWeek)
                .putInt("stored_year", currentYear)
                .putFloat("activity_MON", -1f) // using -1f to denote "not set yet this week"
                .putFloat("activity_TUE", -1f)
                .putFloat("activity_WED", -1f)
                .putFloat("activity_THU", -1f)
                .putFloat("activity_FRI", -1f)
                .putFloat("activity_SAT", -1f)
                .putFloat("activity_SUN", -1f)
                .apply()
        }
    }

    fun incrementDailyActivity(amount: Float = 20f) {
        checkAndResetWeeklyActivity()
        val dayOfWeekStr = SimpleDateFormat("EEE", Locale.US).format(Date()).uppercase()
        val key = "activity_$dayOfWeekStr"
        val current = sharedPreferences.getFloat(key, -1f)
        val base = if (current < 0f) 0f else current
        val newValue = (base + amount).coerceAtMost(100f)
        sharedPreferences.edit().putFloat(key, newValue).apply()
    }

    fun getDailyActivity(day: String): Float {
        checkAndResetWeeklyActivity()
        
        // Find current day of the week
        val currentDayStr = SimpleDateFormat("EEE", Locale.US).format(Date()).uppercase()
        val currentDayIndex = getDayIndex(currentDayStr)
        val targetDayIndex = getDayIndex(day)
        
        // If it's a future day this week, it should have 0f activity
        if (targetDayIndex > currentDayIndex) {
            return 0f
        }
        
        val value = sharedPreferences.getFloat("activity_$day", -1f)
        if (value >= 0f) {
            return value
        }
        
        return 0f
    }
    fun saveEmail(email: String) {
        sharedPreferences.edit().putString(KEY_EMAIL, email).apply()
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, "user@example.com")
    }

    fun savePassword(password: String) {
        sharedPreferences.edit().putString(KEY_PASSWORD, password).apply()
    }

    fun getPassword(): String? {
        return sharedPreferences.getString(KEY_PASSWORD, "password123")
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_OCCUPATION = "user_occupation"
        private const val KEY_SPECIALIZATION = "user_specialization"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_PASSWORD = "user_password"
    }
}

