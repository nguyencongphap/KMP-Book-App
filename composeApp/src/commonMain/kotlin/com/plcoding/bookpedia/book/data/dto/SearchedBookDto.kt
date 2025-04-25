package com.plcoding.bookpedia.book.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchedBookDto(
    @SerialName("key")
    val id: String = "",
    @SerialName("title")
    val title: String = "",
    @SerialName("language")
    val languages: List<String> = listOf(),
    @SerialName("cover_i")
    val coverAlternativeKey: Int = 0,
    @SerialName("author_key")
    val authorKeys: List<String> = listOf(),
    @SerialName("author_name")
    val authorNames: List<String> = listOf(),
    @SerialName("cover_edition_key")
    val coverKey: String = "",
    @SerialName("first_publish_year")
    val firstPublishYear: Int = 0,
    @SerialName("ratings_average")
    val ratingsAverage: Double = 0.0,
    @SerialName("ratings_count")
    val ratingsCount: Int = 0,
    @SerialName("number_of_pages_median")
    val numPagesMedian: Int = 0,
    @SerialName("edition_count")
    val numEditions: Int = 0,
)