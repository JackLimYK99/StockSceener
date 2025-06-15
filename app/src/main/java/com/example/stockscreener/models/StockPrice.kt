package com.example.stockscreener.models

import com.google.gson.annotations.SerializedName

data class StockPrice(
    @SerializedName("current_price")
    val currentPrice: StockCurrentPrice,

    @SerializedName("price_change")
    val priceChange: Double,

    @SerializedName("percentage_change")
    val percentageChange: Double
)
