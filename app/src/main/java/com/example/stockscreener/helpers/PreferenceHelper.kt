package com.example.stockscreener.helpers

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {
    private const val PREF_NAME = "stock_pref"
    private const val KEY_FAVORITES = "favorites"

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private fun getFavorite(context: Context): MutableSet<String> =
        getPrefs(context).getStringSet(KEY_FAVORITES, mutableSetOf()) ?: mutableSetOf()

    fun toggleFavorite(context: Context, stockId: String) {
        val prefs = getPrefs(context)
        val favorites = getFavorite(context)
        if (favorites.contains(stockId)) {
            favorites.remove(stockId)
        } else {
            favorites.add(stockId)
        }
        prefs.edit().putStringSet(KEY_FAVORITES, favorites).apply()
    }

    fun isFavorite(context: Context, stockId: String): Boolean =
        getFavorite(context).contains(stockId)
}
