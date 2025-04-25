package com.plcoding.bookpedia.book.data.dto

import kotlinx.serialization.Serializable

@Serializable(
    // need to use a custom serializer because the Api can return description as either a string
    // or an object
    with = BookWorkDtoSerializer::class
)
data class BookWorkDto(
    val description: String? = null
)