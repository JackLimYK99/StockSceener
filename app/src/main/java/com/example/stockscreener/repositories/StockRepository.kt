package com.example.stockscreener.repositories

import android.content.Context
import com.example.stockscreener.models.Stock
import com.example.stockscreener.models.StockResponse
import com.google.gson.Gson
import java.io.InputStreamReader

class StockRepository(private val context: Context) {
    private val allStocks: List<Stock> by lazy {
        loadStocksFromAssets()
    }

    private fun loadStocksFromAssets(): List<Stock> {
        return try {
            val inputStream = context.assets.open("stocks.json")
            val reader = InputStreamReader(inputStream)
            val stockResponse = Gson().fromJson(reader, StockResponse::class.java)
            reader.close()
            stockResponse.stocks
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun loadMore(offset: Int, limit: Int): List<Stock> {
        return allStocks.drop(offset).take(limit)
    }

    fun filterByName(query: String): List<Stock> {
        return if (query.isBlank()) allStocks
        else allStocks.filter { it.name.contains(query, ignoreCase = true) }
    }
}
