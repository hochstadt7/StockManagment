package com.task.stockmanagement.data.repository

import com.task.stockmanagement.data.model.Stock
import com.task.stockmanagement.data.network.StockApi

class StockRepository(private val api: StockApi) {
    suspend fun fetchStocks(query: String): List<Stock> {
        return api.getStocks(query).sortedBy { it.label }.take(9)
    }
}