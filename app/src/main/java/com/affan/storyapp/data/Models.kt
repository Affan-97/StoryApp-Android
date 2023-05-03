package com.affan.storyapp.data

data class UserModel(
    val name: String?,
    val token: String?,
    val id: String?
)
data class MarkerData(
    val latitude: Double?,
    val longitude: Double?,
    val addressName: String?
)