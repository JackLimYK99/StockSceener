package com.example.stockscreener.views

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stockscreener.R
import com.example.stockscreener.helpers.PreferenceHelper
import com.example.stockscreener.models.Stock
import java.text.DecimalFormat

class StockAdapter(
    private val context: Context,
    private val stocks: MutableList<Stock>
) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    private val formatter = DecimalFormat("0.00")

    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
        val clCard: ConstraintLayout = itemView.findViewById(R.id.clCard)
        val ivLogo: ImageView = itemView.findViewById(R.id.ivLogo)
        val tvCompany: TextView = itemView.findViewById(R.id.tvCompany)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvChange: TextView = itemView.findViewById(R.id.tvChange)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val stock = stocks[position]
        val stockPrice = stock.stockPrice
        val currentPrice = stockPrice.currentPrice
        val strPercentage = (if (stockPrice.percentageChange > 0) "+" else "") + formatter.format(stockPrice.percentageChange)
        val strPrice = (if (stockPrice.priceChange > 0) "+" else "") + formatter.format(stockPrice.priceChange)

        holder.clCard.setBackgroundColor(
            if (stockPrice.priceChange > 0) context.getColor(R.color.green)
            else context.getColor(R.color.red)
        )

        Glide.with(context).load(stock.logoUrl).placeholder(R.drawable.bg_logo_holder).error(R.drawable.bg_logo_holder).into(holder.ivLogo)
        holder.tvCompany.text = context.getString(R.string.company, stock.symbol, stock.name)
        holder.tvPrice.text = context.getString(R.string.price, currentPrice.currency, currentPrice.amount)
        holder.tvChange.text = context.getString(R.string.change, strPrice, strPercentage)

        val isFav = PreferenceHelper.isFavorite(context, stock.id.toString())
        holder.ivFavorite.setImageResource(if (isFav) R.drawable.ic_favorite else R.drawable.ic_favorite_border)

        holder.ivFavorite.setOnClickListener {
            PreferenceHelper.toggleFavorite(context, stock.id.toString())
            sortItems()
        }
    }

    override fun getItemCount(): Int = stocks.size

    fun addMoreItems(newItems: List<Stock>) {
        stocks.addAll(newItems)
        sortItems()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: List<Stock>) {
        stocks.clear()
        stocks.addAll(newItems)
        sortItems()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sortItems() {
        stocks.sortByDescending { stock ->
            PreferenceHelper.isFavorite(context, stock.id.toString())
        }
        notifyDataSetChanged()
    }
}
