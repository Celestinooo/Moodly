package br.edu.ifsp.moodly.model

import java.io.Serializable

data class MoodEntry(
    val id: Long = 0,
    val date: String,
    val mood: MoodState,
    val note: String? = null
) : Serializable