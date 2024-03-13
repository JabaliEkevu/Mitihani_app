package com.example.mitihani_app

data class Quiz(
    val title: String,
    val questions: List<Question>
)

data class Question(
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    var selectedOptionIndex: Int = -1 // Default value to indicate no option selected
)