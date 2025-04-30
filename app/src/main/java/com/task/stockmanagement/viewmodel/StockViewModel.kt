package com.task.stockmanagement.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.stockmanagement.data.model.Stock
import com.task.stockmanagement.data.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class StockViewModel @Inject constructor(
    private val repository: StockRepository
) : ViewModel() {

    // the rows of cards shown in the UI after a search is triggered
    var stockList by mutableStateOf<List<Stock>>(emptyList())
        private set

    // the temporary autocomplete options shown in the dropdown while the user types
    var options by mutableStateOf<List<Stock>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    private var queryJob: Job? = null

    fun onSearchQueryChanged(newQuery: String) {
        searchQuery = newQuery
        queryJob?.cancel() // if there was a previous debounce coroutine running, cancel it
        queryJob = viewModelScope.launch {
            delay(300) // debounce duration
            options = if (newQuery.isNotBlank()) {
                try {
                    withContext(Dispatchers.IO) {
                        repository.fetchStocks(newQuery)
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e // propagate it properly
                    errorMessage = e.localizedMessage ?: "Unknown error"
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    fun loadOptions(stock: Stock) {
        searchQuery = stock.value
        loadStocks(stock.value)
        options = emptyList()
    }

    fun loadStocks(query: String) {
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