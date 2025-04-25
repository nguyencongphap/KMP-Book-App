package com.plcoding.bookpedia.core.presentation

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


// This allows us to make it either a DynamicString (a normal string) or wrap it with
// StringResourceId (string resources that allow us to translate our app into different
// languages. The thing is, we can only unwrap string resource IDs in our UI. We look up
// which specific string is behind a certain ID, but we still might want to create strings
// based on string resource IDs from within our ViewModel where we wouldn't be able to unwrap
// these. This UiText class solves that problem. It makes it convenient to work with
// StringResourceId in places other than UI
sealed interface UiText {
    data class DynamicString(val value: String): UiText
    class StringResourceId(
        val id: StringResource,
        val args: Array<Any> = arrayOf()
    ): UiText

    @Composable
    fun asString(): String {
        return when(this) {
            is DynamicString -> value
            is StringResourceId -> stringResource(resource = id, formatArgs = args)
        }
    }
}