package com.example.mitihani_app


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun WelcomeScreen(onStartQuizClicked: () -> Unit) {
    val textSizeWelcom by remember { mutableStateOf(30.sp) } // Initial text size
    val textSizeWelcomeButton by remember { mutableStateOf(20.sp) } // Initial text size
    Image(
        painter = painterResource(id = R.drawable.app_image_3),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxSize()
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Karibuni App za Mitihani!", fontSize = textSizeWelcom)
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onStartQuizClicked,
            shape = RoundedCornerShape(topStart = 10.dp, bottomEnd = 20.dp),
            modifier = Modifier.padding(top = 200.dp)
        ) {
            Text(text = "Endelee\n\nContinue", fontSize = textSizeWelcomeButton)
        }

    }
}


@Composable
fun QuizSelectionScreen(quizzes: List<Quiz>, onQuizSelected: (Quiz) -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.app_image1),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier.fillMaxSize()
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        quizzes.forEach { quiz ->
            Button(
                onClick = { onQuizSelected(quiz) },
                modifier = Modifier.padding(8.dp),
                shape = RoundedCornerShape(topStart = 10.dp, bottomEnd = 20.dp)
            ) {
                Text(text = quiz.title)
            }
        }
    }
}

@Composable
fun QuizScreen(
    navController: NavController,
    quiz: Quiz,
    onCompleteQuiz: (Int, Int) -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    val totalQuestions = quiz.questions.size
    var showScore by remember { mutableStateOf(false) }
    var scoreColor by remember { mutableStateOf(Color.White) }
    var textSize by remember { mutableStateOf(16.sp) } // Initial text size
    val textSizeTwo by remember { mutableStateOf(20.sp) } // Initial text size
    val selectedOptionIndex = remember { mutableStateOf(-1) } // Selected option index, initialized to -1
    var timeLeft by remember { mutableStateOf(15) } // 15 seconds timer

    val coroutineScope = rememberCoroutineScope() // Remember the coroutine scope

    // Declare a timer job variable
    var timerJob: Job? by remember { mutableStateOf(null) }

    // Shuffle both questions and options while preserving correct answer indices
    val shuffledQuiz = remember {
        quiz.copy(questions = quiz.questions.shuffled().map { question ->
            val shuffledOptions = question.options.shuffled()
            val correctAnswerIndex = question.correctAnswerIndex?.let { originalIndex ->
                shuffledOptions.indexOfFirst { it == question.options[originalIndex] }
            } ?: -1 // Default value in case correctAnswerIndex is null
            question.copy(options = shuffledOptions, correctAnswerIndex = correctAnswerIndex)
        })
    }

    // Modify the startTimer function to cancel the existing job before starting a new one
    fun startTimer() {
        timeLeft = 15
        timerJob?.cancel() // Cancel the existing timer job if it's not null
        timerJob = coroutineScope.launch {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            // Timer expired, mark the question as wrong
            if (selectedOptionIndex.value == -1) {
                selectedOptionIndex.value = 0 // Mark the first option as selected (could be any)
            }
        }
    }

    // Update the DisposableEffect to cancel the timer job when the composable is disposed
    DisposableEffect(currentQuestionIndex) {
        startTimer()
        onDispose {
            timerJob?.cancel() // Cancel the timer job when the composable is disposed
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val currentQuestion = shuffledQuiz.questions[currentQuestionIndex]

        Text(text = currentQuestion.text, fontSize = textSizeTwo)

        currentQuestion.options.forEachIndexed { optionIndex, option ->
            Button(
                onClick = {
                    selectedOptionIndex.value = optionIndex
                },
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .background(
                        if (optionIndex == selectedOptionIndex.value) Color.Gray else Color.White,
                        shape = RoundedCornerShape(topStart = 10.dp, bottomEnd = 20.dp)
                    )
            ) {
                Text(text = option)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (selectedOptionIndex.value == -1) {
                    // No option selected, mark the question as wrong
                    selectedOptionIndex.value = 0 // Mark the first option as selected (could be any)
                }

                if (selectedOptionIndex.value == currentQuestion.correctAnswerIndex) {
                    score++
                }

                selectedOptionIndex.value = -1 // Reset selected option index

                if (currentQuestionIndex < totalQuestions - 1) {
                    currentQuestionIndex++
                    startTimer() // Start the timer for the next question
                } else {
                    showScore = true // Show the score when reaching the last question
                    scoreColor = if (score >= 7) Color.Green else Color.Red // Set color based on score
                    textSize = 24.sp // Set text size to a larger value
                }
            },
            enabled = selectedOptionIndex.value != -1, // Enable button only if an option is selected,
            shape= RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "Next",
                color = Color.White
            )
        }

        if (showScore) {
            Text(
                text = "Alama yako ni: $score/$totalQuestions",
                color = scoreColor,
                fontSize = textSize,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = if (score >= 7) "Hongera! Umefaulu" else "Upaswe kusoma zaidi!!",
                fontSize = textSizeTwo,
                color = scoreColor,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Navigate back to the quiz selection screen
                    navController.navigate("quizSelection")
                },
                shape= RoundedCornerShape(10.dp)
            ) {
                Text(text = "Return to Quiz Selection")
            }
        }

        Text(
            text = "Time Left: $timeLeft seconds",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}














