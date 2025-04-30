package com.task.stockmanagement.data.model

data class Stock(
    val label: String,
    val ticker: String? = null,
    val value: String,
    val category: String,
    val uid: String
)