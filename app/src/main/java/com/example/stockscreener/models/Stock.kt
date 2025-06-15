package com.example.stockscreener.models

import com.google.gson.annotations.SerializedName

data class Stock (
    val id: Int,
    val symbol: String,
    val name: String,

    @SerializedName("logo_url")
    val logoUrl: String,

    @SerializedName("stock_price")
    val stockPrice: StockPrice
)
