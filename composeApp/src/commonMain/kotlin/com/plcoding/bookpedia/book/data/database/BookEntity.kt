package com.plcoding.bookpedia.book.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Similar to Dto but for local db

@Entity // so that Room knows that this a an entity, a table
class BookEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val title: String,
    val description: String?,
    val imageUrl: String,
    // For simplicity we will convert list this into json string and store it into the db
    // instead of making a new table for the list.
    // When we fetch them, we parse them into list again
    val languages: List<String>,
    val authors: List<String>,
    val firstPublishYear: String?,
    val ratingsAverage: Double?,
    val ratingsCount: Int?,
    val numPagesMedian: Int?,
    val numEditions: Int
)