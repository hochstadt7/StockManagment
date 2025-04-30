package com.task.stockmanagment.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.stockmanagment.data.model.Stock
import com.task.stockmanagment.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    // the rows of cards shown in the UI after a search is triggered
    var stockList by mutableStateOf<List<Stock>>(emptyList())
        private set

    // the temporary autocomplete suggestions shown in the dropdown while the user types
    var suggestions by mutableStateOf<List<Stock>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    private var searchJob: Job? = null

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery = newQuery
        searchJob?.cancel() // if there was a previous debounce coroutine running, cancel it
        searchJob = viewModelScope.launch {
            delay(300) // debounce duration
            suggestions = if (newQuery.isNotBlank()) {
                try {
                    withContext(Dispatchers.IO) {
                        repository.fetchStocks(newQuery)
                    }
                } catch (e: Exception) {
                    errorMessage = e.localizedMessage ?: "Unknown error"
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    fun loadSuggestions(stock: Stock) {
        searchQuery = stock.value
        loadStocks(stock.value)
        suggestions = emptyList()
    }

    private fun loadStocks(query: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.fetchStocks(query)
                }
                stockList = result
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Unknown error"
            }
            isLoading = false
        }
    }
}