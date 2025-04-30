package com.task.stockmanagment.data.network

import com.task.stockmanagment.data.model.Stock
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApi {
    @GET("api/Autocomplete/GetAutocomplete")
    suspend fun getStocks(@Query("name") query: String): List<Stock>
}