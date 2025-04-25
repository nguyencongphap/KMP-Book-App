package com.plcoding.bookpedia.app

import kotlinx.serialization.Serializable

sealed interface Route {

    // bundle each feature into a navigation graph
    @Serializable
    data object BookGraph: Route

    // Serializable because compose navigation will figure out a way to serialize this and pass it
    // to a screen
    @Serializable
    data object BookList: Route // our BookList screen doesn't have any screen arguments

    // A screen that has screen arguments must be a data class
    // id is the id of the book we click on
    @Serializable
    data class BookDetail(val id: String): Route
}