package br.edu.ifsp.moodly.model

import java.io.Serializable

enum class MoodState(val code: Int) : Serializable {
    TRISTE(0),
    ESTRESSADO(1),
    NORMAL(2),
    BEM(3),
    FELIZ(4);

    companion object {
        fun fromCode(code: Int): MoodState {
            return MoodState.entries.firstOrNull { it.code == code } ?: NORMAL
        }
    }
}