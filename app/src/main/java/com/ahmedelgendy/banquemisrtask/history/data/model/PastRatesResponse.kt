package com.ahmedelgendy.banquemisrtask.history.data.model

data class PastRatesResponse(
    val base: String? = "",
    val end_date: String? = "",
    val rates: HashMap<String, String>? = null,
    val start_date: String? = "",
    val success: Boolean? = false,
    val timeseries: Boolean? = false
)