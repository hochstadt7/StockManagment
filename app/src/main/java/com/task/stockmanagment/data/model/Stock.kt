package com.task.stockmanagment.data.model

data class Stock(
    val label: String,
    val uid: String,
    val value: String,
    val category: String,
    val ticker: String? = null
)