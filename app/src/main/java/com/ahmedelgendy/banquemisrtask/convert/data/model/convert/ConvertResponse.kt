package com.ahmedelgendy.banquemisrtask.convert.data.model.convert

data class ConvertResponse(
    val date: String? = null,
    val historical: String? = null,
    val info: Info? = null,
    val query: Query? = null,
    val result: Double? = null,
    val success: Boolean? = null
)