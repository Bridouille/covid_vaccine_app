package com.covid.vaccination.prefs

import android.content.Context
import android.text.format.DateUtils
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class PrefHelper @Inject constructor(
    @ApplicationContext private val ctx: Context
) {
    companion object {
        private const val PREF_NAME = "prefs"

        private const val LAST_REFRESH = "last_refresh"
    }

    private val prefs = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getLastRefreshDate() = prefs.getLong(LAST_REFRESH, -1).takeIf { it != -1L }
    fun setLastRefresh(timestamp: Long = Date().time) = prefs.edit { putLong(LAST_REFRESH, timestamp) }
    fun lastRefreshMoreThanADayAgo(): Boolean {
        val lastRefresh = getLastRefreshDate() ?: return true

        return (Date().time - lastRefresh) >= DateUtils.DAY_IN_MILLIS
    }
}