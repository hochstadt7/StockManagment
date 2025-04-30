package com.task.stockmanagment.data.repository

import com.task.stockmanagment.data.model.Stock
import com.task.stockmanagment.data.network.StockApi

class StockRepository(private val api: StockApi) {
    suspend fun fetchStocks(query: String): List<Stock> {
        return api.getStocks(query)
            .sortedBy { it.label }
            .take(9)
    }
}