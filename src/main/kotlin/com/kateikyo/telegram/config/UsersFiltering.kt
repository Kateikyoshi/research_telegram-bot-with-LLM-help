package com.kateikyo.telegram.config

data class UsersFiltering(
    var disable: Boolean = false,
    var usersToModerate: List<String> = emptyList()
)