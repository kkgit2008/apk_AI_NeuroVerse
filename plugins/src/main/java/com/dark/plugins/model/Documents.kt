package com.dark.plugins.model

import kotlinx.serialization.Serializable

@Serializable
data class Document(
    val path: String,
    val name: String,
    val content: String,
    val type: String
)