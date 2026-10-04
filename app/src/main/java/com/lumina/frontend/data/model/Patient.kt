package com.lumina.frontend.data.model


data class Patient(
    val id: Int,
    val name: String,
    val age: Int,
    val room: String,
    val condition: String,
    val status: String
)