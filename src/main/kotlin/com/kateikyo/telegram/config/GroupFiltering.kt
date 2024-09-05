package com.kateikyo.telegram.config

data class GroupFiltering(
    var disable: Boolean = false,
    var groupIdsToReplyTo: List<Long> = emptyList()
)