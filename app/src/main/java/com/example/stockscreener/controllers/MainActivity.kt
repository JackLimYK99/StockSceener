package com.example.stockscreener.controllers

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockscreener.R
import com.example.stockscreener.models.Stock
import com.example.stockscreener.repositories.StockRepository
import com.example.stockscreener.views.StockAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {
    private lateinit var tilSearch: TextInputLayout
    private lateinit var etSearch: TextInputEditText
    private lateinit var rvStock: RecyclerView
    private lateinit var tvVersion: TextView
    private lateinit var adapter: StockAdapter
    private lateinit var repository: StockRepository

    private var isSearchEmpty = true
    private var isLoading = false
    private var hasMoreData = true
    private var dataOffset = 0
    private val INITIAL_LOAD = 10
    private val PAGE_SIZE = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        repository = StockRepository(applicationContext)

        findViews()
        setProperties()
    }

    private fun findViews() {
        tilSearch = findViewById(R.id.tilSearch)
        etSearch = findViewById(R.id.etSearch)
        rvStock = findViewById(R.id.rvStock)
        tvVersion = findViewById(R.id.tvVersion)
    }

    private fun setProperties() {
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        tvVersion.text = getString(R.string.version, versionName)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                val filtered = repository.filterByName(query)

                adapter.setItems(filtered)
                hasMoreData = query.isBlank()

                if (query.isNotEmpty() && isSearchEmpty) {
                    tilSearch.endIconDrawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_clear)
                    isSearchEmpty = false
                } else if (query.isEmpty() && !isSearchEmpty) {
                    tilSearch.endIconDrawable = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_search)
                    isSearchEmpty = true
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        tilSearch.setEndIconOnClickListener {
            if (!isSearchEmpty) {
                etSearch.setText("")
                adapter.setItems(repository.filterByName(""))
                hasMoreData = true
                tilSearch.endIconDrawable = ContextCompat.getDrawable(this, R.drawable.ic_search)
                isSearchEmpty = true
            }
        }

        adapter = StockAdapter(this, mutableListOf())
        rvStock.layoutManager = LinearLayoutManager(this)
        rvStock.adapter = adapter
        loadMore(INITIAL_LOAD)

        rvStock.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (!rv.canScrollVertically(1) && !isLoading && hasMoreData) {
                    loadMore(PAGE_SIZE)
                }
            }
        })
    }

    private fun loadMore(limit: Int) {
        isLoading = true
        val newItems: List<Stock> = repository.loadMore(dataOffset, limit)

        if (newItems.isNotEmpty()) {
            rvStock.post {
                adapter.addMoreItems(newItems)
                dataOffset += newItems.size
                isLoading = false
            }
        } else {
            hasMoreData = false
            isLoading = false
        }
    }
}
